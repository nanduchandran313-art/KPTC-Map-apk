package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KuwaitBusData
import com.example.data.LiveTelemetryEngine
import com.example.data.ServiceAlertEngine
import com.example.data.TripEndpointPoint
import com.example.data.TripItinerary
import com.example.data.TripPlannerEngine
import com.example.data.UserPreferencesRepository
import com.example.location.LocationHelper
import com.example.location.NearbyBusStop
import com.example.location.UserCoordinates
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.model.RouteCategory
import com.example.model.ServiceAlert
import com.example.model.ServiceAlertType
import com.example.model.StationArrival
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RouteFilterTab(val label: String) {
    ALL("All (23)"),
    FAVORITES("Saved"),
    CITY("City"),
    EXPRESS("Express (X)"),
    AIRPORT_MALL("Airport & Malls"),
    COASTAL_SOUTH("Coastal & South")
}

data class KptcUiState(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val allRoutes: List<BusRoute> = KuwaitBusData.routes,
    val filteredRoutes: List<BusRoute> = KuwaitBusData.routes,
    val routeQuery: String = "",
    val selectedFilterTab: RouteFilterTab = RouteFilterTab.ALL,
    val favoriteRouteNumbers: Set<String> = emptySet(),
    val liveBuses: List<LiveBus> = emptyList(),
    val kuwaitClock: LiveTelemetryEngine.KuwaitClockInfo = LiveTelemetryEngine.currentKuwaitClock(),
    val previewOutsideHours: Boolean = true,
    val selectedMapRouteNumber: String? = null,
    val selectedMapBusId: String? = null,
    val selectedMapStopId: String? = null,
    val stationQuery: String = "",
    val filteredStops: List<BusStop> = KuwaitBusData.allStops,
    val userCoordinates: UserCoordinates? = null,
    val closestStops: List<NearbyBusStop> = emptyList(),
    val isFetchingLocation: Boolean = false,
    val tripStartEndpoint: TripEndpointPoint? = null,
    val tripEndEndpoint: TripEndpointPoint? = null,
    val tripStartStop: BusStop? = null,
    val tripEndStop: BusStop? = null,
    val tripRecommendations: List<TripItinerary> = emptyList(),
    val serviceAlerts: List<ServiceAlert> = ServiceAlertEngine.activeAlerts(),
    val dismissedAlertIds: Set<String> = emptySet(),
    val selectedAlertFilter: ServiceAlertType? = null
) {
    val activeServiceAlerts: List<ServiceAlert>
        get() = serviceAlerts.filter { it.id !in dismissedAlertIds }
}

class KptcViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    private val routeQueryFlow = MutableStateFlow("")
    private val filterTabFlow = MutableStateFlow(RouteFilterTab.ALL)
    private val stationQueryFlow = MutableStateFlow("")
    private val selectedMapRouteFlow = MutableStateFlow<String?>(null)
    private val selectedMapBusFlow = MutableStateFlow<String?>(null)
    private val selectedMapStopFlow = MutableStateFlow<String?>(null)
    private val liveBusesFlow = MutableStateFlow<List<LiveBus>>(emptyList())
    private val kuwaitClockFlow = MutableStateFlow(LiveTelemetryEngine.currentKuwaitClock())
    private val userCoordsFlow = MutableStateFlow<UserCoordinates?>(null)
    private val fetchingLocationFlow = MutableStateFlow(false)
    private val tripStartEndpointFlow = MutableStateFlow<TripEndpointPoint?>(null)
    private val tripEndEndpointFlow = MutableStateFlow<TripEndpointPoint?>(null)
    private val serviceAlertsFlow = MutableStateFlow(ServiceAlertEngine.activeAlerts())
    private val dismissedAlertsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val alertFilterFlow = MutableStateFlow<ServiceAlertType?>(null)

    private val favoritesFlow = prefsRepo.favoriteRoutesFlow
    private val previewModeFlow = prefsRepo.livePreviewOutsideHoursFlow
    private val languageFlow = prefsRepo.appLanguageFlow

    private val filterInputsFlow = combine(
        routeQueryFlow,
        filterTabFlow,
        favoritesFlow,
        stationQueryFlow
    ) { rQuery, tab, favs, sQuery ->
        FilterInputs(rQuery, tab, favs, sQuery)
    }

    private val mapSelectionFlow = combine(
        selectedMapRouteFlow,
        selectedMapBusFlow,
        selectedMapStopFlow,
        previewModeFlow,
        languageFlow
    ) { routeNum, busId, stopId, preview, lang ->
        MapSelection(routeNum, busId, stopId, preview, lang)
    }

    private val tripEndpointsFlow = combine(tripStartEndpointFlow, tripEndEndpointFlow) { s, e -> s to e }
    private val alertStateFlow = combine(
        serviceAlertsFlow,
        dismissedAlertsFlow,
        alertFilterFlow
    ) { alerts, dismissed, filter ->
        Triple(alerts, dismissed, filter)
    }

    val uiState: StateFlow<KptcUiState> = combine(
        filterInputsFlow,
        mapSelectionFlow,
        liveBusesFlow,
        kuwaitClockFlow,
        combine(
            combine(userCoordsFlow, fetchingLocationFlow) { coords, loading -> coords to loading },
            combine(tripEndpointsFlow, alertStateFlow) { tripPair, alertTriple -> tripPair to alertTriple }
        ) { locPair, tripAndAlerts -> locPair to tripAndAlerts }
    ) { filters, mapSel, buses, clock, combinedTail ->
        val locationPair = combinedTail.first
        val tripPair = combinedTail.second.first
        val alertTriple = combinedTail.second.second

        val filteredRoutes = filterRoutes(
            routes = KuwaitBusData.routes,
            query = filters.routeQuery,
            tab = filters.tab,
            favorites = filters.favorites
        )

        val userCoords = locationPair.first
        val filteredStops = filterStops(
            stops = KuwaitBusData.allStops,
            query = filters.stationQuery,
            userCoords = userCoords
        )
        val closestStops = if (userCoords != null) {
            LocationHelper.findClosestStops(
                latitude = userCoords.latitude,
                longitude = userCoords.longitude,
                stops = KuwaitBusData.allStops,
                limit = 5
            )
        } else {
            emptyList()
        }

        val startEndpoint = tripPair.first
        val endEndpoint = tripPair.second
        val recommendations = if (startEndpoint != null && endEndpoint != null) {
            TripPlannerEngine.planTripFromEndpoints(
                origin = startEndpoint,
                destination = endEndpoint,
                routes = KuwaitBusData.routes,
                stops = KuwaitBusData.allStops,
                liveBuses = buses
            )
        } else {
            emptyList()
        }

        KptcUiState(
            language = mapSel.language,
            allRoutes = KuwaitBusData.routes,
            filteredRoutes = filteredRoutes,
            routeQuery = filters.routeQuery,
            selectedFilterTab = filters.tab,
            favoriteRouteNumbers = filters.favorites,
            liveBuses = buses,
            kuwaitClock = clock,
            previewOutsideHours = mapSel.previewOutsideHours,
            selectedMapRouteNumber = mapSel.routeNumber,
            selectedMapBusId = mapSel.busId,
            selectedMapStopId = mapSel.stopId,
            stationQuery = filters.stationQuery,
            filteredStops = filteredStops,
            userCoordinates = userCoords,
            closestStops = closestStops,
            isFetchingLocation = locationPair.second,
            tripStartEndpoint = startEndpoint,
            tripEndEndpoint = endEndpoint,
            tripStartStop = startEndpoint?.exactStop,
            tripEndStop = endEndpoint?.exactStop,
            tripRecommendations = recommendations,
            serviceAlerts = alertTriple.first,
            dismissedAlertIds = alertTriple.second,
            selectedAlertFilter = alertTriple.third
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KptcUiState(
            liveBuses = LiveTelemetryEngine.computeLiveBuses(KuwaitBusData.routes, previewModeEnabled = true)
        )
    )

    init {
        startLiveTelemetryTicker()
        refreshLocationIfPermitted()
    }

    private fun startLiveTelemetryTicker() {
        viewModelScope.launch {
            var latestPreview = true
            launch {
                previewModeFlow.collect { latestPreview = it }
            }
            while (isActive) {
                val now = System.currentTimeMillis()
                kuwaitClockFlow.value = LiveTelemetryEngine.currentKuwaitClock()
                liveBusesFlow.value = LiveTelemetryEngine.computeLiveBuses(
                    routes = KuwaitBusData.routes,
                    nowMillis = now,
                    previewModeEnabled = latestPreview
                )
                serviceAlertsFlow.value = ServiceAlertEngine.activeAlerts(now)
                delay(1_000L)
            }
        }
    }

    fun onRouteQueryChange(query: String) {
        routeQueryFlow.value = query
    }

    fun onFilterTabSelected(tab: RouteFilterTab) {
        filterTabFlow.value = tab
    }

    fun onStationQueryChange(query: String) {
        stationQueryFlow.value = query
    }

    fun toggleFavoriteRoute(routeNumber: String) {
        viewModelScope.launch {
            prefsRepo.toggleFavoriteRoute(routeNumber)
        }
    }

    fun setPreviewOutsideHours(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setLivePreviewOutsideHours(enabled)
        }
    }

    fun toggleLanguage() {
        val next = if (uiState.value.language == AppLanguage.ENGLISH) {
            AppLanguage.ARABIC
        } else {
            AppLanguage.ENGLISH
        }
        viewModelScope.launch {
            prefsRepo.setAppLanguage(next)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            prefsRepo.setAppLanguage(language)
        }
    }

    fun selectTripStartStop(stop: BusStop?) {
        tripStartEndpointFlow.value = stop?.let {
            TripEndpointPoint(
                labelEn = it.nameEn,
                labelAr = it.nameAr,
                latitude = it.latitude,
                longitude = it.longitude,
                exactStop = it
            )
        }
    }

    fun selectTripEndStop(stop: BusStop?) {
        tripEndEndpointFlow.value = stop?.let {
            TripEndpointPoint(
                labelEn = it.nameEn,
                labelAr = it.nameAr,
                latitude = it.latitude,
                longitude = it.longitude,
                exactStop = it
            )
        }
    }

    fun resolveAndSelectTripQuery(
        isOrigin: Boolean,
        query: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val localMatch = TripPlannerEngine.resolveQueryToEndpoint(query)
            val resolved = if (localMatch != null) {
                localMatch
            } else {
                val context = getApplication<Application>().applicationContext
                val geocoded = LocationHelper.geocodeKuwaitAddress(context, query)
                TripPlannerEngine.resolveQueryToEndpoint(
                    query = query,
                    geocodedCoords = geocoded
                )
            }
            if (resolved != null) {
                if (isOrigin) {
                    tripStartEndpointFlow.value = resolved
                } else {
                    tripEndEndpointFlow.value = resolved
                }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun useCurrentGpsForTripOrigin() {
        val existing = userCoordsFlow.value
        if (existing != null) {
            tripStartEndpointFlow.value = TripEndpointPoint(
                labelEn = "Current GPS Location",
                labelAr = "موقعي الحالي (GPS)",
                latitude = existing.latitude,
                longitude = existing.longitude,
                exactStop = null,
                isGps = true
            )
            return
        }
        val context = getApplication<Application>().applicationContext
        if (!LocationHelper.hasLocationPermission(context)) return
        viewModelScope.launch {
            fetchingLocationFlow.value = true
            val coords = LocationHelper.fetchCurrentLocation(context)
            fetchingLocationFlow.value = false
            if (coords != null) {
                userCoordsFlow.value = coords
                tripStartEndpointFlow.value = TripEndpointPoint(
                    labelEn = "Current GPS Location",
                    labelAr = "موقعي الحالي (GPS)",
                    latitude = coords.latitude,
                    longitude = coords.longitude,
                    exactStop = null,
                    isGps = true
                )
            }
        }
    }

    fun swapTripStops() {
        val currentStart = tripStartEndpointFlow.value
        val currentEnd = tripEndEndpointFlow.value
        tripStartEndpointFlow.value = currentEnd
        tripEndEndpointFlow.value = currentStart
    }

    fun applyQuickTrip(startId: String, endId: String) {
        selectTripStartStop(KuwaitBusData.stopsMap[startId])
        selectTripEndStop(KuwaitBusData.stopsMap[endId])
    }

    fun dismissAlert(alertId: String) {
        dismissedAlertsFlow.value = dismissedAlertsFlow.value + alertId
    }

    fun restoreAllAlerts() {
        dismissedAlertsFlow.value = emptySet()
    }

    fun setAlertFilter(filter: ServiceAlertType?) {
        alertFilterFlow.value = filter
    }

    fun selectMapRoute(routeNumber: String?) {
        selectedMapRouteFlow.value = routeNumber
        selectedMapBusFlow.value = null
        selectedMapStopFlow.value = null
    }

    fun selectMapBus(busId: String?) {
        selectedMapBusFlow.value = busId
        if (busId != null) {
            selectedMapStopFlow.value = null
        }
    }

    fun selectMapStop(stopId: String?) {
        selectedMapStopFlow.value = stopId
        if (stopId != null) {
            selectedMapBusFlow.value = null
        }
    }

    fun refreshLocationIfPermitted() {
        val context = getApplication<Application>().applicationContext
        if (!LocationHelper.hasLocationPermission(context)) return
        viewModelScope.launch {
            fetchingLocationFlow.value = true
            val coords = LocationHelper.fetchCurrentLocation(context)
            if (coords != null) {
                userCoordsFlow.value = coords
            }
            fetchingLocationFlow.value = false
        }
    }

    fun getRoute(routeNumber: String): BusRoute? = KuwaitBusData.routesByNumber[routeNumber]

    fun arrivalsForStop(stop: BusStop): List<StationArrival> {
        return LiveTelemetryEngine.arrivalsForStop(
            stop = stop,
            routes = KuwaitBusData.routes,
            liveBuses = uiState.value.liveBuses
        )
    }

    private fun filterRoutes(
        routes: List<BusRoute>,
        query: String,
        tab: RouteFilterTab,
        favorites: Set<String>
    ): List<BusRoute> = filterRoutesList(routes, query, tab, favorites)

    companion object {
        fun filterRoutesList(
            routes: List<BusRoute>,
            query: String,
            tab: RouteFilterTab = RouteFilterTab.ALL,
            favorites: Set<String> = emptySet()
        ): List<BusRoute> {
            val trimmed = query.trim().lowercase()
            return routes.filter { route ->
                val matchesTab = when (tab) {
                    RouteFilterTab.ALL -> true
                    RouteFilterTab.FAVORITES -> route.number in favorites
                    RouteFilterTab.CITY -> route.category == RouteCategory.CITY
                    RouteFilterTab.EXPRESS -> route.category == RouteCategory.EXPRESS
                    RouteFilterTab.AIRPORT_MALL -> route.category == RouteCategory.AIRPORT_MALL
                    RouteFilterTab.COASTAL_SOUTH -> route.category == RouteCategory.COASTAL_SOUTH ||
                        route.category == RouteCategory.INTER_GOVERNORATE
                }
                if (!matchesTab) return@filter false
                if (trimmed.isEmpty()) return@filter true

                val arabicInfo = com.example.ui.localization.AppStrings.arabicRoutes[route.number]
                route.number.lowercase().contains(trimmed) ||
                    route.titleEn.lowercase().contains(trimmed) ||
                    route.origin.lowercase().contains(trimmed) ||
                    route.destination.lowercase().contains(trimmed) ||
                    route.corridorSummary.lowercase().contains(trimmed) ||
                    (arabicInfo?.titleAr?.contains(trimmed) == true) ||
                    (arabicInfo?.originAr?.contains(trimmed) == true) ||
                    (arabicInfo?.destinationAr?.contains(trimmed) == true) ||
                    (arabicInfo?.corridorSummaryAr?.contains(trimmed) == true) ||
                    route.stops.any {
                        it.nameEn.lowercase().contains(trimmed) ||
                            it.area.lowercase().contains(trimmed) ||
                            it.nameAr.contains(trimmed)
                    }
            }
        }
    }

    private fun filterStops(
        stops: List<BusStop>,
        query: String,
        userCoords: UserCoordinates?
    ): List<BusStop> {
        val trimmed = query.trim().lowercase()
        val matching = if (trimmed.isEmpty()) {
            stops
        } else {
            stops.filter { stop ->
                stop.nameEn.lowercase().contains(trimmed) ||
                    stop.area.lowercase().contains(trimmed) ||
                    stop.landmark.lowercase().contains(trimmed) ||
                    stop.nameAr.contains(trimmed) ||
                    KuwaitBusData.routesServingStop(stop.id).any { it.number.lowercase().contains(trimmed) }
            }
        }

        val refLat = userCoords?.latitude ?: KuwaitBusData.KUWAIT_CITY_LAT
        val refLng = userCoords?.longitude ?: KuwaitBusData.KUWAIT_CITY_LNG

        return matching.sortedBy {
            LocationHelper.haversineKm(refLat, refLng, it.latitude, it.longitude)
        }
    }

    private data class FilterInputs(
        val routeQuery: String,
        val tab: RouteFilterTab,
        val favorites: Set<String>,
        val stationQuery: String
    )

    private data class MapSelection(
        val routeNumber: String?,
        val busId: String?,
        val stopId: String?,
        val previewOutsideHours: Boolean,
        val language: AppLanguage
    )
}
