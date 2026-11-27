package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.data.ServiceAlertEngine
import com.example.model.KuwaitRoadCondition
import com.example.model.KuwaitTrafficSummary
import com.example.model.RoadConditionStatus
import com.example.ui.KptcUiState
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.RouteBadgePill
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.DesertGold
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KuwaitEmerald

private val BlockedAmber = Color(0xFFD97706)

private fun roadStatusColor(status: RoadConditionStatus): Color = when (status) {
    RoadConditionStatus.CLOSED -> ExpressCrimson
    RoadConditionStatus.BLOCKED -> BlockedAmber
    RoadConditionStatus.HEAVY_TRAFFIC -> DesertGold
    RoadConditionStatus.OPEN_SMOOTH -> KuwaitEmerald
}

private fun roadStatusIcon(status: RoadConditionStatus): ImageVector = when (status) {
    RoadConditionStatus.CLOSED -> Icons.Default.Block
    RoadConditionStatus.BLOCKED -> Icons.Default.Warning
    RoadConditionStatus.HEAVY_TRAFFIC -> Icons.Default.Traffic
    RoadConditionStatus.OPEN_SMOOTH -> Icons.Default.CheckCircle
}

private fun roadStatusLabel(status: RoadConditionStatus, lang: AppLanguage): String = when (status) {
    RoadConditionStatus.CLOSED -> AppStrings.kuwaitRoadClosedLabel(lang)
    RoadConditionStatus.BLOCKED -> AppStrings.kuwaitRoadBlockedLabel(lang)
    RoadConditionStatus.HEAVY_TRAFFIC -> AppStrings.kuwaitHeavyTrafficLabel(lang)
    RoadConditionStatus.OPEN_SMOOTH -> AppStrings.kuwaitRoadOpenLabel(lang)
}

@Composable
fun KuwaitRoadTrafficScreen(
    uiState: KptcUiState,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onRouteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isAr = AppStrings.isArabic(lang)
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<RoadConditionStatus?>(null) }

    val minuteBucket = uiState.kuwaitClock.secondsOfDay / 60
    val summary: KuwaitTrafficSummary = remember(minuteBucket) {
        ServiceAlertEngine.kuwaitTrafficSummary()
    }
    val allRoads: List<KuwaitRoadCondition> = remember(minuteBucket) {
        ServiceAlertEngine.kuwaitRoadConditions()
    }

    val filteredRoads = remember(allRoads, searchQuery, selectedStatus) {
        val q = searchQuery.trim().lowercase()
        allRoads.filter { road ->
            val matchesStatus = selectedStatus == null || road.status == selectedStatus
            if (!matchesStatus) return@filter false
            if (q.isEmpty()) return@filter true
            road.roadNameEn.lowercase().contains(q) ||
                road.roadNameAr.contains(q) ||
                road.sectionEn.lowercase().contains(q) ||
                road.sectionAr.contains(q) ||
                road.detourOrNoteEn.lowercase().contains(q) ||
                road.detourOrNoteAr.contains(q) ||
                road.affectedRouteNumbers.any { it.lowercase().contains(q) }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("traffic_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isAr) "رجوع" else "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppStrings.kuwaitTrafficMenuTitle(lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageSwitchPill(
                        language = lang,
                        onToggle = onToggleLanguage
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Live Kuwait Traffic Time Hero Card
                item {
                    KuwaitTrafficHeroCard(
                        summary = summary,
                        liveClockFormatted = uiState.kuwaitClock.formattedTime,
                        language = lang,
                        onFilterPick = { status ->
                            selectedStatus = if (selectedStatus == status) null else status
                        }
                    )
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kuwait_traffic_search_input"),
                        placeholder = {
                            Text(
                                text = AppStrings.kuwaitTrafficSearchPlaceholder(lang),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = AppStrings.kuwaitTrafficMenuTitle(lang),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.testTag("kuwait_traffic_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = if (isAr) "مسح البحث" else "Clear search"
                                    )
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

                // Status Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedStatus == null,
                                onClick = { selectedStatus = null },
                                label = {
                                    Text("${AppStrings.kuwaitTrafficFilterAll(lang)} (${allRoads.size})")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("traffic_filter_all")
                            )
                        }
                        items(RoadConditionStatus.entries) { status ->
                            val selected = selectedStatus == status
                            val accent = roadStatusColor(status)
                            val labelText = when (status) {
                                RoadConditionStatus.CLOSED -> AppStrings.kuwaitTrafficFilterClosed(lang)
                                RoadConditionStatus.BLOCKED -> AppStrings.kuwaitTrafficFilterBlocked(lang)
                                RoadConditionStatus.HEAVY_TRAFFIC -> AppStrings.kuwaitTrafficFilterHeavy(lang)
                                RoadConditionStatus.OPEN_SMOOTH -> AppStrings.kuwaitTrafficFilterOpen(lang)
                            }
                            val count = allRoads.count { it.status == status }
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedStatus = if (selected) null else status
                                },
                                label = { Text("$labelText ($count)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accent,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("traffic_filter_${status.name.lowercase()}")
                            )
                        }
                    }
                }

                if (filteredRoads.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
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
                                    imageVector = Icons.Default.Traffic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = AppStrings.kuwaitNoMatchingRoadsTitle(lang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = AppStrings.kuwaitNoMatchingRoadsDesc(lang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(filteredRoads, key = { it.id }) { road ->
                        KuwaitRoadConditionCard(
                            road = road,
                            language = lang,
                            onRouteClick = onRouteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KuwaitTrafficHeroCard(
    summary: KuwaitTrafficSummary,
    liveClockFormatted: String,
    language: AppLanguage,
    onFilterPick: (RoadConditionStatus) -> Unit
) {
    val isAr = AppStrings.isArabic(language)
    val periodText = if (isAr) summary.trafficPeriodAr else summary.trafficPeriodEn
    val nextRushText = if (isAr) summary.nextRushWindowAr else summary.nextRushWindowEn

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kuwait_traffic_hero_card"),
        shape = RoundedCornerShape(22.dp),
        color = KptcRoyalBlue,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(KptcRoyalBlue, Color(0xFF062C4E))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.kuwaitTimeNowLabel(language),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.80f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = liveClockFormatted,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Traffic,
                            contentDescription = null,
                            tint = Color(0xFFDEDF2A),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = periodText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = nextRushText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.92f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrafficSummaryStatTile(
                    value = summary.closedCount.toString(),
                    label = AppStrings.kuwaitTrafficFilterClosed(language),
                    accent = Color(0xFFFF8A8A),
                    onClick = { onFilterPick(RoadConditionStatus.CLOSED) },
                    modifier = Modifier.weight(1f)
                )
                TrafficSummaryStatTile(
                    value = summary.blockedCount.toString(),
                    label = AppStrings.kuwaitTrafficFilterBlocked(language),
                    accent = Color(0xFFFFD080),
                    onClick = { onFilterPick(RoadConditionStatus.BLOCKED) },
                    modifier = Modifier.weight(1f)
                )
                TrafficSummaryStatTile(
                    value = if (isAr) "+${summary.avgExtraMinutes} د" else "+${summary.avgExtraMinutes}m",
                    label = AppStrings.kuwaitAvgDelayLabel(language),
                    accent = Color(0xFF9DE4FF),
                    onClick = { onFilterPick(RoadConditionStatus.HEAVY_TRAFFIC) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TrafficSummaryStatTile(
    value: String,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 58.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.13f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.90f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KuwaitRoadConditionCard(
    road: KuwaitRoadCondition,
    language: AppLanguage,
    onRouteClick: (String) -> Unit
) {
    val isAr = AppStrings.isArabic(language)
    val accent = roadStatusColor(road.status)
    val statusTitle = roadStatusLabel(road.status, language)
    val roadName = if (isAr) road.roadNameAr else road.roadNameEn
    val section = if (isAr) road.sectionAr else road.sectionEn
    val detourOrNote = if (isAr) road.detourOrNoteAr else road.detourOrNoteEn
    val trafficWindow = if (isAr) road.trafficTimeLabelAr else road.trafficTimeLabelEn

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("road_condition_card_${road.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = accent.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = roadStatusIcon(road.status),
                                contentDescription = statusTitle,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    }
                }

                Surface(
                    color = if (road.delayMinutes > 0) accent.copy(alpha = 0.12f) else KuwaitEmerald.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (road.delayMinutes > 0) {
                            AppStrings.kuwaitExtraDelayChip(road.delayMinutes, language)
                        } else {
                            AppStrings.kuwaitNoDelayChip(language)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (road.delayMinutes > 0) accent else KuwaitEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = roadName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = section,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = trafficWindow,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = detourOrNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (road.affectedRouteNumbers.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = AppStrings.affectedRoutesLabel(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    road.affectedRouteNumbers.forEach { routeNum ->
                        val route = KuwaitBusData.routesByNumber[routeNum]
                        RouteBadgePill(
                            routeNumber = routeNum,
                            badgeColor = route?.badgeColor ?: KptcRoyalBlue,
                            compact = true,
                            onClick = { onRouteClick(routeNum) }
                        )
                    }
                }
            }
        }
    }
}
