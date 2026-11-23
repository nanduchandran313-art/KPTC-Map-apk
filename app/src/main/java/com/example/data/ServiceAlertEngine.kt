package com.example.data

import com.example.model.AlertSeverity
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

    fun alertsForRoute(routeNumber: String, alerts: List<ServiceAlert>): List<ServiceAlert> {
        return alerts.filter { routeNumber in it.affectedRouteNumbers }
    }
}
