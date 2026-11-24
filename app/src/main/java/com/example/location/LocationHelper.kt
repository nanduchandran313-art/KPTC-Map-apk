package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.model.BusStop
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class UserCoordinates(
    val latitude: Double,
    val longitude: Double,
    val isLiveGps: Boolean
)

data class NearbyBusStop(
    val stop: BusStop,
    val distanceKm: Double
)

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(context: Context): UserCoordinates? {
        if (!hasLocationPermission(context)) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            UserCoordinates(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                isLiveGps = true
                            )
                        )
                    } else {
                        client.lastLocation
                            .addOnSuccessListener { last ->
                                if (last != null) {
                                    cont.resume(
                                        UserCoordinates(
                                            latitude = last.latitude,
                                            longitude = last.longitude,
                                            isLiveGps = true
                                        )
                                    )
                                } else {
                                    cont.resume(null)
                                }
                            }
                            .addOnFailureListener { cont.resume(null) }
                    }
                }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    fun findClosestStops(
        latitude: Double,
        longitude: Double,
        stops: List<BusStop>,
        limit: Int = 5
    ): List<NearbyBusStop> {
        return stops
            .map { stop ->
                NearbyBusStop(
                    stop = stop,
                    distanceKm = haversineKm(latitude, longitude, stop.latitude, stop.longitude)
                )
            }
            .sortedBy { it.distanceKm }
            .take(limit)
    }

    @Suppress("DEPRECATION")
    suspend fun geocodeKuwaitAddress(
        context: Context,
        query: String
    ): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty() || !Geocoder.isPresent()) return@withContext null
        val fullQuery = if (trimmed.contains("kuwait", ignoreCase = true) || trimmed.contains("الكويت")) {
            trimmed
        } else {
            "$trimmed, Kuwait"
        }
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocationName(fullQuery, 3) { addresses ->
                        val match = addresses.firstOrNull {
                            it.latitude in 28.5..30.2 && it.longitude in 46.5..49.0
                        } ?: addresses.firstOrNull()
                        cont.resume(match?.let { it.latitude to it.longitude })
                    }
                }
            } else {
                val addresses = geocoder.getFromLocationName(fullQuery, 3).orEmpty()
                val match = addresses.firstOrNull {
                    it.latitude in 28.5..30.2 && it.longitude in 46.5..49.0
                } ?: addresses.firstOrNull()
                match?.let { it.latitude to it.longitude }
            }
        } catch (_: Exception) {
            null
        }
    }

    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) {
            "${(km * 1000).toInt()} m"
        } else {
            String.format("%.1f km", km)
        }
    }
}
