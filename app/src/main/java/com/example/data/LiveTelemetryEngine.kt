package com.example.data

import com.example.model.BusDirection
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.model.OccupancyLevel
import com.example.model.StationArrival
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

object LiveTelemetryEngine {

    private val kuwaitTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Kuwait")

    data class KuwaitClockInfo(
        val formattedTime: String,
        val isServiceOperating: Boolean,
        val secondsOfDay: Int
    )

    fun currentKuwaitClock(): KuwaitClockInfo {
        val cal = Calendar.getInstance(kuwaitTimeZone)
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val secondsOfDay = hour24 * 3600 + minute * 60 + second

        val amPm = if (hour24 < 12) "AM" else "PM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val formatted = String.format("%02d:%02d:%02d %s AST", hour12, minute, second, amPm)
        // KPTC daily service window: 04:20 AM (15600s) to 11:00 PM (82800s)
        val isOperating = secondsOfDay in 15600..82800
        return KuwaitClockInfo(formatted, isOperating, secondsOfDay)
    }

    /**
     * Computes live bus positions along every route's actual Kuwait GPS polyline.
     * Positions advance smoothly in real-time synchronized to Asia/Kuwait schedule headways.
     * When outside 04:20–23:00 AST, if [previewModeEnabled] is true, uses a daytime offset
     * so users can inspect live route movement at any hour.
     */
    fun computeLiveBuses(
        routes: List<BusRoute>,
        nowMillis: Long = System.currentTimeMillis(),
        previewModeEnabled: Boolean = true
    ): List<LiveBus> {
        val cal = Calendar.getInstance(kuwaitTimeZone).apply { timeInMillis = nowMillis }
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val millis = cal.get(Calendar.MILLISECOND)

        val realSecondsOfDay = hour24 * 3600.0 + minute * 60.0 + second + millis / 1000.0
        val isWithinOperatingWindow = realSecondsOfDay in 15600.0..82800.0

        if (!isWithinOperatingWindow && !previewModeEnabled) {
            return emptyList()
        }

        // If outside operating hours and preview is on, map time into 09:00 AM peak window
        val effectiveTimeSec = if (isWithinOperatingWindow) {
            realSecondsOfDay
        } else {
            32400.0 + (nowMillis / 1000.0) % 3600.0
        }

        val result = mutableListOf<LiveBus>()
        routes.forEachIndexed { routeIndex, route ->
            val stops = route.stops
            if (stops.size < 2) return@forEachIndexed

            val tripDurationSec = (route.estimatedDurationMinutes * 60.0).coerceAtLeast(900.0)
            val headwaySec = (route.frequencyMinutes * 60.0).coerceAtLeast(600.0)
            // Number of active buses per direction based on cycle time / headway
            val busesPerDir = max(1, (tripDurationSec / headwaySec).roundToInt()).coerceIn(1, 3)

            for (dir in listOf(BusDirection.OUTBOUND, BusDirection.INBOUND)) {
                val orderedStops = if (dir == BusDirection.OUTBOUND) stops else stops.reversed()
                val dirOffsetSec = if (dir == BusDirection.OUTBOUND) 0.0 else headwaySec * 0.45

                for (slot in 0 until busesPerDir) {
                    val phaseSec = effectiveTimeSec + routeIndex * 137.0 + dirOffsetSec + slot * headwaySec
                    val rawProgress = ((phaseSec % tripDurationSec) / tripDurationSec).toFloat().coerceIn(0f, 0.999f)

                    val segmentFloat = rawProgress * (orderedStops.size - 1)
                    val segIndex = segmentFloat.toInt().coerceIn(0, orderedStops.size - 2)
                    val segFraction = (segmentFloat - segIndex).coerceIn(0f, 1f)

                    val fromStop = orderedStops[segIndex]
                    val toStop = orderedStops[segIndex + 1]

                    val lat = fromStop.latitude + (toStop.latitude - fromStop.latitude) * segFraction
                    val lng = fromStop.longitude + (toStop.longitude - fromStop.longitude) * segFraction
                    val heading = bearingDegrees(fromStop.latitude, fromStop.longitude, toStop.latitude, toStop.longitude)

                    val remainingTripMinutes = ((1f - rawProgress) * route.estimatedDurationMinutes)
                    val remainingToNextStop = (((1f - segFraction) * route.estimatedDurationMinutes) / (orderedStops.size - 1))
                        .roundToInt()
                        .coerceAtLeast(1)

                    val speedKmh = when {
                        route.number.endsWith("X") -> 52 + ((routeIndex + slot) % 12)
                        route.number == "999" || route.number == "103" -> 44 + ((routeIndex + slot) % 10)
                        else -> 28 + ((routeIndex + slot + segIndex) % 14)
                    }

                    val occupancy = when {
                        hour24 in 6..9 || hour24 in 16..19 -> if ((slot + routeIndex) % 2 == 0) OccupancyLevel.HIGH else OccupancyLevel.MODERATE
                        remainingTripMinutes < 12f -> OccupancyLevel.LOW
                        else -> OccupancyLevel.MODERATE
                    }

                    val fleetCode = 4100 + routeIndex * 20 + (if (dir == BusDirection.OUTBOUND) 1 else 5) + slot

                    result.add(
                        LiveBus(
                            busId = "KPTC-$fleetCode",
                            routeNumber = route.number,
                            direction = dir,
                            latitude = lat,
                            longitude = lng,
                            headingDegrees = heading,
                            routeProgress = rawProgress,
                            currentStopIndex = segIndex,
                            nextStop = toStop,
                            destinationStop = orderedStops.last(),
                            etaNextStopMinutes = remainingToNextStop,
                            speedKmh = speedKmh,
                            occupancy = occupancy
                        )
                    )
                }
            }
        }
        return result
    }

    fun arrivalsForStop(
        stop: BusStop,
        routes: List<BusRoute>,
        liveBuses: List<LiveBus>
    ): List<StationArrival> {
        val servingRoutes = routes.filter { r -> r.stops.any { it.id == stop.id } }
        val arrivals = mutableListOf<StationArrival>()

        for (route in servingRoutes) {
            val busesOnRoute = liveBuses.filter { it.routeNumber == route.number }
            for (bus in busesOnRoute) {
                val orderedStops = if (bus.direction == BusDirection.OUTBOUND) route.stops else route.stops.reversed()
                val stopIdx = orderedStops.indexOfFirst { it.id == stop.id }
                if (stopIdx > bus.currentStopIndex) {
                    val fractionRemaining = (stopIdx.toFloat() / (orderedStops.size - 1)) - bus.routeProgress
                    if (fractionRemaining >= 0f) {
                        val eta = (fractionRemaining * route.estimatedDurationMinutes).roundToInt().coerceAtLeast(1)
                        arrivals.add(
                            StationArrival(
                                route = route,
                                busId = bus.busId,
                                directionLabel = bus.direction.label,
                                destination = orderedStops.last().nameEn,
                                etaMinutes = eta,
                                occupancy = bus.occupancy
                            )
                        )
                    }
                }
            }
        }
        return arrivals.sortedBy { it.etaMinutes }
    }

    private fun bearingDegrees(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = lat1 * PI / 180.0
        val phi2 = lat2 * PI / 180.0
        val dLambda = (lon2 - lon1) * PI / 180.0
        val y = sin(dLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLambda)
        val theta = atan2(y, x)
        return ((theta * 180.0 / PI + 360.0) % 360.0).toFloat()
    }
}
