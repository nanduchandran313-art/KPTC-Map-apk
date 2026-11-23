package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KuwaitBusData
import com.example.data.LiveTelemetryEngine
import com.example.data.UserPreferencesRepository
import com.example.location.LocationHelper
import com.example.location.UserCoordinates
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.model.RouteCategory
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
    val isFetchingLocation: Boolean = false
)

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

    val uiState: StateFlow<KptcUiState> = combine(
        filterInputsFlow,
        mapSelectionFlow,
        liveBusesFlow,
        kuwaitClockFlow,
        combine(userCoordsFlow, fetchingLocationFlow) { coords, loading -> coords to loading }
    ) { filters, mapSel, buses, clock, locationPair ->
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
            isFetchingLocation = locationPair.second
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
