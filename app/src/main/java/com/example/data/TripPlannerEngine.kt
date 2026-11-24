package com.example.data

import com.example.location.LocationHelper
import com.example.model.BusDirection
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import kotlin.math.abs
import kotlin.math.roundToInt

data class TripEndpointPoint(
    val labelEn: String,
    val labelAr: String,
    val latitude: Double,
    val longitude: Double,
    val exactStop: BusStop? = null,
    val isGps: Boolean = false
)

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
    val totalFareFils: Int,
    val originWalkMeters: Int = 0,
    val destWalkMeters: Int = 0
) {
    val isDirect: Boolean get() = legs.size == 1
}

private data class KuwaitPlaceGazetteer(
    val keywords: List<String>,
    val labelEn: String,
    val labelAr: String,
    val latitude: Double,
    val longitude: Double
)

object TripPlannerEngine {

    private val kuwaitPlaces = listOf(
        KuwaitPlaceGazetteer(listOf("kuwait towers", "أبراج الكويت"), "Kuwait Towers", "أبراج الكويت", 29.3897, 48.0033),
        KuwaitPlaceGazetteer(listOf("souq sharq", "سوق شرق"), "Souq Sharq", "سوق شرق", 29.3895, 47.9972),
        KuwaitPlaceGazetteer(listOf("liberation tower", "برج التحرير"), "Liberation Tower", "برج التحرير", 29.3683, 47.9752),
        KuwaitPlaceGazetteer(listOf("mubarakiya", "souq al mubarakiya", "المباركية"), "Souq Al-Mubarakiya", "سوق المباركية", 29.3736, 47.9741),
        KuwaitPlaceGazetteer(listOf("marina mall", "marina", "مارينا مول"), "Marina Mall Salmiya", "مارينا مول السالمية", 29.3422, 48.0680),
        KuwaitPlaceGazetteer(listOf("scientific center", "المركز العلمي"), "The Scientific Center", "المركز العلمي السالمية", 29.3396, 48.0972),
        KuwaitPlaceGazetteer(listOf("bayan", "بيان"), "Bayan Area", "منطقة بيان", 29.3025, 48.0460),
        KuwaitPlaceGazetteer(listOf("mishref", "مشرف"), "Mishref Fairground", "أرض المعارض - مشرف", 29.2770, 48.0595),
        KuwaitPlaceGazetteer(listOf("jabriya", "الجابرية"), "Jabriya", "الجابرية", 29.3208, 48.0278),
        KuwaitPlaceGazetteer(listOf("hawally", "حولي"), "Hawally Center", "مركز حولي", 29.3335, 48.0275),
        KuwaitPlaceGazetteer(listOf("ardhiya", "العارضية"), "Al-Ardhiya", "العارضية", 29.2978, 47.9085),
        KuwaitPlaceGazetteer(listOf("firdous", "الفردوس"), "Al-Firdous", "الفردوس", 29.2945, 47.8825),
        KuwaitPlaceGazetteer(listOf("sabah al nasser", "صباح الناصر"), "Sabah Al-Nasser", "صباح الناصر", 29.2665, 47.8780),
        KuwaitPlaceGazetteer(listOf("al kout", "alkout", "الكوت"), "Al Kout Mall Fahaheel", "مجمع الكوت الفحيحيل", 29.0802, 48.1412),
        KuwaitPlaceGazetteer(listOf("mahboula", "المهبولة"), "Mahboula", "المهبولة", 29.1465, 48.1265),
        KuwaitPlaceGazetteer(listOf("mangaf", "المنقف"), "Mangaf", "المنقف", 29.1010, 48.1345),
        KuwaitPlaceGazetteer(listOf("kuwait city", "مدينة الكويت"), "Kuwait City Center", "مدينة الكويت", 29.3738, 47.9788)
    )

    fun parseCoordinates(input: String): Pair<Double, Double>? {
        val cleaned = input.trim()
        val match = Regex("""^\s*(-?\d{1,3}(?:\.\d+)?)\s*[,;\s]\s*(-?\d{1,3}(?:\.\d+)?)\s*$""")
            .matchEntire(cleaned) ?: return null
        val lat = match.groupValues[1].toDoubleOrNull() ?: return null
        val lng = match.groupValues[2].toDoubleOrNull() ?: return null
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return null
        return lat to lng
    }

    fun resolveQueryToEndpoint(
        query: String,
        stops: List<BusStop> = KuwaitBusData.allStops,
        geocodedCoords: Pair<Double, Double>? = null
    ): TripEndpointPoint? {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return null

        // 1. Direct lat, lng coordinates
        parseCoordinates(trimmed)?.let { (lat, lng) ->
            val coordLabel = String.format("%.4f, %.4f", lat, lng)
            return TripEndpointPoint(
                labelEn = coordLabel,
                labelAr = coordLabel,
                latitude = lat,
                longitude = lng,
                exactStop = null
            )
        }

        val lower = trimmed.lowercase()

        // 2. Exact or close KPTC BusStop match
        val exactStop = stops.firstOrNull {
            it.nameEn.equals(trimmed, ignoreCase = true) ||
                it.nameAr == trimmed
        } ?: stops.firstOrNull {
            it.nameEn.lowercase().contains(lower) ||
                it.area.lowercase().contains(lower) ||
                it.landmark.lowercase().contains(lower) ||
                it.nameAr.contains(trimmed)
        }
        if (exactStop != null) {
            return TripEndpointPoint(
                labelEn = exactStop.nameEn,
                labelAr = exactStop.nameAr,
                latitude = exactStop.latitude,
                longitude = exactStop.longitude,
                exactStop = exactStop
            )
        }

        // 3. Kuwait address / landmark gazetteer
        val gazetteerMatch = kuwaitPlaces.firstOrNull { place ->
            place.keywords.any { kw -> lower.contains(kw) || kw.contains(lower) }
        }
        if (gazetteerMatch != null) {
            return TripEndpointPoint(
                labelEn = gazetteerMatch.labelEn,
                labelAr = gazetteerMatch.labelAr,
                latitude = gazetteerMatch.latitude,
                longitude = gazetteerMatch.longitude,
                exactStop = null
            )
        }

        // 4. Online Android Geocoder coordinates
        if (geocodedCoords != null) {
            return TripEndpointPoint(
                labelEn = trimmed,
                labelAr = trimmed,
                latitude = geocodedCoords.first,
                longitude = geocodedCoords.second,
                exactStop = null
            )
        }

        return null
    }

    fun planTripFromEndpoints(
        origin: TripEndpointPoint,
        destination: TripEndpointPoint,
        routes: List<BusRoute> = KuwaitBusData.routes,
        stops: List<BusStop> = KuwaitBusData.allStops,
        liveBuses: List<LiveBus> = emptyList()
    ): List<TripItinerary> {
        if (origin.exactStop != null && destination.exactStop != null) {
            return planTrip(
                startStop = origin.exactStop,
                endStop = destination.exactStop,
                routes = routes,
                liveBuses = liveBuses
            )
        }

        val originCandidates = if (origin.exactStop != null) {
            listOf(origin.exactStop to 0.0)
        } else {
            LocationHelper.findClosestStops(origin.latitude, origin.longitude, stops, limit = 3)
                .map { it.stop to it.distanceKm }
        }

        val destCandidates = if (destination.exactStop != null) {
            listOf(destination.exactStop to 0.0)
        } else {
            LocationHelper.findClosestStops(destination.latitude, destination.longitude, stops, limit = 3)
                .map { it.stop to it.distanceKm }
        }

        val allOptions = mutableListOf<TripItinerary>()
        val seenSignatures = mutableSetOf<String>()

        for ((boardStop, walkStartKm) in originCandidates) {
            for ((alightStop, walkEndKm) in destCandidates) {
                if (boardStop.id == alightStop.id) continue
                val startWalkMeters = (walkStartKm * 1000).roundToInt()
                val endWalkMeters = (walkEndKm * 1000).roundToInt()
                val walkPenaltyMin = ((walkStartKm + walkEndKm) * 12.0).roundToInt()

                val basePlans = planTrip(boardStop, alightStop, routes, liveBuses)
                for (plan in basePlans) {
                    val sig = plan.legs.joinToString("+") { "${it.route.number}:${it.direction.name}" }
                    if (sig in seenSignatures) continue
                    seenSignatures.add(sig)

                    allOptions.add(
                        plan.copy(
                            id = "${plan.id}_loc_${boardStop.id}_${alightStop.id}",
                            totalMinutes = plan.totalMinutes + walkPenaltyMin,
                            originWalkMeters = startWalkMeters,
                            destWalkMeters = endWalkMeters
                        )
                    )
                }
            }
        }

        return allOptions
            .sortedWith(
                compareBy<TripItinerary> { !it.isDirect }
                    .thenBy { it.totalMinutes }
                    .thenBy { it.originWalkMeters + it.destWalkMeters }
            )
            .take(6)
    }

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
