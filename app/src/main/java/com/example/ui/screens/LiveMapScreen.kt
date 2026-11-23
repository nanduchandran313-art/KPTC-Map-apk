package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.ui.KptcUiState
import com.example.ui.components.KuwaitTransitMap
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.OccupancyBadge
import com.example.ui.components.RouteBadgePill
import com.example.ui.components.TelemetryStatusBadge
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KuwaitEmerald

@Composable
fun LiveMapScreen(
    uiState: KptcUiState,
    onSelectRoute: (String?) -> Unit,
    onSelectBus: (String?) -> Unit,
    onSelectStop: (String?) -> Unit,
    onTogglePreviewOutsideHours: (Boolean) -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenRouteDetail: (String) -> Unit,
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

    val focusedRoute: BusRoute? = remember(uiState.selectedMapRouteNumber) {
        uiState.selectedMapRouteNumber?.let { KuwaitBusData.routesByNumber[it] }
    }
    val selectedBus: LiveBus? = remember(uiState.selectedMapBusId, uiState.liveBuses) {
        uiState.selectedMapBusId?.let { id -> uiState.liveBuses.firstOrNull { it.busId == id } }
    }
    val selectedStop: BusStop? = remember(uiState.selectedMapStopId) {
        uiState.selectedMapStopId?.let { KuwaitBusData.stopsMap[it] }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header & Live Clock + Language Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.liveMapHeader(lang),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uiState.kuwaitClock.formattedTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            LanguageSwitchPill(language = lang, onToggle = onToggleLanguage)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            TelemetryStatusBadge(
                isServiceOperating = uiState.kuwaitClock.isServiceOperating,
                activeBusCount = if (focusedRoute != null) {
                    uiState.liveBuses.count { it.routeNumber == focusedRoute.number }
                } else {
                    uiState.liveBuses.size
                }
            )
        }

        if (!uiState.kuwaitClock.isServiceOperating) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = AppStrings.nightDepotNotice(lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.previewOutsideHours,
                        onCheckedChange = onTogglePreviewOutsideHours
                    )
                }
            }
        }

        // Route Quick Filter Selector (All 23 Routes)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedMapRouteNumber == null,
                    onClick = { onSelectRoute(null) },
                    label = { Text(AppStrings.all23Routes(lang)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("map_filter_all")
                )
            }
            items(uiState.allRoutes, key = { it.number }) { route ->
                val isSelected = uiState.selectedMapRouteNumber == route.number
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectRoute(if (isSelected) null else route.number) },
                    label = { Text(route.number) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = route.badgeColor,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = Modifier.testTag("map_filter_${route.number}")
                )
            }
        }

        // Interactive Kuwait Vector Map + Selection Sheet
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            KuwaitTransitMap(
                routes = uiState.allRoutes,
                liveBuses = uiState.liveBuses,
                focusedRoute = focusedRoute,
                selectedBusId = uiState.selectedMapBusId,
                selectedStopId = uiState.selectedMapStopId,
                userCoordinates = uiState.userCoordinates,
                onBusTapped = { bus -> onSelectBus(bus.busId) },
                onStopTapped = { stop -> onSelectStop(stop.id) },
                onLocationButtonClick = {
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
                modifier = Modifier.fillMaxSize()
            )

            // Selected Live Bus Overlay Card
            androidx.compose.animation.AnimatedVisibility(
                visible = selectedBus != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
            ) {
                selectedBus?.let { bus ->
                    val route = KuwaitBusData.routesByNumber[bus.routeNumber]
                    LiveBusOverlayCard(
                        bus = bus,
                        route = route,
                        language = lang,
                        onDismiss = { onSelectBus(null) },
                        onOpenRoute = { onOpenRouteDetail(bus.routeNumber) }
                    )
                }
            }

            // Selected Station Overlay Card
            androidx.compose.animation.AnimatedVisibility(
                visible = selectedStop != null && selectedBus == null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
            ) {
                selectedStop?.let { stop ->
                    StationOverlayCard(
                        stop = stop,
                        liveBuses = uiState.liveBuses,
                        language = lang,
                        onDismiss = { onSelectStop(null) },
                        onRouteClick = onOpenRouteDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveBusOverlayCard(
    bus: LiveBus,
    route: BusRoute?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onOpenRoute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_bus_sheet"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RouteBadgePill(
                    routeNumber = bus.routeNumber,
                    badgeColor = route?.badgeColor ?: KptcRoyalBlue
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${bus.busId} • ${AppStrings.directionLabel(bus.direction, language)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppStrings.toDestination(AppStrings.stopName(bus.destinationStop, language), language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                OccupancyBadge(occupancy = bus.occupancy)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close bus details")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = KuwaitEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = AppStrings.nextStopEta(
                            AppStrings.stopName(bus.nextStop, language),
                            bus.etaNextStopMinutes,
                            language
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = AppStrings.speedKmh(bus.speedKmh, language),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Button(
                onClick = onOpenRoute,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(AppStrings.viewRouteDetailsButton(bus.routeNumber, language))
            }
        }
    }
}

@Composable
private fun StationOverlayCard(
    stop: BusStop,
    liveBuses: List<LiveBus>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onRouteClick: (String) -> Unit
) {
    val servingRoutes = remember(stop.id) { KuwaitBusData.routesServingStop(stop.id) }
    val arrivals = remember(stop.id, liveBuses) {
        LiveTelemetryEngine.arrivalsForStop(stop, KuwaitBusData.routes, liveBuses).take(3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_stop_sheet"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.stopName(stop, language),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = AppStrings.stopSecondaryLine(stop, language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close station card")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(servingRoutes, key = { it.number }) { route ->
                    RouteBadgePill(
                        routeNumber = route.number,
                        badgeColor = route.badgeColor,
                        compact = true,
                        onClick = { onRouteClick(route.number) }
                    )
                }
            }

            if (arrivals.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    arrivals.forEach { arr ->
                        val destStop = arr.route.stops.firstOrNull { it.nameEn == arr.destination }
                        val destLabel = if (destStop != null) AppStrings.stopName(destStop, language) else arr.destination
                        val (leftText, etaText) = AppStrings.busEtaShort(arr.route.number, destLabel, arr.etaMinutes, language)
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
