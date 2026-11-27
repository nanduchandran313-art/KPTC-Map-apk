package com.example.data

import com.example.model.AlertSeverity
import com.example.model.KuwaitRoadCondition
import com.example.model.KuwaitTrafficSummary
import com.example.model.RoadConditionStatus
import com.example.model.ServiceAlert
import com.example.model.ServiceAlertType
import java.util.Calendar
import java.util.TimeZone

object ServiceAlertEngine {

    private val kuwaitTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Kuwait")

    fun activeAlerts(nowMillis: Long = System.currentTimeMillis()): List<ServiceAlert> {
        val cal = Calendar.getInstance(kuwaitTimeZone).apply { timeInMillis = nowMillis }
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timeStamp = String.format("%02d:%02d AST", hour24, minute)

        val isPeakTraffic = hour24 in 6..9 || hour24 in 13..19
        val airportDelay = if (isPeakTraffic) 12 else 7
        val raiDelay = if (isPeakTraffic) 9 else 5

        return listOf(
            ServiceAlert(
                id = "alert_ghazali_closure",
                type = ServiceAlertType.ROAD_CLOSURE,
                severity = AlertSeverity.HIGH,
                affectedRouteNumbers = listOf("103", "11", "12"),
                titleEn = "Jahra Rd / Ghazali Bridge Diversion",
                titleAr = "تحويلة مرورية: طريق الجهراء وجسر الغزالي",
                messageEn = "Temporary lane closure near Ghazali Bridge & Sabah Medical Zone. Buses detour via Jamal Abdul Nasser St; UN Circle stops remain open.",
                messageAr = "إغلاق مؤقت لمسار قرب جسر الغزالي ومنطقة الصباح الطبية. تتحول الحافلات عبر شارع جمال عبدالناصر مع استمرار التوقف في دوار الأمم.",
                corridorEn = "Jahra Rd 80 • Shuwaikh • UN Circle",
                corridorAr = "طريق الجهراء 80 • الشويخ • دوار الأمم",
                delayMinutes = 10,
                updatedTimeLabel = timeStamp
            ),
            ServiceAlert(
                id = "alert_airport_delay",
                type = ServiceAlertType.DELAY,
                severity = AlertSeverity.MEDIUM,
                affectedRouteNumbers = listOf("13", "507", "507S", "21"),
                titleEn = "Airport Rd 55 Congestion (+${airportDelay} min)",
                titleAr = "ازدحام طريق المطار 55 (+${airportDelay} د)",
                messageEn = "Heavy traffic approaching Farwaniya & Crowne Plaza junction. Expect ${airportDelay}-minute delays on Airport T1/T4 and Jleeb corridors.",
                messageAr = "كثافة مرورية قرب تقاطع الفروانية وكراون بلازا. يرجى توقع تأخير بنحو ${airportDelay} دقيقة على خطوط المطار والجليب.",
                corridorEn = "Airport Rd 55 • Farwaniya • Dajeej",
                corridorAr = "طريق المطار 55 • الفروانية • الضجيج",
                delayMinutes = airportDelay,
                updatedTimeLabel = timeStamp
            ),
            ServiceAlert(
                id = "alert_salmiya_maintenance",
                type = ServiceAlertType.MAINTENANCE,
                severity = AlertSeverity.INFO,
                affectedRouteNumbers = listOf("999", "102X", "102A", "15"),
                titleEn = "Salmiya & Gulf Rd Shelter Maintenance",
                titleAr = "صيانة مواقف السالمية وشارع الخليج",
                messageEn = "Scheduled bus bay resurfacing near Shaab Seafront & Salmiya Marina stops. Board 60m ahead at temporary KPTC signage.",
                messageAr = "أعمال صيانة مجدولة لأرصفة الانتظار قرب الشعب البحري ومارينا السالمية. يتم الصعود قبل الموقف بـ 60 متراً عند اللوحات المؤقتة.",
                corridorEn = "Gulf Rd • Shaab • Salmiya Marina",
                corridorAr = "شارع الخليج • الشعب • مارينا السالمية",
                delayMinutes = null,
                updatedTimeLabel = timeStamp
            ),
            ServiceAlert(
                id = "alert_alrai_delay",
                type = ServiceAlertType.DELAY,
                severity = AlertSeverity.MEDIUM,
                affectedRouteNumbers = listOf("66", "66X", "A2", "77", "77X"),
                titleEn = "5th Ring / Avenues Traffic Delay (+${raiDelay} min)",
                titleAr = "تأخير الدائري الخامس والأفنيوز (+${raiDelay} د)",
                messageEn = "Slow traffic approaching The Avenues Mall Phase 1–4 entrance. Express 66X & 77X prioritize 5th Ring express bays.",
                messageAr = "تباطؤ مروري عند مدخل مجمع الأفنيوز (المراحل 1–4). تحافظ الخطوط السريعة 66X و77X على مسار الدائري الخامس.",
                corridorEn = "Al Rai • The Avenues • 5th Ring",
                corridorAr = "الري • الأفنيوز • الدائري الخامس",
                delayMinutes = raiDelay,
                updatedTimeLabel = timeStamp
            ),
            ServiceAlert(
                id = "alert_south_surra_maintenance",
                type = ServiceAlertType.MAINTENANCE,
                severity = AlertSeverity.INFO,
                affectedRouteNumbers = listOf("106", "106X", "105", "23"),
                titleEn = "South Surra / 6th Ring Signal Upgrade",
                titleAr = "تحديث إشارات جنوب السرة والدائري السادس",
                messageEn = "Traffic signal maintenance near South Surra PACI & 360 Mall interchange. Allow an extra 5 minutes during midday hours.",
                messageAr = "صيانة إشارات المرور قرب البطاقة المدنية في جنوب السرة وتقاطع مول 360. يُنصح بإضافة 5 دقائق لوقت الرحلة.",
                corridorEn = "South Surra PACI • 360 Mall • 6th Ring",
                corridorAr = "جنوب السرة • مول 360 • الدائري السادس",
                delayMinutes = 5,
                updatedTimeLabel = timeStamp
            )
        )
    }

    fun kuwaitRoadConditions(nowMillis: Long = System.currentTimeMillis()): List<KuwaitRoadCondition> {
        val cal = Calendar.getInstance(kuwaitTimeZone).apply { timeInMillis = nowMillis }
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val isMorningPeak = hour24 in 6..9
        val isAfternoonPeak = hour24 in 13..19
        val isPeak = isMorningPeak || isAfternoonPeak

        return listOf(
            KuwaitRoadCondition(
                id = "road_ghazali_closed",
                roadNameEn = "Ghazali Expressway (Route 60)",
                roadNameAr = "طريق الغزالي السريع (60)",
                sectionEn = "Shuwaikh Port Ramp → Ghazali Bridge",
                sectionAr = "منحدر ميناء الشويخ ← جسر الغزالي",
                detourOrNoteEn = "Northbound ramp closed for bridge expansion; buses detour via Jamal Abdul Nasser St.",
                detourOrNoteAr = "المنحدر الشمالي مغلق لأعمال توسعة الجسر؛ تتحول الحافلات عبر شارع جمال عبدالناصر.",
                status = RoadConditionStatus.CLOSED,
                delayMinutes = if (isPeak) 15 else 10,
                trafficTimeLabelEn = "Closed 06:00 AM – 10:00 PM AST",
                trafficTimeLabelAr = "مغلق 06:00 ص – 10:00 م بتوقيت الكويت",
                affectedRouteNumbers = listOf("11", "12", "13", "103")
            ),
            KuwaitRoadCondition(
                id = "road_mubarak_closed",
                roadNameEn = "Mubarak Al Kabeer St. (Kuwait City)",
                roadNameAr = "شارع مبارك الكبير (مدينة الكويت)",
                sectionEn = "Darwazah Tunnel → Safat Square East",
                sectionAr = "نفق دروازة ← شرق ساحة الصفاة",
                detourOrNoteEn = "Road closed for municipal utility works; board at Mirqab or Ahmed Al Jaber St.",
                detourOrNoteAr = "الطريق مغلق لأعمال البنية التحتية؛ الصعود من محطة المرقاب أو شارع أحمد الجابر.",
                status = RoadConditionStatus.CLOSED,
                delayMinutes = if (isPeak) 12 else 7,
                trafficTimeLabelEn = "Closed 24h • Reopens Friday",
                trafficTimeLabelAr = "مغلق 24 ساعة • يُفتح الجمعة",
                affectedRouteNumbers = listOf("15", "16", "18", "999")
            ),
            KuwaitRoadCondition(
                id = "road_sixth_ring_blocked",
                roadNameEn = "6th Ring Road – Jleeb / Hassawi",
                roadNameAr = "الدائري السادس – جليب الشيوخ / الحساوي",
                sectionEn = "Jleeb Mujamma Exit → 60 Rd Ramp",
                sectionAr = "مخرج مجمع الجليب ← تقاطع طريق 60",
                detourOrNoteEn = "2 right lanes blocked due to heavy truck bottleneck; slow single-lane crawl.",
                detourOrNoteAr = "انسداد مسارين بسبب تكدس الشاحنات؛ حركة بطيئة في مسار واحد.",
                status = RoadConditionStatus.BLOCKED,
                delayMinutes = if (isPeak) 18 else 11,
                trafficTimeLabelEn = "Peak Blockage: 13:00 – 20:00 AST",
                trafficTimeLabelAr = "ذروة الانسداد: 01:00 – 08:00 م",
                affectedRouteNumbers = listOf("16", "40", "66", "66X", "106", "106X", "507")
            ),
            KuwaitRoadCondition(
                id = "road_airport55_blocked",
                roadNameEn = "Airport Rd (55) – Farwaniya / Khaitan",
                roadNameAr = "طريق المطار (55) – الفروانية / خيطان",
                sectionEn = "Crowne Plaza Roundabout → Abu Dhabi St.",
                sectionAr = "دوار كراون بلازا ← شارع أبوظبي",
                detourOrNoteEn = "Service lane blocked near Farwaniya Metro; express buses use center bypass.",
                detourOrNoteAr = "مسار الخدمة مسدود قرب مترو الفروانية؛ الحافلات تستخدم المسار الأوسط.",
                status = RoadConditionStatus.BLOCKED,
                delayMinutes = if (isPeak) 14 else 8,
                trafficTimeLabelEn = "Heavy Block: 07:00–09:30 & 16:00–21:00",
                trafficTimeLabelAr = "انسداد مزدحم: 07:00–09:30 ص و04:00–09:00 م",
                affectedRouteNumbers = listOf("13", "21", "23", "40", "51", "507S")
            ),
            KuwaitRoadCondition(
                id = "road_fifth_ring_traffic",
                roadNameEn = "5th Ring Rd – Al Rai / The Avenues",
                roadNameAr = "الدائري الخامس – الري / الأفنيوز",
                sectionEn = "Lulu Al Rai → Centerpoint / 5th Ring",
                sectionAr = "لولو الري ← سنتربوينت / الدائري الخامس",
                detourOrNoteEn = if (isPeak) "Bumper-to-bumper mall & evening rush traffic." else "Moderate flow; short queues at Gate 4.",
                detourOrNoteAr = if (isPeak) "ازدحام مروري كثيف باتجاه المجمعات." else "حركة متوسطة مع تباطؤ خفيف عند البوابة 4.",
                status = RoadConditionStatus.HEAVY_TRAFFIC,
                delayMinutes = if (isPeak) 14 else 6,
                trafficTimeLabelEn = "Rush Time: 16:30 – 22:00 AST",
                trafficTimeLabelAr = "وقت الذروة: 04:30 – 10:00 م",
                affectedRouteNumbers = listOf("13", "66", "66X", "77", "A2", "507")
            ),
            KuwaitRoadCondition(
                id = "road_fourth_ring_traffic",
                roadNameEn = "4th Ring Rd – Hawally / Jabriya",
                roadNameAr = "الدائري الرابع – حولي / الجابرية",
                sectionEn = "Al Bahar Center → Royal Hayat Hospital",
                sectionAr = "مجمع البحر ← مستشفى رويال حياة",
                detourOrNoteEn = if (isPeak) "School & hospital rush congestion across Hawally junctions." else "Steady movement with signal waits.",
                detourOrNoteAr = if (isPeak) "ازدحام المدارس والمستشفيات عند تقاطعات حولي." else "انسيابية متوسطة مع توقف عند الإشارات.",
                status = RoadConditionStatus.HEAVY_TRAFFIC,
                delayMinutes = if (isPeak) 11 else 5,
                trafficTimeLabelEn = "Rush Time: 06:45–09:00 & 13:00–16:00",
                trafficTimeLabelAr = "وقت الذروة: 06:45–09:00 ص و01:00–04:00 م",
                affectedRouteNumbers = listOf("15", "16", "18", "66", "66X", "77", "77X")
            ),
            KuwaitRoadCondition(
                id = "road_fahaheel30_open",
                roadNameEn = "Fahaheel Expressway (Route 30)",
                roadNameAr = "طريق الفحيحيل السريع (30)",
                sectionEn = "Salwa → Sabah Al Salem → Egaila → Fahaheel",
                sectionAr = "سلوى ← صباح السالم ← العقيلة ← الفحيحيل",
                detourOrNoteEn = "All lanes open; smooth express bus flow along coastal corridor.",
                detourOrNoteAr = "جميع المسارات مفتوحة؛ حركة سلسة للحافلات على الخط الساحلي.",
                status = RoadConditionStatus.OPEN_SMOOTH,
                delayMinutes = if (isPeak) 4 else 0,
                trafficTimeLabelEn = "Clear Corridor • Normal Speed",
                trafficTimeLabelAr = "طريق سالك • سرعة طبيعية",
                affectedRouteNumbers = listOf("102A", "102X", "105", "106", "106X", "999")
            ),
            KuwaitRoadCondition(
                id = "road_jahra80_west_open",
                roadNameEn = "Jahra Rd (80) – Sulaibikhat to Jahra",
                roadNameAr = "طريق الجهراء (80) – الصليبيخات إلى الجهراء",
                sectionEn = "UN Circle → Doha → Jahra Roundabout",
                sectionAr = "دوار الأمم ← الدوحة ← دوار الجهراء",
                detourOrNoteEn = "Open & clear west of UN Circle; buses running on regular 15m headway.",
                detourOrNoteAr = "مفتوح وسالك غرب دوار الأمم؛ الحافلات تعمل بانتظام كل 15 دقيقة.",
                status = RoadConditionStatus.OPEN_SMOOTH,
                delayMinutes = 0,
                trafficTimeLabelEn = "Open & On Schedule",
                trafficTimeLabelAr = "مفتوح وفي الموعد",
                affectedRouteNumbers = listOf("11", "103")
            )
        )
    }

    fun kuwaitTrafficSummary(nowMillis: Long = System.currentTimeMillis()): KuwaitTrafficSummary {
        val cal = Calendar.getInstance(kuwaitTimeZone).apply { timeInMillis = nowMillis }
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val amPm = if (hour24 < 12) "AM" else "PM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val formattedTime = String.format("%02d:%02d %s AST", hour12, minute, amPm)

        val (periodEn, periodAr) = when (hour24) {
            in 6..9 -> "Morning Peak Rush" to "ذروة الصباح المرورية"
            in 10..12 -> "Midday Moderate Flow" to "حركة متوسطة منتصف النهار"
            in 13..16 -> "Afternoon School & Work Rush" to "ذروة الخروج والمدارس"
            in 17..20 -> "Evening Mall & Coastal Peak" to "ذروة المساء والمجمعات"
            in 21..23 -> "Late Evening Clearing" to "هدوء مروري مسائي"
            else -> "Night Clear Roads" to "طرق سالكة ليلاً"
        }

        val (nextWindowEn, nextWindowAr) = when {
            hour24 < 6 -> "Next Peak: 06:30 – 09:00 AM AST" to "الذروة القادمة: 06:30 – 09:00 صباحاً"
            hour24 in 6..9 -> "Active Now until 09:30 AM AST" to "نشطة الآن حتى 09:30 صباحاً"
            hour24 in 10..12 -> "Next Peak: 01:00 – 04:00 PM AST" to "الذروة القادمة: 01:00 – 04:00 عصراً"
            hour24 in 13..20 -> "Eases after 08:30 PM AST" to "تخف الحركة بعد 08:30 مساءً"
            else -> "Clear until 06:30 AM AST" to "سالكة حتى 06:30 صباحاً"
        }

        val roads = kuwaitRoadConditions(nowMillis)
        val closedCount = roads.count { it.status == RoadConditionStatus.CLOSED }
        val blockedCount = roads.count { it.status == RoadConditionStatus.BLOCKED }
        val heavyCount = roads.count { it.status == RoadConditionStatus.HEAVY_TRAFFIC }
        val avgExtra = roads.filter { it.delayMinutes > 0 }
            .map { it.delayMinutes }
            .average()
            .let { if (it.isNaN()) 0 else kotlin.math.round(it).toInt() }

        return KuwaitTrafficSummary(
            kuwaitTimeFormatted = formattedTime,
            trafficPeriodEn = periodEn,
            trafficPeriodAr = periodAr,
            nextRushWindowEn = nextWindowEn,
            nextRushWindowAr = nextWindowAr,
            closedCount = closedCount,
            blockedCount = blockedCount,
            heavyTrafficCount = heavyCount,
            avgExtraMinutes = avgExtra
        )
    }

    fun alertsForRoute(routeNumber: String, alerts: List<ServiceAlert>): List<ServiceAlert> {
        return alerts.filter { routeNumber in it.affectedRouteNumbers }
    }
}
