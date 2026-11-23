package com.example.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.model.ServiceAlert
import com.example.model.ServiceAlertType
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.DesertGold
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KuwaitEmerald
import kotlinx.coroutines.delay

private fun alertAccentColor(type: ServiceAlertType): Color = when (type) {
    ServiceAlertType.ROAD_CLOSURE -> ExpressCrimson
    ServiceAlertType.DELAY -> DesertGold
    ServiceAlertType.MAINTENANCE -> KptcRoyalBlue
}

private fun alertIcon(type: ServiceAlertType): ImageVector = when (type) {
    ServiceAlertType.ROAD_CLOSURE -> Icons.Default.Warning
    ServiceAlertType.DELAY -> Icons.Default.Schedule
    ServiceAlertType.MAINTENANCE -> Icons.Default.Build
}

@Composable
fun ServiceAlertBellButton(
    activeCount: Int,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (activeCount > 0) ExpressCrimson.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50),
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("notifications_bell_button")
    ) {
        Box(contentAlignment = Alignment.Center) {
            BadgedBox(
                badge = {
                    if (activeCount > 0) {
                        Badge(
                            containerColor = ExpressCrimson,
                            contentColor = Color.White
                        ) {
                            Text(activeCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (activeCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                    contentDescription = AppStrings.serviceAlertsTitle(language),
                    tint = if (activeCount > 0) ExpressCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceAlertBanner(
    alerts: List<ServiceAlert>,
    language: AppLanguage,
    onOpenNotifications: () -> Unit,
    onDismissAlert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (alerts.isEmpty()) return

    var currentIndex by remember(alerts.size) { mutableIntStateOf(0) }
    LaunchedEffect(alerts.size) {
        while (alerts.size > 1) {
            delay(5_500L)
            currentIndex = (currentIndex + 1) % alerts.size
        }
    }

    val safeIndex = currentIndex.coerceIn(0, alerts.lastIndex)
    val alert = alerts[safeIndex]
    val accent = alertAccentColor(alert.type)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenNotifications)
            .testTag("service_alert_banner"),
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.10f),
        tonalElevation = 1.dp
    ) {
        AnimatedContent(targetState = alert, label = "service_alert_cycle") { item ->
            val itemAccent = alertAccentColor(item.type)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(itemAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = alertIcon(item.type),
                                contentDescription = AppStrings.alertTypeLabel(item.type, language),
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Surface(
                            color = itemAccent,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = AppStrings.alertTypeLabel(item.type, language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${safeIndex + 1}/${alerts.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppStrings.viewAllAlertsAction(alerts.size, language),
                            style = MaterialTheme.typography.labelMedium,
                            color = itemAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenNotifications)
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                        IconButton(
                            onClick = { onDismissAlert(item.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("dismiss_banner_alert_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = AppStrings.dismissAlertAction(language),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = AppStrings.alertTitle(item, language),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = AppStrings.alertMessage(item, language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    item.affectedRouteNumbers.forEach { routeNum ->
                        val route = KuwaitBusData.routesByNumber[routeNum]
                        RouteBadgePill(
                            routeNumber = routeNum,
                            badgeColor = route?.badgeColor ?: KptcRoyalBlue,
                            compact = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RouteSpecificAlertCard(
    alerts: List<ServiceAlert>,
    language: AppLanguage,
    onOpenAllAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (alerts.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        alerts.forEach { alert ->
            val accent = alertAccentColor(alert.type)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenAllAlerts)
                    .testTag("route_alert_card_${alert.id}"),
                shape = RoundedCornerShape(16.dp),
                color = accent.copy(alpha = 0.11f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = alertIcon(alert.type),
                            contentDescription = AppStrings.alertTypeLabel(alert.type, language),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.alertTypeLabel(alert.type, language),
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = alert.updatedTimeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = AppStrings.alertTitle(alert, language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.alertMessage(alert, language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ServiceAlertsNotificationSheet(
    allAlerts: List<ServiceAlert>,
    dismissedIds: Set<String>,
    selectedFilter: ServiceAlertType?,
    language: AppLanguage,
    onSelectFilter: (ServiceAlertType?) -> Unit,
    onDismissAlert: (String) -> Unit,
    onRestoreAll: () -> Unit,
    onRouteClick: (String) -> Unit,
    onDismissSheet: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val visibleAlerts = remember(allAlerts, dismissedIds, selectedFilter) {
        allAlerts.filter { alert ->
            alert.id !in dismissedIds && (selectedFilter == null || alert.type == selectedFilter)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissSheet,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 18.dp)
                .testTag("service_alerts_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.serviceAlertsTitle(language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppStrings.serviceAlertsSubtitle(
                            allAlerts.count { it.id !in dismissedIds },
                            language
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (dismissedIds.isNotEmpty()) {
                    TextButton(
                        onClick = onRestoreAll,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("restore_alerts_button")
                    ) {
                        Text(AppStrings.restoreAlertsAction(language))
                    }
                }
            }

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onSelectFilter(null) },
                        label = { Text(AppStrings.allAlertsFilter(language)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("alert_filter_all")
                    )
                }
                items(ServiceAlertType.entries) { type ->
                    val selected = selectedFilter == type
                    val accent = alertAccentColor(type)
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectFilter(if (selected) null else type) },
                        label = { Text(AppStrings.alertTypeFilterLabel(type, language)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accent,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("alert_filter_${type.name.lowercase()}")
                    )
                }
            }

            if (visibleAlerts.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = KuwaitEmerald.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = KuwaitEmerald,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = AppStrings.noActiveAlertsTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = AppStrings.noActiveAlertsDesc(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(visibleAlerts, key = { it.id }) { alert ->
                        val accent = alertAccentColor(alert.type)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("alert_item_${alert.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = accent.copy(alpha = 0.16f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = alertIcon(alert.type),
                                                    contentDescription = null,
                                                    tint = accent,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = AppStrings.alertTypeLabel(alert.type, language),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = accent,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = alert.updatedTimeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDismissAlert(alert.id) },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.dismissAlertAction(language),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = AppStrings.alertTitle(alert, language),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = AppStrings.alertCorridor(alert, language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = accent
                                )

                                Text(
                                    text = AppStrings.alertMessage(alert, language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

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
                                    alert.affectedRouteNumbers.forEach { routeNum ->
                                        val route = KuwaitBusData.routesByNumber[routeNum]
                                        RouteBadgePill(
                                            routeNumber = routeNum,
                                            badgeColor = route?.badgeColor ?: KptcRoyalBlue,
                                            compact = true,
                                            onClick = {
                                                onDismissSheet()
                                                onRouteClick(routeNum)
                                            }
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
