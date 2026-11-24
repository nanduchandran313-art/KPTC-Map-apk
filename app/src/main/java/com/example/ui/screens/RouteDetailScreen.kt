package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.data.ServiceAlertEngine
import com.example.model.BusDirection
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.ui.KptcUiState
import com.example.ui.components.KuwaitTransitMap
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.MetricChip
import com.example.ui.components.OccupancyBadge
import com.example.ui.components.RouteBadgePill
import com.example.ui.components.RouteSpecificAlertCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KuwaitEmerald

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RouteDetailScreen(
    routeNumber: String,
    uiState: KptcUiState,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenFullMap: (String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onOpenAlerts: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val context = LocalContext.current
    val route = KuwaitBusData.routesByNumber[routeNumber]
    if (route == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Route not found")
        }
        return
    }

    var direction by remember(routeNumber) { mutableStateOf(BusDirection.OUTBOUND) }
    var selectedStopId by remember(routeNumber) { mutableStateOf<String?>(null) }
    var selectedBusId by remember(routeNumber) { mutableStateOf<String?>(null) }

    val orderedStops = remember(route, direction) {
        if (direction == BusDirection.OUTBOUND) route.stops else route.stops.reversed()
    }
    val routeBuses = remember(uiState.liveBuses, route.number, direction) {
        uiState.liveBuses.filter { it.routeNumber == route.number && it.direction == direction }
    }
    val allRouteBuses = remember(uiState.liveBuses, route.number) {
        uiState.liveBuses.filter { it.routeNumber == route.number }
    }
    val routeAlerts = remember(uiState.activeServiceAlerts, route.number) {
        ServiceAlertEngine.alertsForRoute(
            routeNumber = route.number,
            alerts = uiState.activeServiceAlerts
        )
    }
    val isFavorite = route.number in uiState.favoriteRouteNumbers

    val originText = AppStrings.routeOrigin(route, lang)
    val destText = AppStrings.routeDestination(route, lang)

    val shareRoute: () -> Unit = {
        val shareText = AppStrings.buildRouteShareText(route, routeAlerts, lang)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "KPTC Route ${route.number}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(
            Intent.createChooser(sendIntent, AppStrings.shareRouteChooserTitle(lang))
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RouteBadgePill(
                            routeNumber = route.number,
                            badgeColor = route.badgeColor,
                            compact = true
                        )
                        Column {
                            Text(
                                text = AppStrings.routeTitle(route, lang),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${AppStrings.categoryLabel(route.category, lang)} • ${AppStrings.stopsCountLabel(route.stops.size, lang)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("route_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to routes"
                        )
                    }
                },
                actions = {
                    LanguageSwitchPill(
                        language = lang,
                        onToggle = onToggleLanguage
                    )
                    IconButton(
                        onClick = shareRoute,
                        modifier = Modifier.testTag("route_detail_share")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = AppStrings.shareRouteDescription(lang),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { onToggleFavorite(route.number) },
                        modifier = Modifier.testTag("route_detail_favorite")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Save route ${route.number}",
                            tint = if (isFavorite) ExpressCrimson else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (routeAlerts.isNotEmpty()) {
                    item {
                        RouteSpecificAlertCard(
                            alerts = routeAlerts,
                            language = lang,
                            onOpenAllAlerts = onOpenAlerts
                        )
                    }
                }

                // Interactive Route Map Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        KuwaitTransitMap(
                            routes = uiState.allRoutes,
                            liveBuses = allRouteBuses,
                            focusedRoute = route,
                            selectedBusId = selectedBusId,
                            selectedStopId = selectedStopId,
                            userCoordinates = uiState.userCoordinates,
                            onBusTapped = { selectedBusId = it.busId },
                            onStopTapped = { selectedStopId = it.id },
                            showControls = true,
                            modifier = Modifier.fillMaxSize()
                        )
                        FilledTonalButton(
                            onClick = { onOpenFullMap(route.number) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .testTag("route_detail_full_map_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.expandMap(lang))
                        }
                    }
                }

                // Direction Switcher
                item {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = direction == BusDirection.OUTBOUND,
                            onClick = { direction = BusDirection.OUTBOUND },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("direction_outbound")
                        ) {
                            Text(
                                text = "$originText → $destText",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        SegmentedButton(
                            selected = direction == BusDirection.INBOUND,
                            onClick = { direction = BusDirection.INBOUND },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("direction_inbound")
                        ) {
                            Text(
                                text = "$destText → $originText",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Live Buses Active in Selected Direction
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = AppStrings.liveBusesHeader(
                                AppStrings.directionLabel(direction, lang),
                                routeBuses.size,
                                lang
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (routeBuses.isEmpty()) {
                            Text(
                                text = AppStrings.outsideOperatingNote(lang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(routeBuses, key = { it.busId }) { bus ->
                                    LiveRouteBusChip(
                                        bus = bus,
                                        language = lang,
                                        accentColor = route.badgeColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Operations & Schedule Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = AppStrings.operationsHeader(lang),
                                style = MaterialTheme.typography.titleMedium
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricChip(AppStrings.firstBusLabel(lang), route.firstBus)
                                MetricChip(AppStrings.lastBusLabel(lang), route.lastBus)
                                MetricChip(
                                    AppStrings.headwayLabel(lang),
                                    AppStrings.headwayValue(route.frequencyMinutes, route.peakFrequencyMinutes, lang)
                                )
                                MetricChip(
                                    AppStrings.fareLabel(lang),
                                    AppStrings.fareAmount(route.fareFils, lang),
                                    accentColor = KuwaitEmerald
                                )
                                MetricChip(
                                    AppStrings.tripTimeLabel(lang),
                                    AppStrings.tripTimeValue(route.estimatedDurationMinutes, lang)
                                )
                                MetricChip(
                                    AppStrings.distanceLabel(lang),
                                    AppStrings.distanceValue(route.distanceKm, lang)
                                )
                            }
                            Text(
                                text = AppStrings.ramadanHoursText(route.ramadanHours, lang),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = AppStrings.routeNotes(route, lang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Station-by-Station Timeline Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.stationSequenceHeader(orderedStops.size, lang),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.tapBadgeHint(lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Vertical Timeline of Stops
                itemsIndexed(orderedStops, key = { idx, s -> "${direction.name}_${idx}_${s.id}" }) { index, stop ->
                    val approachingBus = routeBuses.firstOrNull { it.nextStop.id == stop.id }
                    val connectingRoutes = remember(stop.id, route.number) {
                        KuwaitBusData.routesServingStop(stop.id).filter { it.number != route.number }
                    }
                    StopTimelineRow(
                        index = index,
                        totalStops = orderedStops.size,
                        stop = stop,
                        language = lang,
                        routeColor = route.badgeColor,
                        approachingBus = approachingBus,
                        connectingRoutes = connectingRoutes,
                        onInterchangeClick = onNavigateToRoute
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveRouteBusChip(
    bus: LiveBus,
    language: AppLanguage,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .width(210.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = bus.busId,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = AppStrings.speedKmh(bus.speedKmh, language),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = AppStrings.nextStopShort(AppStrings.stopName(bus.nextStop, language), language),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.etaShort(bus.etaNextStopMinutes, language),
                    style = MaterialTheme.typography.labelMedium,
                    color = KuwaitEmerald,
                    fontWeight = FontWeight.Bold
                )
                OccupancyBadge(occupancy = bus.occupancy)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StopTimelineRow(
    index: Int,
    totalStops: Int,
    stop: BusStop,
    language: AppLanguage,
    routeColor: Color,
    approachingBus: LiveBus?,
    connectingRoutes: List<BusRoute>,
    onInterchangeClick: (String) -> Unit
) {
    val isTerminal = index == 0 || index == totalStops - 1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline Spine
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(34.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(10.dp)
                    .background(if (index == 0) Color.Transparent else routeColor.copy(alpha = 0.55f))
            )
            Box(
                modifier = Modifier
                    .size(if (isTerminal) 16.dp else 12.dp)
                    .clip(CircleShape)
                    .background(if (isTerminal) routeColor else MaterialTheme.colorScheme.surface)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isTerminal) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(routeColor)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .background(if (index == totalStops - 1) Color.Transparent else routeColor.copy(alpha = 0.55f))
            )
        }

        // Station Details Card
        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = if (approachingBus != null || isTerminal) 2.dp else 0.dp
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${index + 1}. ${AppStrings.stopName(stop, language)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isTerminal) FontWeight.Bold else FontWeight.SemiBold
                        )
                        Text(
                            text = AppStrings.stopSecondaryLine(stop, language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (approachingBus != null) {
                        val etaTag = if (language == AppLanguage.ARABIC) {
                            "${approachingBus.busId} • ${approachingBus.etaNextStopMinutes}د"
                        } else {
                            "${approachingBus.busId} • ${approachingBus.etaNextStopMinutes}m"
                        }
                        Surface(
                            color = KuwaitEmerald.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = etaTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = KuwaitEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (connectingRoutes.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        connectingRoutes.take(8).forEach { conn ->
                            RouteBadgePill(
                                routeNumber = conn.number,
                                badgeColor = conn.badgeColor,
                                compact = true,
                                onClick = { onInterchangeClick(conn.number) }
                            )
                        }
                    }
                }
            }
        }
    }
}
