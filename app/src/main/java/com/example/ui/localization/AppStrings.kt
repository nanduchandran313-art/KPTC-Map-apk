package com.example.ui.localization

import androidx.compose.runtime.compositionLocalOf
import com.example.model.BusDirection
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.OccupancyLevel
import com.example.model.RouteCategory
import com.example.ui.RouteFilterTab

enum class AppLanguage(val code: String, val displayLabel: String) {
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

data class RouteArabicText(
    val titleAr: String,
    val originAr: String,
    val destinationAr: String,
    val corridorSummaryAr: String,
    val operationNotesAr: String
)

object AppStrings {

    fun isArabic(lang: AppLanguage): Boolean = lang == AppLanguage.ARABIC

    // Navigation Tabs
    fun navRoutes(lang: AppLanguage) = if (isArabic(lang)) "الخطوط" else "Routes"
    fun navPlanner(lang: AppLanguage) = if (isArabic(lang)) "الرحلات" else "Planner"
    fun navLiveMap(lang: AppLanguage) = if (isArabic(lang)) "الخريطة" else "Live Map"
    fun navStations(lang: AppLanguage) = if (isArabic(lang)) "المحطات" else "Stations"
    fun navCompany(lang: AppLanguage) = if (isArabic(lang)) "تواصل" else "Contact"

    // Company Details & Contact Us Constants
    const val KPTC_PO_BOX_EN = "P.O.Box 375 Safat, 13004 Kuwait"
    const val KPTC_PO_BOX_AR = "ص.ب 375 الصفاة، 13004 الكويت"
    const val KPTC_TEL = "22328866"
    const val KPTC_FAX = "22328870"
    const val KPTC_HOTLINE = "1880001"
    const val KPTC_EMAIL = "info@kptc.com.kw"

    fun companyDetailsTitle(lang: AppLanguage) =
        if (isArabic(lang)) "الشركة والتواصل" else "Company & Contact"
    fun companyDetailsSubtitle(lang: AppLanguage) =
        if (isArabic(lang)) "شركة النقل العام الكويتية (KPTC)" else "Kuwait Public Transport Company (KPTC)"
    fun companyFullName(lang: AppLanguage) =
        if (isArabic(lang)) "شركة النقل العام الكويتية (ش.م.ك)" else "Kuwait Public Transport Company"
    fun companyOverview(lang: AppLanguage) = if (isArabic(lang)) {
        "المشغّل الوطني للنقل العام بالحافلات في دولة الكويت، يخدم جميع المحافظات والمناطق السكنية والتجارية والمطار على مدار اليوم بتعرفة موحدة 250 فلس."
    } else {
        "Kuwait's national public bus operator connecting Kuwait City, Hawally, Salmiya, Farwaniya, Jahra, Fahaheel, and Kuwait International Airport with a uniform 250 Fils fare."
    }
    fun contactUsTitle(lang: AppLanguage) = if (isArabic(lang)) "اتصل بنا" else "Contact Us"
    fun poBoxLabel(lang: AppLanguage) = if (isArabic(lang)) "العنوان البريدي" else "P.O. Box"
    fun poBoxDisplay(lang: AppLanguage) =
        if (isArabic(lang)) "$KPTC_PO_BOX_AR\n$KPTC_PO_BOX_EN" else KPTC_PO_BOX_EN
    fun telLabel(lang: AppLanguage) = if (isArabic(lang)) "الهاتف (Tel)" else "Tel"
    fun faxLabel(lang: AppLanguage) = if (isArabic(lang)) "الفاكس (Fax)" else "Fax"
    fun hotLineLabel(lang: AppLanguage) = if (isArabic(lang)) "الخط الساخن (Hot Line)" else "Hot Line"
    fun emailLabel(lang: AppLanguage) = if (isArabic(lang)) "البريد الإلكتروني" else "Email"
    fun headOfficeHoursLabel(lang: AppLanguage) =
        if (isArabic(lang)) "ساعات خدمة العملاء" else "Customer Support Hours"
    fun headOfficeHoursValue(lang: AppLanguage) = if (isArabic(lang)) {
        "الخط الساخن يومياً 04:20 ص – 11:00 م • الإدارة: الأحد–الخميس"
    } else {
        "Hot Line Daily 04:20 AM – 11:00 PM • HQ Sun–Thu"
    }
    fun callActionLabel(lang: AppLanguage) = if (isArabic(lang)) "اتصال" else "Call"
    fun emailActionLabel(lang: AppLanguage) = if (isArabic(lang)) "إرسال" else "Email"
    fun copyActionDesc(label: String, lang: AppLanguage) =
        if (isArabic(lang)) "نسخ $label" else "Copy $label"
    fun copiedFeedback(label: String, lang: AppLanguage) =
        if (isArabic(lang)) "تم نسخ $label" else "$label copied"
    fun noDialerFeedback(lang: AppLanguage) =
        if (isArabic(lang)) "لا يوجد تطبيق اتصال متاح على هذا الجهاز" else "No phone app available on this device"
    fun noEmailAppFeedback(lang: AppLanguage) =
        if (isArabic(lang)) "لا يوجد تطبيق بريد إلكتروني متاح" else "No email app available on this device"
    fun serviceHighlightsTitle(lang: AppLanguage) =
        if (isArabic(lang)) "معلومات التشغيل والخدمات" else "Service Overview"
    fun standardFareTitle(lang: AppLanguage) = if (isArabic(lang)) "التعرفة الموحدة" else "Standard Fare"
    fun operatingHoursTitle(lang: AppLanguage) = if (isArabic(lang)) "ساعات التشغيل (الكويت)" else "Operating Hours"
    fun operatingHoursValue(lang: AppLanguage) =
        if (isArabic(lang)) "04:20 صباحاً – 11:00 مساءً" else "04:20 AM – 11:00 PM AST"
    fun networkCoverageTitle(lang: AppLanguage) = if (isArabic(lang)) "تغطية الشبكة" else "Network Coverage"
    fun networkCoverageValue(routeCount: Int, stopCount: Int, lang: AppLanguage) =
        if (isArabic(lang)) "$routeCount خطاً • $stopCount محطة" else "$routeCount Routes • $stopCount Stops"

    // Service Alerts & Notifications
    fun serviceAlertsTitle(lang: AppLanguage) =
        if (isArabic(lang)) "تنبيهات الخدمة المباشرة" else "Live Service Alerts"
    fun serviceAlertsSubtitle(count: Int, lang: AppLanguage) =
        if (isArabic(lang)) "$count تنبيهات نشطة للطرق والخطوط" else "$count active route & road updates"
    fun allAlertsFilter(lang: AppLanguage) = if (isArabic(lang)) "الكل" else "All"
    fun alertTypeLabel(type: com.example.model.ServiceAlertType, lang: AppLanguage): String = if (isArabic(lang)) {
        when (type) {
            com.example.model.ServiceAlertType.ROAD_CLOSURE -> "إغلاق طريق"
            com.example.model.ServiceAlertType.DELAY -> "تأخير"
            com.example.model.ServiceAlertType.MAINTENANCE -> "صيانة"
        }
    } else {
        when (type) {
            com.example.model.ServiceAlertType.ROAD_CLOSURE -> "ROAD CLOSURE"
            com.example.model.ServiceAlertType.DELAY -> "DELAY"
            com.example.model.ServiceAlertType.MAINTENANCE -> "MAINTENANCE"
        }
    }
    fun alertTypeFilterLabel(type: com.example.model.ServiceAlertType, lang: AppLanguage): String = if (isArabic(lang)) {
        when (type) {
            com.example.model.ServiceAlertType.ROAD_CLOSURE -> "إغلاق طرق"
            com.example.model.ServiceAlertType.DELAY -> "تأخيرات"
            com.example.model.ServiceAlertType.MAINTENANCE -> "صيانة"
        }
    } else {
        when (type) {
            com.example.model.ServiceAlertType.ROAD_CLOSURE -> "Closures"
            com.example.model.ServiceAlertType.DELAY -> "Delays"
            com.example.model.ServiceAlertType.MAINTENANCE -> "Maintenance"
        }
    }
    fun alertTitle(alert: com.example.model.ServiceAlert, lang: AppLanguage) =
        if (isArabic(lang)) alert.titleAr else alert.titleEn
    fun alertMessage(alert: com.example.model.ServiceAlert, lang: AppLanguage) =
        if (isArabic(lang)) alert.messageAr else alert.messageEn
    fun alertCorridor(alert: com.example.model.ServiceAlert, lang: AppLanguage) =
        if (isArabic(lang)) alert.corridorAr else alert.corridorEn
    fun viewAllAlertsAction(count: Int, lang: AppLanguage) =
        if (isArabic(lang)) "عرض الكل ($count)" else "View All ($count)"
    fun dismissAlertAction(lang: AppLanguage) = if (isArabic(lang)) "إخفاء" else "Dismiss"
    fun restoreAlertsAction(lang: AppLanguage) = if (isArabic(lang)) "استعادة الكل" else "Restore All"
    fun noActiveAlertsTitle(lang: AppLanguage) =
        if (isArabic(lang)) "جميع الخطوط تعمل بشكل منتظم" else "All KPTC routes running normally"
    fun noActiveAlertsDesc(lang: AppLanguage) =
        if (isArabic(lang)) "لا توجد تنبيهات نشطة في هذا التصنيف." else "No active alerts in this category."
    fun affectedRoutesLabel(lang: AppLanguage) =
        if (isArabic(lang)) "الخطوط المتأثرة:" else "Affected routes:"

    // Trip Planner Screen
    fun plannerTitle(lang: AppLanguage) = if (isArabic(lang)) "مخطط الرحلة" else "Trip Planner"
    fun plannerSubtitle(lang: AppLanguage) =
        if (isArabic(lang)) "اختر نقطة الانطلاق والوجهة لاقتراح أفضل خطوط KPTC" else "Select start and destination stops for recommended KPTC routes"
    fun startPointLabel(lang: AppLanguage) = if (isArabic(lang)) "نقطة الانطلاق" else "Start Point"
    fun destinationPointLabel(lang: AppLanguage) = if (isArabic(lang)) "نقطة الوصول (الوجهة)" else "Destination"
    fun chooseStartHint(lang: AppLanguage) = if (isArabic(lang)) "اضغط لاختيار محطة الانطلاق" else "Tap to select start station"
    fun chooseDestHint(lang: AppLanguage) = if (isArabic(lang)) "اضغط لاختيار محطة الوصول" else "Tap to select destination station"
    fun popularTripsLabel(lang: AppLanguage) = if (isArabic(lang)) "رحلات شائعة في الكويت" else "Popular Kuwait Trips"
    fun recommendedRoutesHeader(count: Int, lang: AppLanguage) =
        if (isArabic(lang)) "الخطوط المقترحة ($count)" else "Recommended Routes ($count)"
    fun directBadge(lang: AppLanguage) = if (isArabic(lang)) "مباشر" else "DIRECT"
    fun oneTransferBadge(lang: AppLanguage) = if (isArabic(lang)) "تبديل واحد" else "1 TRANSFER"
    fun stopsHopLabel(stops: Int, lang: AppLanguage) =
        if (isArabic(lang)) "$stops محطة" else "$stops stops"
    fun transferAtLabel(stopName: String, lang: AppLanguage) =
        if (isArabic(lang)) "التبديل في: $stopName" else "Transfer at: $stopName"
    fun nextBusInMin(mins: Int, lang: AppLanguage) =
        if (isArabic(lang)) "الباص التالي خلال $mins د" else "Next bus in $mins min"
    fun boardRouteAt(routeNum: String, from: String, to: String, lang: AppLanguage) =
        if (isArabic(lang)) "اركب خط $routeNum من $from إلى $to" else "Board Route $routeNum at $from → $to"
    fun sameStopWarning(lang: AppLanguage) =
        if (isArabic(lang)) "يرجى اختيار نقطة وصول مختلفة عن نقطة الانطلاق." else "Please select a destination different from your start point."
    fun noTripFoundTitle(lang: AppLanguage) =
        if (isArabic(lang)) "لم يتم العثور على خط مباشر أو مشترك" else "No connecting route found"
    fun noTripFoundDesc(lang: AppLanguage) =
        if (isArabic(lang)) "جرّب اختيار محطة رئيسية قريبة مثل المرقاب أو المالية أو السالمية." else "Try selecting a nearby major hub such as Mirqab, Maliya, or Salmiya."
    fun selectPointsPromptTitle(lang: AppLanguage) =
        if (isArabic(lang)) "خطط رحلتك في الكويت" else "Plan Your Kuwait Bus Trip"
    fun selectPointsPromptDesc(lang: AppLanguage) =
        if (isArabic(lang)) "اختر محطة الانطلاق ومحطة الوصول أعلاه أو جرّب إحدى الرحلات الشائعة." else "Pick a start station and destination above, or tap a popular Kuwait trip below."
    fun viewRouteAction(lang: AppLanguage) = if (isArabic(lang)) "تفاصيل الخط" else "Route Details"
    fun pickerTitleStart(lang: AppLanguage) = if (isArabic(lang)) "اختر نقطة الانطلاق" else "Choose Start Station or Address"
    fun pickerTitleDest(lang: AppLanguage) = if (isArabic(lang)) "اختر نقطة الوصول" else "Choose Destination or Address"
    fun addressOrCoordsPlaceholder(lang: AppLanguage) =
        if (isArabic(lang)) "أدخل محطة أو عنواناً في الكويت أو إحداثيات (29.36, 47.97)..." else "Search stop, Kuwait address, or coords (29.36, 47.97)..."
    fun useMyGpsLabel(lang: AppLanguage) = if (isArabic(lang)) "موقعي GPS" else "My GPS"
    fun currentGpsPointLabel(lang: AppLanguage) = if (isArabic(lang)) "موقعي الحالي (GPS)" else "Current GPS Location"
    fun useCustomLocationAction(query: String, lang: AppLanguage) =
        if (isArabic(lang)) "استخدام العنوان / الإحداثيات: $query" else "Use Address / Coordinates: $query"
    fun addressNotResolvedWarning(lang: AppLanguage) =
        if (isArabic(lang)) "لم يتم العثور على هذا العنوان في الكويت. جرّب اسم منطقة أو إحداثيات مثل 29.367, 47.978" else "Address not found in Kuwait. Try an area name or coordinates like 29.367, 47.978"
    fun walkToBoardStop(meters: Int, stopName: String, lang: AppLanguage) =
        if (isArabic(lang)) "امشِ ~$meters م إلى $stopName" else "Walk ~${meters}m to $stopName"
    fun walkFromAlightStop(meters: Int, stopName: String, lang: AppLanguage) =
        if (isArabic(lang)) "انزل في $stopName وامشِ ~$meters م للوجهة" else "Alight at $stopName (~${meters}m walk)"

    // Closest Stops GPS Overlay & Route Share
    fun closestStopsOverlayTitle(lang: AppLanguage) =
        if (isArabic(lang)) "أقرب المحطات لموقعك" else "Closest Stops to You"
    fun enableGpsOverlayCta(lang: AppLanguage) =
        if (isArabic(lang)) "تفعيل GPS لأقرب المحطات" else "Find Closest Stops (GPS)"
    fun shareRouteDescription(lang: AppLanguage) =
        if (isArabic(lang)) "مشاركة تفاصيل الخط" else "Share route info"
    fun shareRouteChooserTitle(lang: AppLanguage) =
        if (isArabic(lang)) "مشاركة خط KPTC عبر" else "Share KPTC Route via"

    // KPTC PASS Promotional Ad Card
    fun passAdBadge(lang: AppLanguage) =
        if (isArabic(lang)) "عرض خاص" else "KPTC PASS"
    fun passAdHeadline(lang: AppLanguage) =
        if (isArabic(lang)) "توفير التنقل اليومي" else "Unlimited Travel Pass"
    fun passAdPlansSummary(lang: AppLanguage) =
        if (isArabic(lang)) "10 د.ك / شهر  •  25 د.ك / 91 يوماً" else "10 KD / 1 Mo  •  25 KD / 91 Days"
    fun passAdFeaturesLine(lang: AppLanguage) =
        if (isArabic(lang)) "رحلات غير محدودة • جميع الخطوط (24)" else "Unlimited rides • All 24 routes"
    fun passAdSubscribeCta(lang: AppLanguage) =
        if (isArabic(lang)) "اشترك الآن 1880001" else "Subscribe 1880001"
    fun passAdPosterDesc(lang: AppLanguage) =
        if (isArabic(lang)) "إعلان بطاقة الاشتراك KPTC PASS" else "KPTC PASS unlimited travel poster"
    fun passAdCloseAction(lang: AppLanguage) =
        if (isArabic(lang)) "إغلاق" else "Close"

    // KPTC Bus Advertising (Advertise Now) Card
    fun busAdBadge(lang: AppLanguage) =
        if (isArabic(lang)) "أعلن الآن" else "ADVERTISE NOW"
    fun busAdHeadline(lang: AppLanguage) =
        if (isArabic(lang)) "أعلن على باصات KPTC" else "Advertise on KPTC Buses"
    fun busAdSubheadline(lang: AppLanguage) =
        if (isArabic(lang)) "اجعل علامتك تصل لجميع ركاب الكويت" else "Reach daily commuters across Kuwait"
    fun busAdFeaturesLine(lang: AppLanguage) =
        if (isArabic(lang)) "احجز مساحتك الإعلانية على الحافلات" else "Rent premium bus ad space"
    fun busAdCta(lang: AppLanguage) =
        if (isArabic(lang)) "احجز الآن 1880001" else "Book Ad 1880001"
    fun busAdPosterDesc(lang: AppLanguage) =
        if (isArabic(lang)) "ملصق أعلن الآن على باصات KPTC" else "KPTC Advertise Now bus wrap poster"

    // KPTC "Always Ready to Move." Fleet Ad & 10s Side Flash Banner
    fun alwaysReadyAdBadge(lang: AppLanguage) =
        if (isArabic(lang)) "أسطول KPTC" else "KPTC FLEET"
    fun alwaysReadyAdHeadline(lang: AppLanguage) =
        if (isArabic(lang)) "جاهزون دائماً للانطلاق." else "Always Ready to Move."
    fun alwaysReadyAdSubheadline(lang: AppLanguage) =
        if (isArabic(lang)) "أسطول الكويت الحديث • @kptckuwait" else "Modern Kuwait Fleet • @kptckuwait"
    fun alwaysReadyAdFeaturesLine(lang: AppLanguage) =
        if (isArabic(lang)) "www.kptc.com.kw • خدمة يومية موثوقة" else "www.kptc.com.kw • Daily Kuwait service"
    fun alwaysReadyAdCta(lang: AppLanguage) =
        if (isArabic(lang)) "تواصل 1880001" else "Info 1880001"
    fun alwaysReadyAdPosterDesc(lang: AppLanguage) =
        if (isArabic(lang)) "إعلان أسطول باصات الكويت KPTC جاهزون دائماً للانطلاق" else "KPTC Always Ready to Move bus fleet poster"

    fun buildRouteShareText(
        route: BusRoute,
        activeAlerts: List<com.example.model.ServiceAlert>,
        lang: AppLanguage
    ): String {
        val title = routeTitle(route, lang)
        val origin = routeOrigin(route, lang)
        val dest = routeDestination(route, lang)
        val corridor = routeCorridor(route, lang)
        val alertSection = if (activeAlerts.isNotEmpty()) {
            val firstAlert = activeAlerts.first()
            "\n⚠️ ${alertTypeLabel(firstAlert.type, lang)}: ${alertTitle(firstAlert, lang)}"
        } else {
            ""
        }
        return if (isArabic(lang)) {
            "🚌 خط KPTC رقم ${route.number}: $title\n" +
                "📍 المسار: $origin ↔ $dest\n" +
                "🛣️ المحطات: $corridor\n" +
                "🎫 التعرفة: ${fareAmount(route.fareFils, lang)} • ${everyMinutes(route.frequencyMinutes, lang)}\n" +
                "🕒 التشغيل: ${route.firstBus} – ${route.lastBus}$alertSection\n" +
                "شركة النقل العام الكويتية (KPTC)"
        } else {
            "🚌 KPTC Kuwait Route ${route.number}: $title\n" +
                "📍 Path: $origin ↔ $dest\n" +
                "🛣️ Corridor: $corridor\n" +
                "🎫 Fare: ${fareAmount(route.fareFils, lang)} • ${everyMinutes(route.frequencyMinutes, lang)}\n" +
                "🕒 Hours: ${route.firstBus} – ${route.lastBus}$alertSection\n" +
                "Kuwait Public Transport Company (KPTC)"
        }
    }

    // Filter Tabs
    fun filterTabLabel(tab: RouteFilterTab, lang: AppLanguage): String = if (isArabic(lang)) {
        when (tab) {
            RouteFilterTab.ALL -> "الكل (24)"
            RouteFilterTab.FAVORITES -> "المحفوظة"
            RouteFilterTab.CITY -> "المدينة"
            RouteFilterTab.EXPRESS -> "سريع (X)"
            RouteFilterTab.AIRPORT_MALL -> "المطار والمجمعات"
            RouteFilterTab.COASTAL_SOUTH -> "الساحل والجنوب"
        }
    } else {
        tab.label
    }

    fun categoryLabel(category: RouteCategory, lang: AppLanguage): String = if (isArabic(lang)) {
        when (category) {
            RouteCategory.CITY -> "خط مدينة"
            RouteCategory.EXPRESS -> "خط سريع (X)"
            RouteCategory.AIRPORT_MALL -> "المطار والمجمعات"
            RouteCategory.COASTAL_SOUTH -> "الساحل والجنوب"
            RouteCategory.INTER_GOVERNORATE -> "بين المحافظات"
        }
    } else {
        category.label
    }

    fun directionLabel(dir: BusDirection, lang: AppLanguage): String = if (isArabic(lang)) {
        when (dir) {
            BusDirection.OUTBOUND -> "ذهاب"
            BusDirection.INBOUND -> "عودة"
        }
    } else {
        dir.label
    }

    fun occupancyLabel(level: OccupancyLevel, lang: AppLanguage): String = if (isArabic(lang)) {
        when (level) {
            OccupancyLevel.LOW -> "مقاعد متوفرة"
            OccupancyLevel.MODERATE -> "متوسط"
            OccupancyLevel.HIGH -> "مزدحم"
        }
    } else {
        level.label
    }

    // Hero & Routes Screen
    fun stateOfKuwaitRoutes(routeCount: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "دولة الكويت • $routeCount خطاً" else "STATE OF KUWAIT • $routeCount ROUTES"

    fun heroTitle(lang: AppLanguage): String =
        if (isArabic(lang)) "شركة النقل العام الكويتية" else "Kuwait Public Transport"

    fun heroSubtitle(lang: AppLanguage): String =
        if (isArabic(lang)) "خطوط الباصات، المحطات، المواعيد والتتبع المباشر" else "Routes, Stations, Schedules & Live Fleet"

    fun searchRoutesPlaceholder(lang: AppLanguage): String =
        if (isArabic(lang)) "ابحث برقم الخط أو الوجهة (مثل 507، 102X، السالمية)..." else "Search by route number or destination (e.g. 507, Airport)..."

    fun routesFoundCount(count: Int, total: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "عرض $count من أصل $total خطاً" else "Showing $count of $total routes"

    fun noMatchingRoutesTitle(lang: AppLanguage): String =
        if (isArabic(lang)) "لا توجد خطوط مطابقة" else "No matching KPTC routes"

    fun noMatchingRoutesDesc(lang: AppLanguage): String =
        if (isArabic(lang)) "جرّب البحث برقم خط مثل 11 أو 507 أو 102X أو A2، أو اسم منطقة مثل السالمية أو المطار أو الفحيحيل." else "Try searching a route number like 11, 507, 102X, A2, or a Kuwait area like Salmiya, Airport, or Fahaheel."

    fun expressTag(lang: AppLanguage): String = if (isArabic(lang)) "سريع" else "EXPRESS"

    fun routeMetaLine(route: BusRoute, lang: AppLanguage): String = if (isArabic(lang)) {
        "${route.stops.size} محطة • ${route.distanceKm} كم • ~${route.estimatedDurationMinutes} د"
    } else {
        "${route.stops.size} stations • ${route.distanceKm} km • ~${route.estimatedDurationMinutes} min"
    }

    fun everyMinutes(mins: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "كل $mins د" else "Every ${mins}m"

    fun fareAmount(fils: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$fils فلس" else "$fils Fils"

    fun liveCountChip(count: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$count مباشر" else "$count live"

    fun liveMapButton(lang: AppLanguage): String =
        if (isArabic(lang)) "الخريطة" else "Live Map"

    // Telemetry Badge & Dialog
    fun telemetryLabel(isOperating: Boolean, count: Int, lang: AppLanguage): String = if (isArabic(lang)) {
        if (isOperating) "تتبع مباشر مجدول ($count)" else "معاينة الجدول المباشر ($count)"
    } else {
        if (isOperating) "Schedule-Synced Live ($count)" else "Live Schedule Preview ($count)"
    }

    fun telemetryDialogTitle(lang: AppLanguage): String =
        if (isArabic(lang)) "التتبع المباشر لباصات الكويت KPTC" else "Kuwait KPTC Live Telemetry"

    fun telemetryDialogBody(lang: AppLanguage): String = if (isArabic(lang)) {
        "لا توفر شركة النقل العام الكويتية (KPTC) واجهة برمجية عامة لبيانات GPS الحية. " +
            "يتم حساب مواقع الباصات والسرعة وأوقات الوصول المتوقعة في هذا التطبيق بشكل فوري عبر إحداثيات المحطات الفعلية في الكويت، " +
            "بالتزامن مع ساعات التشغيل الرسمية بتوقيت الكويت (04:20 صباحاً – 11:00 مساءً) وترددات كل خط."
    } else {
        "KPTC Kuwait does not publish a public real-time GPS API. " +
            "Live bus locations, speeds, and arrival ETAs in this app are calculated in real time " +
            "along each route's actual Kuwait GPS stop coordinates, synchronized to official " +
            "Asia/Kuwait (AST) operating hours (04:20 AM – 11:00 PM) and route headways."
    }

    fun gotIt(lang: AppLanguage): String = if (isArabic(lang)) "حسناً" else "Got it"

    // Live Map Screen
    fun liveMapHeader(lang: AppLanguage): String =
        if (isArabic(lang)) "خريطة باصات الكويت الحية" else "Kuwait Live Bus Map"

    fun nightDepotNotice(lang: AppLanguage): String = if (isArabic(lang)) {
        "ساعات التوقف الليلي في الكويت (23:00–04:20). عرض محاكاة النهار:"
    } else {
        "Night Depot Hours in Kuwait (23:00–04:20). Preview daytime live fleet:"
    }

    fun all23Routes(lang: AppLanguage): String =
        if (isArabic(lang)) "كل الـ 24 خطاً" else "All 24 Routes"

    fun mapOverlayLegend(focusedRoute: BusRoute?, busCount: Int, lang: AppLanguage): String = if (isArabic(lang)) {
        if (focusedRoute != null) "خط ${focusedRoute.number} • $busCount باص مباشر" else "شبكة الكويت • $busCount باص نشط"
    } else {
        if (focusedRoute != null) "Route ${focusedRoute.number} • $busCount live" else "Kuwait Network • $busCount active buses"
    }

    fun kuwaitBayLabel(lang: AppLanguage): String = if (isArabic(lang)) "جون الكويت" else "KUWAIT BAY"

    fun toDestination(dest: String, lang: AppLanguage): String =
        if (isArabic(lang)) "إلى $dest" else "To $dest"

    fun nextStopEta(stopName: String, etaMin: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "المحطة التالية: $stopName ($etaMin د)" else "Next: $stopName ($etaMin min)"

    fun speedKmh(speed: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$speed كم/س" else "$speed km/h"

    fun viewRouteDetailsButton(routeNumber: String, lang: AppLanguage): String =
        if (isArabic(lang)) "عرض محطات وتشغيل خط $routeNumber" else "View Route $routeNumber Stops & Operations"

    fun busEtaShort(routeNumber: String, destination: String, etaMin: Int, lang: AppLanguage): Pair<String, String> =
        if (isArabic(lang)) {
            "باص $routeNumber ← $destination" to "$etaMin د"
        } else {
            "Bus $routeNumber → $destination" to "$etaMin min"
        }

    // Stations Screen
    fun stationsTitle(lang: AppLanguage): String =
        if (isArabic(lang)) "محطات باصات الكويت" else "Kuwait Bus Stations"

    fun stationsSubtitle(routeCount: Int, stopCount: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$routeCount خطاً من KPTC عبر $stopCount محطة رئيسية في الكويت" else "$routeCount KPTC routes across $stopCount major Kuwait hubs & stops"

    fun gpsSortedTitle(hasGps: Boolean, lang: AppLanguage): String = if (isArabic(lang)) {
        if (hasGps) "مرتبة حسب موقعك الحالي" else "مرتبة من المرقاب / مدينة الكويت"
    } else {
        if (hasGps) "Sorted by your GPS location" else "Sorted from Mirqab / Kuwait City"
    }

    fun gpsSortedDesc(hasGps: Boolean, lang: AppLanguage): String = if (isArabic(lang)) {
        if (hasGps) "المسافة المباشرة إلى كل محطة في الكويت" else "اسمح بالوصول للموقع للترتيب حسب الأقرب"
    } else {
        if (hasGps) "Live distance to every Kuwait stop" else "Grant location to sort by nearest stop"
    }

    fun gpsButtonLabel(hasGps: Boolean, lang: AppLanguage): String = if (isArabic(lang)) {
        if (hasGps) "تحديث" else "تحديد الموقع"
    } else {
        if (hasGps) "Refresh" else "Use GPS"
    }

    fun searchStationsPlaceholder(lang: AppLanguage): String =
        if (isArabic(lang)) "ابحث عن محطة أو منطقة (السالمية، الجليب، المطار) أو خط..." else "Search station, area (Salmiya, Jleeb, Airport) or route..."

    fun liveNextArrivals(lang: AppLanguage): String =
        if (isArabic(lang)) "الباصات القادمة مباشرة" else "LIVE NEXT ARRIVALS"

    fun routeToEtaRow(routeNumber: String, destination: String, etaMin: Int, lang: AppLanguage): Pair<String, String> =
        if (isArabic(lang)) {
            "خط $routeNumber ← $destination" to "$etaMin د"
        } else {
            "Route $routeNumber → $destination" to "$etaMin min"
        }

    // Route Detail Screen
    fun expandMap(lang: AppLanguage): String = if (isArabic(lang)) "تكبير الخريطة" else "Expand Map"
    fun liveBusesHeader(dirLabel: String, count: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "الباصات النشطة ($dirLabel) • $count مباشر" else "Live Buses ($dirLabel) • $count active"
    fun outsideOperatingNote(lang: AppLanguage): String =
        if (isArabic(lang)) "خارج أوقات التشغيل (فعّل المعاينة في الخريطة الحية لعرض الأسطول)." else "Outside operating hours (enable preview in Live Map to view simulated fleet)."
    fun operationsHeader(lang: AppLanguage): String =
        if (isArabic(lang)) "التشغيل والمواعيد" else "Operations & Schedule"
    fun firstBusLabel(lang: AppLanguage): String = if (isArabic(lang)) "أول باص" else "First Bus"
    fun lastBusLabel(lang: AppLanguage): String = if (isArabic(lang)) "آخر باص" else "Last Bus"
    fun headwayLabel(lang: AppLanguage): String = if (isArabic(lang)) "التكرار" else "Headway"
    fun headwayValue(freq: Int, peak: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$freq د (الذروة $peak د)" else "$freq min (Peak ${peak}m)"
    fun fareLabel(lang: AppLanguage): String = if (isArabic(lang)) "التعرفة" else "Fare"
    fun tripTimeLabel(lang: AppLanguage): String = if (isArabic(lang)) "مدة الرحلة" else "Trip Time"
    fun tripTimeValue(mins: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "~$mins د" else "~$mins min"
    fun distanceLabel(lang: AppLanguage): String = if (isArabic(lang)) "المسافة" else "Distance"
    fun distanceValue(km: Double, lang: AppLanguage): String =
        if (isArabic(lang)) "$km كم" else "$km km"
    fun ramadanHoursText(hours: String, lang: AppLanguage): String =
        if (isArabic(lang)) "مواعيد رمضان: $hours" else "Ramadan Hours: $hours"
    fun stationSequenceHeader(count: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "ترتيب المحطات ($count محطة)" else "Station Sequence ($count stops)"
    fun tapBadgeHint(lang: AppLanguage): String =
        if (isArabic(lang)) "اضغط الرقم لتبديل الخط" else "Tap badge to switch route"
    fun stopsCountLabel(count: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "$count محطة" else "$count stops"
    fun nextStopShort(stopName: String, lang: AppLanguage): String =
        if (isArabic(lang)) "التالي: $stopName" else "Next: $stopName"
    fun etaShort(mins: Int, lang: AppLanguage): String =
        if (isArabic(lang)) "الوصول $mins د" else "ETA $mins min"

    // Localized Stop & Route helpers
    fun stopName(stop: BusStop, lang: AppLanguage): String =
        if (isArabic(lang)) stop.nameAr else stop.nameEn

    fun stopSecondaryLine(stop: BusStop, lang: AppLanguage): String =
        if (isArabic(lang)) "${stop.nameEn} • ${stop.area}" else "${stop.nameAr} • ${stop.area}"

    fun routeTitle(route: BusRoute, lang: AppLanguage): String =
        if (isArabic(lang)) arabicRoutes[route.number]?.titleAr ?: route.titleEn else route.titleEn

    fun routeOrigin(route: BusRoute, lang: AppLanguage): String =
        if (isArabic(lang)) arabicRoutes[route.number]?.originAr ?: route.origin else route.origin

    fun routeDestination(route: BusRoute, lang: AppLanguage): String =
        if (isArabic(lang)) arabicRoutes[route.number]?.destinationAr ?: route.destination else route.destination

    fun routeCorridor(route: BusRoute, lang: AppLanguage): String =
        if (isArabic(lang)) arabicRoutes[route.number]?.corridorSummaryAr ?: route.corridorSummary else route.corridorSummary

    fun routeNotes(route: BusRoute, lang: AppLanguage): String =
        if (isArabic(lang)) arabicRoutes[route.number]?.operationNotesAr ?: route.operationNotes else route.operationNotes

    val arabicRoutes: Map<String, RouteArabicText> = mapOf(
        "11" to RouteArabicText(
            titleAr = "شارع الشهداء – كراج النقل العام",
            originAr = "شارع الشهداء",
            destinationAr = "كراج النقل العام",
            corridorSummaryAr = "شارع الشهداء • شرق • دسمان • المرقاب • الصفاة • المالية • الشيراتون • طريق الجهراء 80 • ميناء الشويخ • الصباح • دوار الأمم • النهضة • الصليبيخات • أمغرة",
            operationNotesAr = "المسار الكامل المكون من 29 محطة من شارع الشهداء مروراً بشرق والمرقاب والمالية وطريق الجهراء 80 وميناء الشويخ ومنطقة الصباح ودوار الأمم والنهضة والصليبيخات حتى كراج النقل العام في أمغرة."
        ),
        "12" to RouteArabicText(
            titleAr = "مدينة الكويت – الصليبية الصناعية",
            originAr = "شارع الشهداء",
            destinationAr = "الصليبية الصناعية",
            corridorSummaryAr = "شارع الشهداء • المرقاب • المالية • الشويخ الصناعية • دوار الأمم • الصليبية",
            operationNotesAr = "يخدم محطات مدينة الكويت المركزية وصولاً إلى المناطق الصناعية والمخازن في الصليبية."
        ),
        "13" to RouteArabicText(
            titleAr = "المرقاب – مطار الكويت الدولي",
            originAr = "المرقاب",
            destinationAr = "مطار الكويت",
            corridorSummaryAr = "المرقاب • المتحف العلمي • المالية • الشويخ • جامعة الكويت • الخالدية • سوق الجمعة • حديقة الحيوان • العمرية • خيطان • الفروانية • طريق الغزالي 60 • المطار",
            operationNotesAr = "المسار الكامل المكون من 32 محطة عبر طريق المطار 55 والغزالي 60 من المرقاب مروراً بالشويخ والجامعة والخالدية والري والعمرية وخيطان والفروانية إلى المطار."
        ),
        "15" to RouteArabicText(
            titleAr = "الشيراتون – السالمية",
            originAr = "الشيراتون",
            destinationAr = "السالمية",
            corridorSummaryAr = "الشيراتون • المالية • برج التحرير • المرقاب • شرق • بنيد القار • الدسمة • الدعية • الشعب • ميدان حولي • شارع عمان • بوليفارد • سالم المبارك • السالمية",
            operationNotesAr = "المسار الكامل المكون من 41 محطة من الشيراتون والمالية مروراً بأبراج مدينة الكويت وبنيد القار والدعية والشعب وميدان حولي وشارع عمان وبوليفارد وشارع سالم المبارك والجامعة الأمريكية حتى السالمية."
        ),
        "16" to RouteArabicText(
            titleAr = "الشيراتون – دوار جليب الشيوخ",
            originAr = "الشيراتون",
            destinationAr = "دوار جليب الشيوخ",
            corridorSummaryAr = "الشيراتون • المالية • المرقاب • كيبكو • الحمراء • بنيد القار • الدسمة • الدعية • حولي • الروضة • العديلية • قرطبة • خيطان • الفروانية • الضجيج • الجليب",
            operationNotesAr = "المسار الكامل المكون من 52 محطة من الشيراتون والمالية مروراً بأبراج مدينة الكويت وبنيد القار والدعية وحولي والروضة والعديلية وقرطبة وخيطان والفروانية والضجيج إلى دوار الجليب."
        ),
        "18" to RouteArabicText(
            titleAr = "المرقاب – دوار الأمم المتحدة",
            originAr = "المرقاب",
            destinationAr = "دوار الأمم المتحدة",
            corridorSummaryAr = "المرقاب • دروازة • شارع القاهرة • شارع تونس • دوار صادق حولي • الدائري الرابع • رويال حياة • طريق المطار 55 • كندا دراي • تويوتا • الصباح • دوار الأمم",
            operationNotesAr = "يربط المرقاب ودروازة عبر شارع تونس في حولي والدائري الرابع ورويال حياة والمنطقة الصناعية في الشويخ ومستشفيات الصباح إلى دوار الأمم المتحدة."
        ),
        "21" to RouteArabicText(
            titleAr = "المرقاب – دوار جليب الشيوخ",
            originAr = "المرقاب",
            destinationAr = "دوار جليب الشيوخ",
            corridorSummaryAr = "المرقاب • المتحف العلمي • المالية • طريق الجهراء • نادي الكويت • سيتي سنتر • طريق المطار • لولو • العمرية • الفروانية • الضجيج • الجليب",
            operationNotesAr = "مسار من 20 محطة من المرقاب عبر سيتي سنتر الشويخ ولولو الري والعمرية وجمعية الفروانية ومترو وهوليدي إن والضجيج والعباسية إلى دوار الجليب."
        ),
        "23" to RouteArabicText(
            titleAr = "المرقاب – صبحان",
            originAr = "المرقاب",
            destinationAr = "صبحان",
            corridorSummaryAr = "المرقاب • برج التحرير • المالية • شارع السور • طريق الملك فيصل • طريق الرياض • الخالدية • الدائري الخامس • حديقة الحيوان • خيطان • الفروانية • صبحان",
            operationNotesAr = "مسار من 19 محطة يربط المرقاب والمالية عبر طريق الرياض والملك فيصل والخالدية وحديقة الحيوان وخيطان وهوليدي إن الفروانية إلى صبحان."
        ),
        "40" to RouteArabicText(
            titleAr = "شرق – الفحيحيل",
            originAr = "شرق",
            destinationAr = "الفحيحيل",
            corridorSummaryAr = "شرق • المالية • طريق المطار • الشويخ • خيطان • جليب الشيوخ • الفروانية • صبحان • الفحيحيل",
            operationNotesAr = "خط بين المحافظات يربط شرق والمالية عبر طريق المطار والشويخ وخيطان وجليب الشيوخ والفروانية وصبحان وصولاً إلى الفحيحيل."
        ),
        "51" to RouteArabicText(
            titleAr = "شرق – دوار جليب الشيوخ",
            originAr = "شرق",
            destinationAr = "دوار جليب الشيوخ",
            corridorSummaryAr = "شرق • المرقاب • فهد السالم • المالية • طريق الجهراء • سيتي سنتر • طريق المطار • لولو • الخالدية 55 • هوليدي إن • الضجيج • الجليب",
            operationNotesAr = "خط نقل رئيسي بـ 15 محطة من شرق والمرقاب عبر سيتي سنتر ولولو وهوليدي إن الفروانية والضجيج وجمعية الجليب إلى دوار الجليب."
        ),
        "66" to RouteArabicText(
            titleAr = "السالمية – الحساوي",
            originAr = "السالمية",
            destinationAr = "الحساوي",
            corridorSummaryAr = "السالمية • مركز سلطان • مجمع الزهرة • حديقة الشعب • البحر • الرحاب • قرطبة • الدائري الرابع • لولو • سوق الجمعة • الفروانية • الضجيج • الحساوي",
            operationNotesAr = "خط عرضي من 17 محطة يربط مجمعات السالمية وحديقة الشعب عبر حولي وقرطبة وسوق الجمعة والفروانية والضجيج إلى الحساوي."
        ),
        "66X" to RouteArabicText(
            titleAr = "السالمية – الحساوي (سريع)",
            originAr = "السالمية",
            destinationAr = "الحساوي السريع",
            corridorSummaryAr = "سريع • السالمية • شارع المغيرة • قرطبة • الدائري الرابع • لولو • سوق الجمعة • الفروانية • الدائري السادس • الضجيج • الحساوي",
            operationNotesAr = "خدمة إكسبريس محدودة التوقف من شارع المغيرة في السالمية عبر قرطبة والدائري الرابع وسوق الجمعة والفروانية والضجيج إلى الحساوي."
        ),
        "77" to RouteArabicText(
            titleAr = "السالمية – خيطان",
            originAr = "السالمية",
            destinationAr = "خيطان",
            corridorSummaryAr = "السالمية • أبولو • البحر • الرحاب • دار الشفاء • النقرة • العديلية • التطبيقي • كندا دراي • تويوتا • لندن شوبينج • الري • خيطان",
            operationNotesAr = "خط تنقل بـ 24 محطة يربط السالمية بمراكز حولي الطبية والعديلية والهيئة العامة للتعليم التطبيقي والشويخ الصناعية والري وخيطان."
        ),
        "77X" to RouteArabicText(
            titleAr = "السالمية – العارضية (سريع)",
            originAr = "السالمية",
            destinationAr = "العارضية",
            corridorSummaryAr = "سريع • السالمية • شارع الخنساء • شارع قطر • المغيرة • أبولو • الدائري الرابع • الشويخ • كندا دراي • الرقعي • العارضية",
            operationNotesAr = "خط سريع من 11 محطة عبر الدائري الرابع يربط السالمية بالشويخ وشارع كندا دراي والرقعي والعارضية."
        ),
        "999" to RouteArabicText(
            titleAr = "المالية – الفحيحيل (البرج الأخضر)",
            originAr = "المالية",
            destinationAr = "الفحيحيل - البرج الأخضر",
            corridorSummaryAr = "المالية • شارع البنوك • برج الحمراء • حديقة الشعب • بوليفارد • السالمية • مستشفى السيف • البدع • سلوى • المسيلة • العقيلة • الفنطاس • كويت ماجيك • المنقف • الفحيحيل",
            operationNotesAr = "الخط الساحلي الرئيسي بـ 30 محطة من المالية وشارع البنوك عبر الحمراء والشعب والسالمية والبدع وسلوى والمسيلة وكويت ماجيك مول والمنقف إلى البرج الأخضر في الفحيحيل."
        ),
        "103" to RouteArabicText(
            titleAr = "المرقاب – المواصلات الجهراء",
            originAr = "المرقاب",
            destinationAr = "المواصلات الجهراء",
            corridorSummaryAr = "المرقاب • المالية • سيتي سنتر • التلال • جسر الغزالي • ريسكو • دوار الأمم • الأندلس • الصليبيخات • الدوحة • معسكر أمغرة • جمعية الجهراء • المواصلات",
            operationNotesAr = "محور غربي بـ 23 محطة من المرقاب عبر طريق الجهراء 80 والشويخ ودوار الأمم والأندلس والصليبيخات والدوحة ومعسكر أمغرة وجمعية الجهراء إلى المواصلات."
        ),
        "106" to RouteArabicText(
            titleAr = "الحساوي – الفحيحيل",
            originAr = "الحساوي",
            destinationAr = "الفحيحيل",
            corridorSummaryAr = "الحساوي • الجليب • الدائري السادس • الفروانية • مترو • خيطان • الزهراء • جنوب السرة • مول 360 • المسيلة • صباح السالم • العقيلة • الفنطاس • أبو حليفة • الفحيحيل",
            operationNotesAr = "خط جنوبي بـ 23 محطة من الحساوي والجليب عبر الفروانية وخيطان والزهراء والبطاقة المدنية في جنوب السرة ومول 360 والمسيلة وصباح السالم إلى الفحيحيل."
        ),
        "106X" to RouteArabicText(
            titleAr = "الحساوي – الفحيحيل (سريع)",
            originAr = "الحساوي",
            destinationAr = "الفحيحيل السريع",
            corridorSummaryAr = "سريع • الحساوي • الجليب • الدائري السادس • الفروانية • خيطان • المسيلة • صباح السالم • الفنيطيس • العقيلة • الفنطاس • المهبولة • أبو حليفة • الفحيحيل",
            operationNotesAr = "خط سريع بـ 19 محطة من الحساوي والفروانية يتجاوز جنوب السرة مباشرة إلى المسيلة وصباح السالم والفنيطيس والعقيلة والفنطاس والفحيحيل."
        ),
        "102X" to RouteArabicText(
            titleAr = "المالية – الفحيحيل (سريع)",
            originAr = "المالية",
            destinationAr = "الفحيحيل السريع",
            corridorSummaryAr = "سريع • المالية • حولي • صباح السالم • الفنطاس • المنقف • الفحيحيل",
            operationNotesAr = "النسخة السريعة من خط 102A على الطرق السريعة بين مدينة الكويت والفحيحيل."
        ),
        "102A" to RouteArabicText(
            titleAr = "المالية – الفحيحيل (عادي)",
            originAr = "المالية",
            destinationAr = "الفحيحيل",
            corridorSummaryAr = "المالية • حي البنوك • السفارات • الدعية • شارع القاهرة • مستشفى هادي • بيان • مشرف • سلوى • المسيلة • صباح السالم • الفنيطيس • العقيلة • الفنطاس • الفحيحيل",
            operationNotesAr = "خط شامل للجنوب بـ 26 محطة من المالية مروراً بحديقة البلدية والدعية ومستشفى هادي وبيان ومشرف وسلوى والمسيلة وصباح السالم وسوق الفحيحيل المركزي."
        ),
        "507" to RouteArabicText(
            titleAr = "المرقاب – دوار جليب الشيوخ",
            originAr = "المرقاب",
            destinationAr = "دوار جليب الشيوخ",
            corridorSummaryAr = "المرقاب • فهد السالم • المالية • سيتي سنتر • طريق المطار • الوطن • كندا دراي • تويوتا • الري • شارع 60 • الدائري السادس • الجليب",
            operationNotesAr = "الخط الكلاسيكي الكامل بـ 20 محطة من المرقاب والمالية مروراً بسيتي سنتر والشويخ الصناعية والري وشارع 60 والدائري السادس إلى دوار الجليب."
        ),
        "507S" to RouteArabicText(
            titleAr = "شرق – جليب الشيوخ (الفرع الجنوبي)",
            originAr = "شرق",
            destinationAr = "جنوب الجليب",
            corridorSummaryAr = "شرق • المرقاب • طريق المطار 55 • هوليدي إن الفروانية • الضجيج • جمعية الجليب",
            operationNotesAr = "الفرع الجنوبي من خط 507 عبر طريق المطار 55 والفروانية والضجيج."
        ),
        "A2" to RouteArabicText(
            titleAr = "خيطان – الأفنيوز – الرقعي",
            originAr = "خيطان / صبحان",
            destinationAr = "الأفنيوز والرقعي",
            corridorSummaryAr = "صبحان • خيطان • الفروانية • الري • الأفنيوز مول • الرقعي",
            operationNotesAr = "خط مخصص لربط صبحان وخيطان والفروانية مباشرة بمجمع الأفنيوز ومنطقة الرقعي."
        ),
        "105" to RouteArabicText(
            titleAr = "الفحيحيل – الحساوي",
            originAr = "الفحيحيل",
            destinationAr = "الحساوي",
            corridorSummaryAr = "الفحيحيل • شارع مكة • المنقف • أبو حليفة • الطريق الساحلي • العقيلة • القرين • ضاحية صباح السالم • جنوب السرة • الدائري السادس • خيطان الجديدة • كندا دراي • الحساوي",
            operationNotesAr = "مسار من 14 محطة يربط الفحيحيل وشارع مكة والطريق الساحلي والعقيلة والقرين وضاحية صباح السالم والبطاقة المدنية في جنوب السرة وخيطان الجديدة وكندا دراي إلى الحساوي."
        )
    )
}
