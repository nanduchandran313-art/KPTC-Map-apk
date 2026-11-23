package com.example.data

import com.example.model.BusDirection
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import kotlin.math.abs
import kotlin.math.roundToInt

data class TripLeg(
    val route: BusRoute,
    val direction: BusDirection,
    val fromStop: BusStop,
    val toStop: BusStop,
    val intermediateStops: List<BusStop>,
    val stopCount: Int,
    val estimatedMinutes: Int,
    val nextBusEtaMinutes: Int?
)

data class TripItinerary(
    val id: String,
    val legs: List<TripLeg>,
    val transferStop: BusStop?,
    val totalStops: Int,
    val totalMinutes: Int,
    val totalFareFils: Int
) {
    val isDirect: Boolean get() = legs.size == 1
}

object TripPlannerEngine {

    fun planTrip(
        startStop: BusStop,
        endStop: BusStop,
        routes: List<BusRoute> = KuwaitBusData.routes,
        liveBuses: List<LiveBus> = emptyList()
    ): List<TripItinerary> {
        if (startStop.id == endStop.id) return emptyList()

        val directResults = mutableListOf<TripItinerary>()

        // 1. Find all Direct Routes connecting startStop -> endStop
        for (route in routes) {
            val leg = buildLeg(route, startStop, endStop, liveBuses)
            if (leg != null) {
                directResults.add(
                    TripItinerary(
                        id = "direct_${route.number}_${startStop.id}_${endStop.id}",
                        legs = listOf(leg),
                        transferStop = null,
                        totalStops = leg.stopCount,
                        totalMinutes = leg.estimatedMinutes,
                        totalFareFils = route.fareFils
                    )
                )
            }
        }

        val sortedDirect = directResults.sortedWith(
            compareBy<TripItinerary> { it.totalMinutes }
                .thenBy { it.totalStops }
        )

        // 2. Find 1-Transfer Connecting Routes via shared Kuwait interchange stops
        val transferCandidates = mutableListOf<TripItinerary>()
        val directRouteNumbers = sortedDirect.map { it.legs.first().route.number }.toSet()

        val routesFromStart = routes.filter { r ->
            r.number !in directRouteNumbers && r.stops.any { it.id == startStop.id }
        }
        val routesToEnd = routes.filter { r ->
            r.number !in directRouteNumbers && r.stops.any { it.id == endStop.id }
        }

        val seenRoutePairs = mutableSetOf<String>()

        for (firstRoute in routesFromStart) {
            for (secondRoute in routesToEnd) {
                if (firstRoute.number == secondRoute.number) continue
                val pairKey = "${firstRoute.number}->${secondRoute.number}"
                if (pairKey in seenRoutePairs) continue

                val secondStopIds = secondRoute.stops.map { it.id }.toSet()
                val sharedStops = firstRoute.stops.filter {
                    it.id in secondStopIds && it.id != startStop.id && it.id != endStop.id
                }

                var bestForPair: TripItinerary? = null
                for (hub in sharedStops) {
                    val leg1 = buildLeg(firstRoute, startStop, hub, liveBuses) ?: continue
                    val leg2 = buildLeg(secondRoute, hub, endStop, liveBuses) ?: continue

                    val totalStops = leg1.stopCount + leg2.stopCount
                    val transferPenaltyMin = 6
                    val totalMinutes = leg1.estimatedMinutes + leg2.estimatedMinutes + transferPenaltyMin
                    val candidate = TripItinerary(
                        id = "xfer_${firstRoute.number}_${hub.id}_${secondRoute.number}",
                        legs = listOf(leg1, leg2),
                        transferStop = hub,
                        totalStops = totalStops,
                        totalMinutes = totalMinutes,
                        totalFareFils = firstRoute.fareFils + secondRoute.fareFils
                    )

                    if (bestForPair == null || candidate.totalMinutes < bestForPair.totalMinutes) {
                        bestForPair = candidate
                    }
                }

                if (bestForPair != null) {
                    seenRoutePairs.add(pairKey)
                    transferCandidates.add(bestForPair)
                }
            }
        }

        val topTransfers = transferCandidates
            .sortedWith(compareBy<TripItinerary> { it.totalMinutes }.thenBy { it.totalStops })
            .take(if (sortedDirect.isEmpty()) 6 else 3)

        return sortedDirect + topTransfers
    }

    private fun buildLeg(
        route: BusRoute,
        fromStop: BusStop,
        toStop: BusStop,
        liveBuses: List<LiveBus>
    ): TripLeg? {
        val fromIdx = route.stops.indexOfFirst { it.id == fromStop.id }
        val toIdx = route.stops.indexOfFirst { it.id == toStop.id }
        if (fromIdx == -1 || toIdx == -1 || fromIdx == toIdx) return null

        val direction = if (fromIdx < toIdx) BusDirection.OUTBOUND else BusDirection.INBOUND
        val orderedStops = if (direction == BusDirection.OUTBOUND) route.stops else route.stops.reversed()
        val orderedFrom = orderedStops.indexOfFirst { it.id == fromStop.id }
        val orderedTo = orderedStops.indexOfFirst { it.id == toStop.id }
        if (orderedFrom == -1 || orderedTo <= orderedFrom) return null

        val hopCount = orderedTo - orderedFrom
        val totalRouteHops = (orderedStops.size - 1).coerceAtLeast(1)
        val estMinutes = ((route.estimatedDurationMinutes.toDouble() * hopCount) / totalRouteHops)
            .roundToInt()
            .coerceAtLeast(3)

        val nextEta = nextArrivalForStopInDirection(
            route = route,
            stop = fromStop,
            direction = direction,
            liveBuses = liveBuses
        )

        return TripLeg(
            route = route,
            direction = direction,
            fromStop = fromStop,
            toStop = toStop,
            intermediateStops = orderedStops.subList(orderedFrom, orderedTo + 1),
            stopCount = hopCount,
            estimatedMinutes = estMinutes,
            nextBusEtaMinutes = nextEta
        )
    }

    private fun nextArrivalForStopInDirection(
        route: BusRoute,
        stop: BusStop,
        direction: BusDirection,
        liveBuses: List<LiveBus>
    ): Int? {
        val orderedStops = if (direction == BusDirection.OUTBOUND) route.stops else route.stops.reversed()
        val stopIdx = orderedStops.indexOfFirst { it.id == stop.id }
        if (stopIdx < 0) return null

        val matchingBuses = liveBuses.filter {
            it.routeNumber == route.number && it.direction == direction
        }
        if (matchingBuses.isEmpty()) return null

        return matchingBuses.mapNotNull { bus ->
            if (stopIdx >= bus.currentStopIndex) {
                val frac = (stopIdx.toFloat() / (orderedStops.size - 1).coerceAtLeast(1)) - bus.routeProgress
                if (frac >= 0f) {
                    (frac * route.estimatedDurationMinutes).roundToInt().coerceAtLeast(1)
                } else {
                    null
                }
            } else {
                null
            }
        }.minOrNull() ?: abs(route.frequencyMinutes / 2).coerceAtLeast(3)
    }
}
