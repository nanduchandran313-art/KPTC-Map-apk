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
    fun navLiveMap(lang: AppLanguage) = if (isArabic(lang)) "الخريطة الحية" else "Live Map"
    fun navStations(lang: AppLanguage) = if (isArabic(lang)) "المحطات" else "Stations"

    // Filter Tabs
    fun filterTabLabel(tab: RouteFilterTab, lang: AppLanguage): String = if (isArabic(lang)) {
        when (tab) {
            RouteFilterTab.ALL -> "الكل (23)"
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
        if (isArabic(lang)) "كل الـ 23 خطاً" else "All 23 Routes"

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
            titleAr = "شرق – أمغرة",
            originAr = "شرق",
            destinationAr = "أمغرة",
            corridorSummaryAr = "شرق • المالية • ميناء الشويخ • الصباح • دوار الأمم • الصليبيخات • أمغرة",
            operationNotesAr = "محور ساحلي شمالي يربط مدينة الكويت ومنطقة الشويخ الطبية والميناء بكراج أمغرة."
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
            corridorSummaryAr = "المرقاب • المالية • الخالدية • الري • العمرية • خيطان • الفروانية • المطار",
            operationNotesAr = "الخط الرئيسي للمطار عبر طريق المطار 55 مروراً بخيطان والفروانية ومباني الركاب 1 و4."
        ),
        "15" to RouteArabicText(
            titleAr = "المالية – السالمية",
            originAr = "المالية",
            destinationAr = "السالمية",
            corridorSummaryAr = "المالية • المرقاب • دروازة • الشعب • حولي • مستشفى مبارك • جمعية السالمية",
            operationNotesAr = "خط حضري عالي التردد يربط مركز الأعمال في مدينة الكويت بحولي والسالمية."
        ),
        "16" to RouteArabicText(
            titleAr = "المالية – جليب الشيوخ",
            originAr = "المالية",
            destinationAr = "جليب الشيوخ",
            corridorSummaryAr = "المالية • طريق الجهراء • الري • خيطان • الفروانية • العباسية • الجليب",
            operationNotesAr = "خط مباشر من محطة المالية في مدينة الكويت عبر خيطان والفروانية إلى جليب الشيوخ."
        ),
        "18" to RouteArabicText(
            titleAr = "المرقاب – دوار الأمم المتحدة",
            originAr = "المرقاب",
            destinationAr = "دوار الأمم",
            corridorSummaryAr = "المرقاب • فهد السالم • شارع كندا دراي • تويوتا • مستشفى الصباح • دوار الأمم",
            operationNotesAr = "يربط المرقاب بالمنطقة الصناعية في الشويخ والمنطقة الطبية في الصباح."
        ),
        "21" to RouteArabicText(
            titleAr = "المرقاب – جليب الشيوخ (السادس)",
            originAr = "المرقاب",
            destinationAr = "جليب الشيوخ",
            corridorSummaryAr = "المرقاب • فهد السالم • طريق المطار 55 • الضجيج • الدائري السادس • الجليب • العباسية",
            operationNotesAr = "وصلة سريعة عبر طريق المطار والدائري السادس من المرقاب إلى العباسية وجليب الشيوخ."
        ),
        "23" to RouteArabicText(
            titleAr = "المرقاب – خيطان وصبحان",
            originAr = "المرقاب",
            destinationAr = "صبحان الصناعية",
            corridorSummaryAr = "المرقاب • المالية • طريق المطار 50 • خيطان • جنوب السرة • صبحان",
            operationNotesAr = "يخدم مجمع الوزارات في جنوب السرة (البطاقة المدنية) ومنطقة صبحان الصناعية."
        ),
        "51" to RouteArabicText(
            titleAr = "شرق – جليب الشيوخ",
            originAr = "شرق",
            destinationAr = "جليب الشيوخ",
            corridorSummaryAr = "شرق • المرقاب • المالية • سيتي سنتر • الفروانية • الضجيج • جمعية الجليب",
            operationNotesAr = "خط نقل رئيسي يربط شرق والمرقاب بالضجيج ومناطق جليب الشيوخ السكنية."
        ),
        "66" to RouteArabicText(
            titleAr = "السالمية – الحساوي (الجليب)",
            originAr = "السالمية",
            destinationAr = "الحساوي / الجليب",
            corridorSummaryAr = "السالمية • حولي • مستشفى مبارك • الدائري الرابع • الري • الفروانية • الحساوي",
            operationNotesAr = "محور عرضي من الشرق للغرب يربط ساحل السالمية بالري والأفنيوز والحساوي."
        ),
        "66X" to RouteArabicText(
            titleAr = "السالمية – الحساوي (سريع)",
            originAr = "السالمية",
            destinationAr = "الحساوي السريع",
            corridorSummaryAr = "سريع • بوليفارد السالمية • الدائري الخامس • الأفنيوز • الفروانية • الحساوي",
            operationNotesAr = "خدمة إكسبريس محدودة التوقف تتجاوز شوارع حولي الداخلية عبر الدائري الخامس."
        ),
        "77" to RouteArabicText(
            titleAr = "السالمية – خيطان",
            originAr = "السالمية",
            destinationAr = "خيطان",
            corridorSummaryAr = "السالمية • حولي • مستشفى مبارك • الدائري الرابع • الري • أوتاد مول • خيطان",
            operationNotesAr = "خط تنقل يومي يربط السالمية وحولي بالري وخيطان."
        ),
        "77X" to RouteArabicText(
            titleAr = "السالمية – العارضية / خيطان (سريع)",
            originAr = "السالمية",
            destinationAr = "خيطان / الرقعي",
            corridorSummaryAr = "سريع • السالمية • الدائري الخامس • سنتربوينت الري • خيطان • الرقعي",
            operationNotesAr = "خط سريع عبر الدائري الخامس يربط السالمية بالري وخيطان والعارضية/الرقعي."
        ),
        "999" to RouteArabicText(
            titleAr = "المالية – الفحيحيل (الخط الساحلي)",
            originAr = "المالية",
            destinationAr = "الفحيحيل",
            corridorSummaryAr = "المالية • الشعب • السالمية • الرميثية • سلوى • المسيلة • الفنطاس • المنقف • الفحيحيل",
            operationNotesAr = "الخط الساحلي الرئيسي من الشمال إلى الجنوب يربط المالية الشيراتون بالسالمية حتى الفحيحيل."
        ),
        "103" to RouteArabicText(
            titleAr = "المرقاب – الجهراء",
            originAr = "المرقاب",
            destinationAr = "جمعية الجهراء",
            corridorSummaryAr = "المرقاب • طريق الجهراء 80 • جسر الغزالي • دوار الأمم • الصليبيخات • أمغرة • الجهراء",
            operationNotesAr = "المحور الغربي الرئيسي بين المحافظات عبر طريق الجهراء السريع (80) إلى الجهراء."
        ),
        "106" to RouteArabicText(
            titleAr = "الفحيحيل – الجليب / الحساوي",
            originAr = "الفحيحيل",
            destinationAr = "الجليب / الحساوي",
            corridorSummaryAr = "الفحيحيل • المنقف • الفنطاس • المسيلة • مول 360 • جنوب السرة • الفروانية • الجليب",
            operationNotesAr = "خط جنوبي رئيسي يربط المناطق الساحلية بمول 360 وجنوب السرة والفروانية عبر الدائري السادس."
        ),
        "106X" to RouteArabicText(
            titleAr = "الفحيحيل – الجليب (سريع)",
            originAr = "الفحيحيل",
            destinationAr = "الجليب السريع",
            corridorSummaryAr = "سريع • الفحيحيل • المنقف • العقيلة • مول 360 • الضجيج • محطة الجليب",
            operationNotesAr = "خط سريع للمسافات الطويلة عبر طريق الملك فهد 40 والدائري السادس."
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
            corridorSummaryAr = "المالية • الدسمة • الدعية • حولي • صباح السالم • المسيلة • المسايل • الفنطاس • المنقف • الفحيحيل",
            operationNotesAr = "خط شامل للجنوب يمر بالدسمة وحولي والمسايل والفنيطيس والعقيلة والمهبولة والفحيحيل."
        ),
        "507" to RouteArabicText(
            titleAr = "المرقاب – جليب الشيوخ",
            originAr = "المرقاب",
            destinationAr = "جليب الشيوخ",
            corridorSummaryAr = "المرقاب • فهد السالم • المالية • سيتي سنتر • كندا دراي • الري • شارع 60 • الجليب",
            operationNotesAr = "الخط الكلاسيكي الكامل بـ 19 محطة مروراً بالشويخ الصناعية والري وصولاً إلى جليب الشيوخ."
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
            titleAr = "الفحيحيل – الحساوي / الجليب",
            originAr = "الفحيحيل",
            destinationAr = "الحساوي / الجليب",
            corridorSummaryAr = "الفحيحيل • المنقف • أبو حليفة • الفنطاس • القرين • جنوب السرة • خيطان الجديدة • الحساوي",
            operationNotesAr = "يربط الجنوب الساحلي بالقرين وجنوب السرة (البطاقة المدنية) وخيطان الجديدة حتى الحساوي."
        )
    )
}
