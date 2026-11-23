package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.data.LiveTelemetryEngine
import com.example.location.LocationHelper
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.ui.KptcUiState
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.RouteBadgePill
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.KuwaitEmerald

@Composable
fun StationsScreen(
    uiState: KptcUiState,
    onStationQueryChange: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onRouteClick: (String) -> Unit,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val context = LocalContext.current
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            onRefreshLocation()
        }
    }

    val refLat = uiState.userCoordinates?.latitude ?: KuwaitBusData.KUWAIT_CITY_LAT
    val refLng = uiState.userCoordinates?.longitude ?: KuwaitBusData.KUWAIT_CITY_LNG

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = AppStrings.stationsTitle(lang),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = AppStrings.stationsSubtitle(uiState.allRoutes.size, KuwaitBusData.allStops.size, lang),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageSwitchPill(language = lang, onToggle = onToggleLanguage)
                }
            }

            // GPS / Nearest Reference Card
            item {
                val hasGps = uiState.userCoordinates != null
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.gpsSortedTitle(hasGps, lang),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = AppStrings.gpsSortedDesc(hasGps, lang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = {
                                if (LocationHelper.hasLocationPermission(context)) {
                                    onRefreshLocation()
                                } else {
                                    locationLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.testTag("stations_use_gps_button")
                        ) {
                            Text(AppStrings.gpsButtonLabel(hasGps, lang))
                        }
                    }
                }
            }

            // Station Search Input
            item {
                OutlinedTextField(
                    value = uiState.stationQuery,
                    onValueChange = onStationQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stations_search_input"),
                    placeholder = {
                        Text(AppStrings.searchStationsPlaceholder(lang))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (uiState.stationQuery.isNotEmpty()) {
                            IconButton(onClick = { onStationQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear station search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            items(uiState.filteredStops, key = { it.id }) { stop ->
                val distKm = LocationHelper.haversineKm(refLat, refLng, stop.latitude, stop.longitude)
                StationDirectoryCard(
                    stop = stop,
                    language = lang,
                    distanceLabel = LocationHelper.formatDistance(distKm),
                    liveBuses = uiState.liveBuses,
                    onRouteClick = onRouteClick
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StationDirectoryCard(
    stop: BusStop,
    language: AppLanguage,
    distanceLabel: String,
    liveBuses: List<LiveBus>,
    onRouteClick: (String) -> Unit
) {
    val servingRoutes = remember(stop.id) { KuwaitBusData.routesServingStop(stop.id) }
    val nextArrivals = remember(stop.id, liveBuses) {
        LiveTelemetryEngine.arrivalsForStop(stop, KuwaitBusData.routes, liveBuses).take(3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("station_card_${stop.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = AppStrings.stopName(stop, language),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = AppStrings.stopSecondaryLine(stop, language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (stop.landmark.isNotEmpty() && language == AppLanguage.ENGLISH) {
                            Text(
                                text = stop.landmark,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = distanceLabel,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                servingRoutes.forEach { route ->
                    RouteBadgePill(
                        routeNumber = route.number,
                        badgeColor = route.badgeColor,
                        compact = true,
                        onClick = { onRouteClick(route.number) }
                    )
                }
            }

            if (nextArrivals.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = AppStrings.liveNextArrivals(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        nextArrivals.forEach { arr ->
                            val destStop = arr.route.stops.firstOrNull { it.nameEn == arr.destination }
                            val destLabel = if (destStop != null) AppStrings.stopName(destStop, language) else arr.destination
                            val (leftText, etaText) = AppStrings.routeToEtaRow(arr.route.number, destLabel, arr.etaMinutes, language)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = leftText,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = etaText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = KuwaitEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
