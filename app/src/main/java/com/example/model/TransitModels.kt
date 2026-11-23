package com.example.model

import androidx.compose.ui.graphics.Color

enum class RouteCategory(val label: String) {
    CITY("City"),
    EXPRESS("Express (X)"),
    AIRPORT_MALL("Airport & Malls"),
    COASTAL_SOUTH("Coastal & South"),
    INTER_GOVERNORATE("Inter-District")
}

enum class BusDirection(val label: String) {
    OUTBOUND("Outbound"),
    INBOUND("Inbound")
}

enum class OccupancyLevel(val label: String) {
    LOW("Seats Available"),
    MODERATE("Moderate"),
    HIGH("Busy")
}

data class BusStop(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val area: String,
    val latitude: Double,
    val longitude: Double,
    val landmark: String = ""
)

data class BusRoute(
    val number: String,
    val titleEn: String,
    val origin: String,
    val destination: String,
    val corridorSummary: String,
    val category: RouteCategory,
    val stops: List<BusStop>,
    val firstBus: String,
    val lastBus: String,
    val frequencyMinutes: Int,
    val peakFrequencyMinutes: Int,
    val fareFils: Int,
    val estimatedDurationMinutes: Int,
    val distanceKm: Double,
    val badgeColor: Color,
    val ramadanHours: String = "04:00 AM – 12:30 AM",
    val operationNotes: String
)

data class LiveBus(
    val busId: String,
    val routeNumber: String,
    val direction: BusDirection,
    val latitude: Double,
    val longitude: Double,
    val headingDegrees: Float,
    val routeProgress: Float,
    val currentStopIndex: Int,
    val nextStop: BusStop,
    val destinationStop: BusStop,
    val etaNextStopMinutes: Int,
    val speedKmh: Int,
    val occupancy: OccupancyLevel
)

data class StationArrival(
    val route: BusRoute,
    val busId: String,
    val directionLabel: String,
    val destination: String,
    val etaMinutes: Int,
    val occupancy: OccupancyLevel
)

data class MapPoint(
    val latitude: Double,
    val longitude: Double
)

data class HighwayPath(
    val name: String,
    val points: List<MapPoint>,
    val isMajor: Boolean = true
)
