package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.data.TripItinerary
import com.example.model.BusStop
import com.example.ui.KptcUiState
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.RouteBadgePill
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.DesertGold
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KuwaitEmerald

private enum class PickingEndpoint {
    NONE, START, DESTINATION
}

private data class QuickTripPreset(
    val startId: String,
    val endId: String,
    val labelEn: String,
    val labelAr: String
)

private val popularKuwaitTrips = listOf(
    QuickTripPreset("mirqab", "airport_t1", "Mirqab → Airport", "المرقاب ← المطار"),
    QuickTripPreset("salmiya_coop", "avenues_mall", "Salmiya → Avenues", "السالمية ← الأفنيوز"),
    QuickTripPreset("maliya", "fahaheel_green_tower", "Maliya → Fahaheel", "المالية ← الفحيحيل"),
    QuickTripPreset("sharq", "jleeb_terminal", "Sharq → Jleeb", "شرق ← الجليب"),
    QuickTripPreset("mirqab", "jahra_coop", "Mirqab → Jahra", "المرقاب ← الجهراء")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    uiState: KptcUiState,
    onSelectStartStop: (BusStop) -> Unit,
    onSelectEndStop: (BusStop) -> Unit,
    onSwapStops: () -> Unit,
    onApplyQuickTrip: (String, String) -> Unit,
    onToggleLanguage: () -> Unit,
    onRouteClick: (String) -> Unit,
    onTrackRouteOnMap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    var pickingEndpoint by remember { mutableStateOf(PickingEndpoint.NONE) }
    var stationSearchQuery by remember { mutableStateOf("") }

    val openPicker: (PickingEndpoint) -> Unit = { target ->
        stationSearchQuery = ""
        pickingEndpoint = target
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.plannerTitle(lang),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.plannerSubtitle(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageSwitchPill(
                        language = lang,
                        onToggle = onToggleLanguage
                    )
                }
            }

            // Start & Destination Picker Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EndpointSelectorBox(
                                label = AppStrings.startPointLabel(lang),
                                selectedStop = uiState.tripStartStop,
                                placeholder = AppStrings.chooseStartHint(lang),
                                indicatorColor = KuwaitEmerald,
                                isStart = true,
                                language = lang,
                                onClick = { openPicker(PickingEndpoint.START) },
                                modifier = Modifier.testTag("planner_start_selector")
                            )

                            EndpointSelectorBox(
                                label = AppStrings.destinationPointLabel(lang),
                                selectedStop = uiState.tripEndStop,
                                placeholder = AppStrings.chooseDestHint(lang),
                                indicatorColor = ExpressCrimson,
                                isStart = false,
                                language = lang,
                                onClick = { openPicker(PickingEndpoint.DESTINATION) },
                                modifier = Modifier.testTag("planner_dest_selector")
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalIconButton(
                            onClick = onSwapStops,
                            enabled = uiState.tripStartStop != null || uiState.tripEndStop != null,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("planner_swap_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap start and destination stations"
                            )
                        }
                    }
                }
            }

            // Popular Kuwait Trips Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = AppStrings.popularTripsLabel(lang),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(popularKuwaitTrips) { preset ->
                            AssistChip(
                                onClick = { onApplyQuickTrip(preset.startId, preset.endId) },
                                label = {
                                    Text(
                                        text = if (AppStrings.isArabic(lang)) preset.labelAr else preset.labelEn,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AltRoute,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("quick_trip_${preset.startId}_${preset.endId}")
                            )
                        }
                    }
                }
            }

            // Results Section
            val startStop = uiState.tripStartStop
            val endStop = uiState.tripEndStop

            when {
                startStop == null || endStop == null -> {
                    item {
                        PlannerNoticeCard(
                            icon = Icons.Default.DirectionsBus,
                            iconTint = MaterialTheme.colorScheme.primary,
                            title = AppStrings.selectPointsPromptTitle(lang),
                            description = AppStrings.selectPointsPromptDesc(lang),
                            modifier = Modifier.testTag("planner_empty_prompt")
                        )
                    }
                }

                startStop.id == endStop.id -> {
                    item {
                        PlannerNoticeCard(
                            icon = Icons.Default.SwapHoriz,
                            iconTint = DesertGold,
                            title = AppStrings.stopName(startStop, lang),
                            description = AppStrings.sameStopWarning(lang),
                            modifier = Modifier.testTag("planner_same_stop_warning")
                        )
                    }
                }

                uiState.tripRecommendations.isEmpty() -> {
                    item {
                        PlannerNoticeCard(
                            icon = Icons.Default.DirectionsBus,
                            iconTint = ExpressCrimson,
                            title = AppStrings.noTripFoundTitle(lang),
                            description = AppStrings.noTripFoundDesc(lang),
                            modifier = Modifier.testTag("planner_no_routes_found")
                        )
                    }
                }

                else -> {
                    item {
                        Text(
                            text = AppStrings.recommendedRoutesHeader(uiState.tripRecommendations.size, lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 2.dp)
                                .testTag("planner_results_header")
                        )
                    }

                    items(uiState.tripRecommendations, key = { it.id }) { itinerary ->
                        TripItineraryCard(
                            itinerary = itinerary,
                            language = lang,
                            onRouteClick = onRouteClick,
                            onTrackRouteOnMap = onTrackRouteOnMap,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // Station Picker Modal Bottom Sheet
    if (pickingEndpoint != PickingEndpoint.NONE) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val trimmed = stationSearchQuery.trim().lowercase()
        val matchingStops = remember(trimmed) {
            if (trimmed.isEmpty()) {
                KuwaitBusData.allStops
            } else {
                KuwaitBusData.allStops.filter { stop ->
                    stop.nameEn.lowercase().contains(trimmed) ||
                        stop.area.lowercase().contains(trimmed) ||
                        stop.landmark.lowercase().contains(trimmed) ||
                        stop.nameAr.contains(trimmed) ||
                        KuwaitBusData.routesServingStop(stop.id).any {
                            it.number.lowercase().contains(trimmed)
                        }
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { pickingEndpoint = PickingEndpoint.NONE },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (pickingEndpoint == PickingEndpoint.START) {
                        AppStrings.pickerTitleStart(lang)
                    } else {
                        AppStrings.pickerTitleDest(lang)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = stationSearchQuery,
                    onValueChange = { stationSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("planner_station_search"),
                    placeholder = {
                        Text(
                            text = AppStrings.searchStationsPlaceholder(lang),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Kuwait stations",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (stationSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { stationSearchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear station search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(matchingStops, key = { it.id }) { stop ->
                        val servingRoutes = KuwaitBusData.routesServingStop(stop.id)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (pickingEndpoint == PickingEndpoint.START) {
                                        onSelectStartStop(stop)
                                    } else {
                                        onSelectEndStop(stop)
                                    }
                                    pickingEndpoint = PickingEndpoint.NONE
                                }
                                .testTag("picker_stop_${stop.id}"),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
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
                                            text = AppStrings.stopName(stop, lang),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = AppStrings.stopSecondaryLine(stop, lang),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                if (servingRoutes.isNotEmpty()) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        items(servingRoutes, key = { it.number }) { route ->
                                            RouteBadgePill(
                                                routeNumber = route.number,
                                                badgeColor = route.badgeColor,
                                                compact = true
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EndpointSelectorBox(
    label: String,
    selectedStop: BusStop?,
    placeholder: String,
    indicatorColor: Color,
    isStart: Boolean,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(indicatorColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isStart) Icons.Default.TripOrigin else Icons.Default.LocationOn,
                    contentDescription = label,
                    tint = indicatorColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (selectedStop != null) AppStrings.stopName(selectedStop, language) else placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedStop != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedStop != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (selectedStop != null) {
                    Text(
                        text = selectedStop.area,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerNoticeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TripItineraryCard(
    itinerary: TripItinerary,
    language: AppLanguage,
    onRouteClick: (String) -> Unit,
    onTrackRouteOnMap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstLeg = itinerary.legs.first()
    val badgeColor = if (itinerary.isDirect) KuwaitEmerald else DesertGold

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_option_${itinerary.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top summary row: Direct / Transfer badge + Route pills + Time / Fare
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
                    Surface(
                        color = badgeColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (itinerary.isDirect) {
                                AppStrings.directBadge(language)
                            } else {
                                AppStrings.oneTransferBadge(language)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    itinerary.legs.forEachIndexed { idx, leg ->
                        if (idx > 0) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        RouteBadgePill(
                            routeNumber = leg.route.number,
                            badgeColor = leg.route.badgeColor,
                            compact = true,
                            onClick = { onRouteClick(leg.route.number) }
                        )
                    }
                }

                Text(
                    text = AppStrings.tripTimeValue(itinerary.totalMinutes, language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Metrics Row: stops count + total fare + next live ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = AppStrings.stopsHopLabel(itinerary.totalStops, language),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = AppStrings.fareAmount(itinerary.totalFareFils, language),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                firstLeg.nextBusEtaMinutes?.let { eta ->
                    Surface(
                        color = KuwaitEmerald.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = AppStrings.nextBusInMin(eta, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = KuwaitEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Leg-by-leg boarding instructions
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itinerary.legs.forEachIndexed { index, leg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(leg.route.badgeColor)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.boardRouteAt(
                                    routeNum = leg.route.number,
                                    from = AppStrings.stopName(leg.fromStop, language),
                                    to = AppStrings.stopName(leg.toStop, language),
                                    lang = language
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${AppStrings.directionLabel(leg.direction, language)} • ${AppStrings.stopsHopLabel(leg.stopCount, language)} • ${AppStrings.tripTimeValue(leg.estimatedMinutes, language)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (index == 0 && itinerary.transferStop != null) {
                        Surface(
                            color = DesertGold.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = DesertGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = AppStrings.transferAtLabel(
                                        AppStrings.stopName(itinerary.transferStop, language),
                                        language
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DesertGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(
                    onClick = { onRouteClick(firstLeg.route.number) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("trip_detail_${firstLeg.route.number}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.viewRouteAction(language))
                }

                TextButton(
                    onClick = { onTrackRouteOnMap(firstLeg.route.number) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("trip_map_${firstLeg.route.number}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.liveMapButton(language))
                }
            }
        }
    }
}
