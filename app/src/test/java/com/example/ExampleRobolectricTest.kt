package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KuwaitBusData
import com.example.data.LiveTelemetryEngine
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
}
