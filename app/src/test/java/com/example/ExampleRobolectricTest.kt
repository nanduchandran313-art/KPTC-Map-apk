package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KuwaitBusData
import com.example.data.LiveTelemetryEngine
import com.example.data.TripPlannerEngine
import com.example.model.BusDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches KPTC Kuwait`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("KPTC Kuwait", appName)
    }

    @Test
    fun `all 23 requested KPTC routes exist with valid Kuwait coordinates`() {
        val expectedRoutes = setOf(
            "11", "12", "13", "15", "16", "18", "21", "23", "51",
            "66", "66X", "77", "77X", "999", "103", "106", "106X",
            "102X", "102A", "507", "507S", "A2", "105"
        )
        val actualRoutes = KuwaitBusData.routes.map { it.number }.toSet()
        assertEquals(expectedRoutes, actualRoutes)

        KuwaitBusData.routes.forEach { route ->
            assertEquals("Route ${route.number} fare should be 250 Fils", 250, route.fareFils)
            assertTrue("Route ${route.number} should have >= 5 stops", route.stops.size >= 5)
            route.stops.forEach { stop ->
                assertTrue("Stop ${stop.id} lat in Kuwait", stop.latitude in 29.0..29.5)
                assertTrue("Stop ${stop.id} lng in Kuwait", stop.longitude in 47.5..48.3)
            }
        }
    }

    @Test
    fun `live telemetry computes active buses across all routes`() {
        val liveBuses = LiveTelemetryEngine.computeLiveBuses(
            routes = KuwaitBusData.routes,
            previewModeEnabled = true
        )
        assertTrue("Live buses should be active", liveBuses.size >= 46)
        val coveredRoutes = liveBuses.map { it.routeNumber }.toSet()
        assertEquals(23, coveredRoutes.size)
    }

    @Test
    fun `arabic translations cover all 23 routes`() {
        KuwaitBusData.routes.forEach { route ->
            val ar = com.example.ui.localization.AppStrings.arabicRoutes[route.number]
            assertTrue("Route ${route.number} should have Arabic title", ar != null && ar.titleAr.isNotBlank())
        }
    }

    @Test
    fun `route search filters by number and destination in English and Arabic`() {
        val byNum = com.example.ui.KptcViewModel.filterRoutesList(KuwaitBusData.routes, "507")
        assertTrue(byNum.any { it.number == "507" } && byNum.any { it.number == "507S" })

        val byAirport = com.example.ui.KptcViewModel.filterRoutesList(KuwaitBusData.routes, "Airport")
        assertTrue(byAirport.any { it.number == "13" })

        val byArabicDest = com.example.ui.KptcViewModel.filterRoutesList(KuwaitBusData.routes, "الفحيحيل")
        assertTrue(byArabicDest.any { it.number == "999" } && byArabicDest.any { it.number == "102A" })
    }

    @Test
    fun `trip planner recommends direct outbound and inbound routes`() {
        val mirqab = KuwaitBusData.stopsMap.getValue("mirqab")
        val airport = KuwaitBusData.stopsMap.getValue("airport_t1")
        val liveBuses = LiveTelemetryEngine.computeLiveBuses(KuwaitBusData.routes, previewModeEnabled = true)

        val outbound = TripPlannerEngine.planTrip(mirqab, airport, KuwaitBusData.routes, liveBuses)
        val route13Out = outbound.firstOrNull { it.isDirect && it.legs.first().route.number == "13" }
        assertTrue("Route 13 should connect Mirqab -> Airport directly", route13Out != null)
        assertEquals(250, route13Out?.totalFareFils)

        val inbound = TripPlannerEngine.planTrip(airport, mirqab, KuwaitBusData.routes, liveBuses)
        val route13In = inbound.firstOrNull { it.isDirect && it.legs.first().route.number == "13" }
        assertTrue("Route 13 should connect Airport -> Mirqab in reverse", route13In != null)
        assertEquals(BusDirection.INBOUND, route13In?.legs?.first()?.direction)
    }

    @Test
    fun `trip planner recommends multiple direct routes and 1-transfer itineraries`() {
        val maliya = KuwaitBusData.stopsMap.getValue("maliya")
        val fahaheel = KuwaitBusData.stopsMap.getValue("fahaheel_green_tower")
        val southRoutes = TripPlannerEngine.planTrip(maliya, fahaheel)
        val directNums = southRoutes.filter { it.isDirect }.map { it.legs.first().route.number }.toSet()
        assertTrue(setOf("102X", "999", "102A").all { it in directNums })

        val jahra = KuwaitBusData.stopsMap.getValue("jahra_coop")
        val airport = KuwaitBusData.stopsMap.getValue("airport_t1")
        val transferTrips = TripPlannerEngine.planTrip(jahra, airport)
        assertTrue("Jahra -> Airport should return 1-transfer recommendations", transferTrips.isNotEmpty())
        assertTrue(transferTrips.all { !it.isDirect && it.transferStop != null && it.totalFareFils == 500 })
    }

    @Test
    fun `company contact details match official KPTC info`() {
        assertEquals("P.O.Box 375 Safat, 13004 Kuwait", com.example.ui.localization.AppStrings.KPTC_PO_BOX_EN)
        assertEquals("22328866", com.example.ui.localization.AppStrings.KPTC_TEL)
        assertEquals("22328870", com.example.ui.localization.AppStrings.KPTC_FAX)
        assertEquals("1880001", com.example.ui.localization.AppStrings.KPTC_HOTLINE)
        assertEquals("info@kptc.com.kw", com.example.ui.localization.AppStrings.KPTC_EMAIL)
    }
}
