package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Payments
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ServiceAlertEngine
import com.example.model.BusRoute
import com.example.model.ServiceAlert
import com.example.model.ServiceAlertType
import com.example.ui.KptcUiState
import com.example.ui.RouteFilterTab
import com.example.ui.components.KptcPassPromoCard
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.components.RouteBadgePill
import com.example.ui.components.ServiceAlertBanner
import com.example.ui.components.ServiceAlertBellButton
import com.example.ui.components.TelemetryStatusBadge
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.DesertGold
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KuwaitEmerald

@Composable
fun RoutesScreen(
    uiState: KptcUiState,
    onQueryChange: (String) -> Unit,
    onFilterTabSelected: (RouteFilterTab) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onRouteClick: (String) -> Unit,
    onTrackRouteOnMap: (String) -> Unit,
    onOpenCompanyInfo: () -> Unit = {},
    onOpenAlerts: () -> Unit = {},
    onDismissAlert: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = uiState.language

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
        ) {
            // Pinned Top Bar: Compact Official Logo + Language Switcher + Top Search Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kptc_brand_logo_banner"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_kptc_official_logo),
                                contentDescription = "KPTC Kuwait Public Transport Company",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .height(42.dp)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        ServiceAlertBellButton(
                            activeCount = uiState.activeServiceAlerts.size,
                            language = lang,
                            onClick = onOpenAlerts
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable(onClick = onOpenCompanyInfo)
                                .testTag("open_company_info_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ContactPhone,
                                    contentDescription = AppStrings.companyDetailsTitle(lang),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        LanguageSwitchPill(
                            language = lang,
                            onToggle = onToggleLanguage
                        )
                    }

                    // Pinned Search Bar at the Top of Route List Screen
                    OutlinedTextField(
                        value = uiState.routeQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("routes_search_input"),
                        placeholder = {
                            Text(
                                text = AppStrings.searchRoutesPlaceholder(lang),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search bus routes",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (uiState.routeQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onQueryChange("") },
                                    modifier = Modifier.testTag("routes_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search"
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

                    if (uiState.routeQuery.isNotBlank()) {
                        Text(
                            text = AppStrings.routesFoundCount(
                                uiState.filteredRoutes.size,
                                uiState.allRoutes.size,
                                lang
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("routes_search_count")
                        )
                    }
                }
            }

            // Scrollable Category Filters, Hero Card (collapses while searching), and Route List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Filter Chips
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(RouteFilterTab.entries) { tab ->
                            val selected = uiState.selectedFilterTab == tab
                            FilterChip(
                                selected = selected,
                                onClick = { onFilterTabSelected(tab) },
                                label = {
                                    val countSuffix = if (tab == RouteFilterTab.FAVORITES && uiState.favoriteRouteNumbers.isNotEmpty()) {
                                        " (${uiState.favoriteRouteNumbers.size})"
                                    } else {
                                        ""
                                    }
                                    Text("${AppStrings.filterTabLabel(tab, lang)}$countSuffix")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("filter_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }

                // Real-Time Service Alert Banner
                if (uiState.activeServiceAlerts.isNotEmpty()) {
                    item {
                        ServiceAlertBanner(
                            alerts = uiState.activeServiceAlerts,
                            language = lang,
                            onOpenNotifications = onOpenAlerts,
                            onDismissAlert = onDismissAlert,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Hero Banner Card (auto-collapses when typing a search query for immediate results)
                item {
                    AnimatedVisibility(visible = uiState.routeQuery.isBlank()) {
                        KptcHeroCard(
                            language = lang,
                            kuwaitTime = uiState.kuwaitClock.formattedTime,
                            isServiceOperating = uiState.kuwaitClock.isServiceOperating,
                            activeBusCount = uiState.liveBuses.size,
                            routeCount = uiState.allRoutes.size
                        )
                    }
                }

                // KPTC PASS Side Advertising Banner (medium side poster + subtle animations)
                item {
                    AnimatedVisibility(visible = uiState.routeQuery.isBlank()) {
                        KptcPassPromoCard(
                            language = lang,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Empty State
                if (uiState.filteredRoutes.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = AppStrings.noMatchingRoutesTitle(lang),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = AppStrings.noMatchingRoutesDesc(lang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Bus Route Cards
                items(uiState.filteredRoutes, key = { it.number }) { route ->
                    val liveCount = uiState.liveBuses.count { it.routeNumber == route.number }
                    val routeAlert = ServiceAlertEngine.alertsForRoute(
                        route.number,
                        uiState.activeServiceAlerts
                    ).firstOrNull()
                    BusRouteCard(
                        route = route,
                        language = lang,
                        liveBusCount = liveCount,
                        isFavorite = route.number in uiState.favoriteRouteNumbers,
                        activeAlert = routeAlert,
                        onToggleFavorite = { onToggleFavorite(route.number) },
                        onClick = { onRouteClick(route.number) },
                        onTrackLiveClick = { onTrackRouteOnMap(route.number) },
                        onAlertClick = onOpenAlerts,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KptcHeroCard(
    language: AppLanguage,
    kuwaitTime: String,
    isServiceOperating: Boolean,
    activeBusCount: Int,
    routeCount: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("kptc_services_hero_card"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(2.dp, KptcRoyalBlue),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_kptc_main_logo),
                contentDescription = "KPTC Kuwait Public Transport Company",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(228.dp)
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                KptcRoyalBlue,
                                Color(0xFF073259)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = AppStrings.stateOfKuwaitRoutes(routeCount, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = kuwaitTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }

                TelemetryStatusBadge(
                    isServiceOperating = isServiceOperating,
                    activeBusCount = activeBusCount
                )
            }
        }
    }
}

@Composable
private fun BusRouteCard(
    route: BusRoute,
    language: AppLanguage,
    liveBusCount: Int,
    isFavorite: Boolean,
    activeAlert: ServiceAlert?,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    onTrackLiveClick: () -> Unit,
    onAlertClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("route_card_${route.number}"),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RouteBadgePill(
                    routeNumber = route.number,
                    badgeColor = route.badgeColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = AppStrings.routeTitle(route, language),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (route.number.endsWith("X")) {
                            Surface(
                                color = ExpressCrimson.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = AppStrings.expressTag(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ExpressCrimson,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = AppStrings.routeMetaLine(route, language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("favorite_route_${route.number}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove route ${route.number} from saved" else "Save route ${route.number}",
                        tint = if (isFavorite) ExpressCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (activeAlert != null) {
                val alertColor = when (activeAlert.type) {
                    ServiceAlertType.ROAD_CLOSURE -> ExpressCrimson
                    ServiceAlertType.DELAY -> DesertGold
                    ServiceAlertType.MAINTENANCE -> KptcRoyalBlue
                }
                Surface(
                    color = alertColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onAlertClick)
                        .testTag("route_card_alert_${route.number}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = AppStrings.alertTypeLabel(activeAlert.type, language),
                            tint = alertColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${AppStrings.alertTypeLabel(activeAlert.type, language)}: ${AppStrings.alertTitle(activeAlert, language)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = alertColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Text(
                text = AppStrings.routeCorridor(route, language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                            text = AppStrings.everyMinutes(route.frequencyMinutes, language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
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
                            text = AppStrings.fareAmount(route.fareFils, language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        color = KuwaitEmerald.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = AppStrings.liveCountChip(liveBusCount, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = KuwaitEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                TextButton(
                    onClick = onTrackLiveClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("track_live_${route.number}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.liveMapButton(language))
                }
            }
        }
    }
}
