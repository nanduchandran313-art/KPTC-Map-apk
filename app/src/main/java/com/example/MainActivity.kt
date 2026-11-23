package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.KptcViewModel
import com.example.ui.components.ServiceAlertsNotificationSheet
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.screens.CompanyInfoScreen
import com.example.ui.screens.LiveMapScreen
import com.example.ui.screens.RouteDetailScreen
import com.example.ui.screens.RoutesScreen
import com.example.ui.screens.StationsScreen
import com.example.ui.screens.TripPlannerScreen
import com.example.ui.theme.KptcKuwaitTheme
import kotlinx.coroutines.delay

object KptcDestinations {
    const val ROUTES = "routes"
    const val PLANNER = "planner"
    const val LIVE_MAP = "live_map"
    const val STATIONS = "stations"
    const val COMPANY = "company"
    const val ROUTE_DETAIL = "route_detail/{routeNumber}"

    fun routeDetail(routeNumber: String): String = "route_detail/$routeNumber"
}

private data class TopLevelTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topLevelTabs = listOf(
    TopLevelTab(
        route = KptcDestinations.ROUTES,
        label = "Routes",
        selectedIcon = Icons.Filled.DirectionsBus,
        unselectedIcon = Icons.Outlined.DirectionsBus,
        testTag = "nav_tab_routes"
    ),
    TopLevelTab(
        route = KptcDestinations.PLANNER,
        label = "Planner",
        selectedIcon = Icons.Filled.AltRoute,
        unselectedIcon = Icons.Outlined.AltRoute,
        testTag = "nav_tab_planner"
    ),
    TopLevelTab(
        route = KptcDestinations.LIVE_MAP,
        label = "Live Map",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map,
        testTag = "nav_tab_live_map"
    ),
    TopLevelTab(
        route = KptcDestinations.STATIONS,
        label = "Stations",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn,
        testTag = "nav_tab_stations"
    ),
    TopLevelTab(
        route = KptcDestinations.COMPANY,
        label = "Contact",
        selectedIcon = Icons.Filled.ContactPhone,
        unselectedIcon = Icons.Outlined.ContactPhone,
        testTag = "nav_tab_company"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KptcKuwaitTheme {
                var showOpeningSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1800L)
                    showOpeningSplash = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    KptcKuwaitApp()

                    AnimatedVisibility(
                        visible = showOpeningSplash,
                        exit = fadeOut(animationSpec = tween(420))
                    ) {
                        KptcOpeningLogoSplash()
                    }
                }
            }
        }
    }
}

@Composable
private fun KptcOpeningLogoSplash() {
    val scaleAnim = remember { Animatable(0.96f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(350))
        scaleAnim.animateTo(1f, animationSpec = tween(550))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF163D4D),
                        Color(0xFF285F75),
                        Color(0xFF1E3E4C)
                    )
                )
            )
            .testTag("opening_logo_splash"),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_kptc_opening_poster),
            contentDescription = "KPTC Kuwait Public Transport Company Opening Splash",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        )
    }
}

@Composable
fun KptcKuwaitApp(
    viewModel: KptcViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTopLevel = currentDestination?.route in topLevelTabs.map { it.route }
    val lang = uiState.language
    val layoutDir = if (lang == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr
    var showAlertsSheet by remember { mutableStateOf(false) }

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides lang,
        LocalLayoutDirection provides layoutDir
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavRail = maxWidth >= 600.dp

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (isTopLevel && !useNavRail) {
                        NavigationBar {
                            topLevelTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                                val tabLabel = when (tab.route) {
                                    KptcDestinations.ROUTES -> AppStrings.navRoutes(lang)
                                    KptcDestinations.PLANNER -> AppStrings.navPlanner(lang)
                                    KptcDestinations.LIVE_MAP -> AppStrings.navLiveMap(lang)
                                    KptcDestinations.STATIONS -> AppStrings.navStations(lang)
                                    else -> AppStrings.navCompany(lang)
                                }
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tabLabel
                                        )
                                    },
                                    label = { Text(tabLabel) },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isTopLevel && useNavRail) {
                        NavigationRail {
                            topLevelTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                                val tabLabel = when (tab.route) {
                                    KptcDestinations.ROUTES -> AppStrings.navRoutes(lang)
                                    KptcDestinations.PLANNER -> AppStrings.navPlanner(lang)
                                    KptcDestinations.LIVE_MAP -> AppStrings.navLiveMap(lang)
                                    KptcDestinations.STATIONS -> AppStrings.navStations(lang)
                                    else -> AppStrings.navCompany(lang)
                                }
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tabLabel
                                        )
                                    },
                                    label = { Text(tabLabel) },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = KptcDestinations.ROUTES,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable(KptcDestinations.ROUTES) {
                            RoutesScreen(
                                uiState = uiState,
                                onQueryChange = viewModel::onRouteQueryChange,
                                onFilterTabSelected = viewModel::onFilterTabSelected,
                                onToggleFavorite = viewModel::toggleFavoriteRoute,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onRouteClick = { routeNum ->
                                    navController.navigate(KptcDestinations.routeDetail(routeNum))
                                },
                                onTrackRouteOnMap = { routeNum ->
                                    viewModel.selectMapRoute(routeNum)
                                    navigateToTab(KptcDestinations.LIVE_MAP)
                                },
                                onOpenCompanyInfo = {
                                    navigateToTab(KptcDestinations.COMPANY)
                                },
                                onOpenAlerts = { showAlertsSheet = true },
                                onDismissAlert = viewModel::dismissAlert
                            )
                        }

                        composable(KptcDestinations.PLANNER) {
                            TripPlannerScreen(
                                uiState = uiState,
                                onSelectStartStop = viewModel::selectTripStartStop,
                                onSelectEndStop = viewModel::selectTripEndStop,
                                onSwapStops = viewModel::swapTripStops,
                                onApplyQuickTrip = viewModel::applyQuickTrip,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onRouteClick = { routeNum ->
                                    navController.navigate(KptcDestinations.routeDetail(routeNum))
                                },
                                onTrackRouteOnMap = { routeNum ->
                                    viewModel.selectMapRoute(routeNum)
                                    navigateToTab(KptcDestinations.LIVE_MAP)
                                },
                                onOpenAlerts = { showAlertsSheet = true },
                                onDismissAlert = viewModel::dismissAlert
                            )
                        }

                        composable(KptcDestinations.LIVE_MAP) {
                            LiveMapScreen(
                                uiState = uiState,
                                onSelectRoute = viewModel::selectMapRoute,
                                onSelectBus = viewModel::selectMapBus,
                                onSelectStop = viewModel::selectMapStop,
                                onTogglePreviewOutsideHours = viewModel::setPreviewOutsideHours,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onOpenRouteDetail = { routeNum ->
                                    navController.navigate(KptcDestinations.routeDetail(routeNum))
                                },
                                onRefreshLocation = viewModel::refreshLocationIfPermitted,
                                onOpenAlerts = { showAlertsSheet = true },
                                onDismissAlert = viewModel::dismissAlert
                            )
                        }

                        composable(KptcDestinations.STATIONS) {
                            StationsScreen(
                                uiState = uiState,
                                onStationQueryChange = viewModel::onStationQueryChange,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onRouteClick = { routeNum ->
                                    navController.navigate(KptcDestinations.routeDetail(routeNum))
                                },
                                onRefreshLocation = viewModel::refreshLocationIfPermitted
                            )
                        }

                        composable(KptcDestinations.COMPANY) {
                            CompanyInfoScreen(
                                uiState = uiState,
                                onToggleLanguage = viewModel::toggleLanguage
                            )
                        }

                        composable(
                            route = KptcDestinations.ROUTE_DETAIL,
                            arguments = listOf(navArgument("routeNumber") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val routeNumber = backStackEntry.arguments?.getString("routeNumber").orEmpty()
                            RouteDetailScreen(
                                routeNumber = routeNumber,
                                uiState = uiState,
                                onBack = { navController.popBackStack() },
                                onToggleFavorite = viewModel::toggleFavoriteRoute,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onOpenFullMap = { rNum ->
                                    viewModel.selectMapRoute(rNum)
                                    navController.popBackStack(KptcDestinations.ROUTES, inclusive = false)
                                    navigateToTab(KptcDestinations.LIVE_MAP)
                                },
                                onNavigateToRoute = { targetRoute ->
                                    navController.navigate(KptcDestinations.routeDetail(targetRoute))
                                },
                                onOpenAlerts = { showAlertsSheet = true }
                            )
                        }
                    }
                }
            }

            if (showAlertsSheet) {
                ServiceAlertsNotificationSheet(
                    allAlerts = uiState.serviceAlerts,
                    dismissedIds = uiState.dismissedAlertIds,
                    selectedFilter = uiState.selectedAlertFilter,
                    language = lang,
                    onSelectFilter = viewModel::setAlertFilter,
                    onDismissAlert = viewModel::dismissAlert,
                    onRestoreAll = viewModel::restoreAllAlerts,
                    onRouteClick = { routeNum ->
                        navController.navigate(KptcDestinations.routeDetail(routeNum))
                    },
                    onDismissSheet = { showAlertsSheet = false }
                )
            }
        }
    }
}
