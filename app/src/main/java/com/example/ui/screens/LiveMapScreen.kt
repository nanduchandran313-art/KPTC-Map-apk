package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.ServiceAlertBanner
import com.example.ui.components.ServiceAlertBellButton
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
    onOpenAlerts: () -> Unit = {},
    onDismissAlert: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val context = LocalContext.current
    var centerOnUserTrigger by remember { mutableIntStateOf(0) }
    var showClosestOverlay by remember { mutableStateOf(true) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            onRefreshLocation()
            centerOnUserTrigger++
            showClosestOverlay = true
        }
    }

    val requestOrRefreshGps: () -> Unit = {
        if (LocationHelper.hasLocationPermission(context)) {
            onRefreshLocation()
            centerOnUserTrigger++
            showClosestOverlay = true
        } else {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
    val closestStopIds = remember(uiState.closestStops) {
        uiState.closestStops.take(3).map { it.stop.id }.toSet()
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
            Spacer(modifier = Modifier.width(6.dp))
            ServiceAlertBellButton(
                activeCount = uiState.activeServiceAlerts.size,
                language = lang,
                onClick = onOpenAlerts
            )
            Spacer(modifier = Modifier.width(6.dp))
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

        val mapAlerts = remember(uiState.activeServiceAlerts, focusedRoute) {
            if (focusedRoute != null) {
                uiState.activeServiceAlerts.filter { focusedRoute.number in it.affectedRouteNumbers }
            } else {
                uiState.activeServiceAlerts
            }
        }
        if (mapAlerts.isNotEmpty()) {
            ServiceAlertBanner(
                alerts = mapAlerts,
                language = lang,
                onOpenNotifications = onOpenAlerts,
                onDismissAlert = onDismissAlert,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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
                closestStopIds = closestStopIds,
                centerOnUserTrigger = centerOnUserTrigger,
                onBusTapped = { bus -> onSelectBus(bus.busId) },
                onStopTapped = { stop -> onSelectStop(stop.id) },
                onLocationButtonClick = requestOrRefreshGps,
                modifier = Modifier.fillMaxSize()
            )

            // Top-Right Closest Bus Stops Overlay Chip / Toggle
            if (uiState.userCoordinates == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .defaultMinSize(minHeight = 40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = requestOrRefreshGps)
                        .testTag("map_enable_gps_overlay"),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = AppStrings.enableGpsOverlayCta(lang),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = AppStrings.enableGpsOverlayCta(lang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else if (!showClosestOverlay && selectedBus == null && selectedStop == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showClosestOverlay = true }
                        .testTag("map_show_closest_chip"),
                    color = KuwaitEmerald.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = AppStrings.closestStopsOverlayTitle(lang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }

            // Closest Bus Stops GPS Overlay Card (when GPS available & no bus/stop sheet open)
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.closestStops.isNotEmpty() && showClosestOverlay && selectedBus == null && selectedStop == null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, end = 68.dp, bottom = 10.dp)
            ) {
                ClosestStopsMapOverlayCard(
                    closestStops = uiState.closestStops.take(3),
                    language = lang,
                    onStopClick = { stop -> onSelectStop(stop.id) },
                    onDismiss = { showClosestOverlay = false }
                )
            }

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

@Composable
private fun ClosestStopsMapOverlayCard(
    closestStops: List<com.example.location.NearbyBusStop>,
    language: AppLanguage,
    onStopClick: (BusStop) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp)
            .testTag("map_closest_stops_overlay"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = KuwaitEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = AppStrings.closestStopsOverlayTitle(language),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = KuwaitEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hide closest stops overlay",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            closestStops.forEach { nearby ->
                val servingRoutes = KuwaitBusData.routesServingStop(nearby.stop.id)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onStopClick(nearby.stop) }
                        .testTag("map_closest_stop_${nearby.stop.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.stopName(nearby.stop, language),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (servingRoutes.isNotEmpty()) {
                                Text(
                                    text = servingRoutes.take(4).joinToString(" • ") { it.number },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocationHelper.formatDistance(nearby.distanceKm),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KuwaitEmerald
                        )
                    }
                }
            }
        }
    }
}
