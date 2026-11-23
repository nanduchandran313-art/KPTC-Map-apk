package com.example.data

import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.HighwayPath
import com.example.model.MapPoint
import com.example.model.RouteCategory
import com.example.ui.theme.RouteAirportMallColor
import com.example.ui.theme.RouteCityColor
import com.example.ui.theme.RouteCoastalColor
import com.example.ui.theme.RouteExpressColor
import com.example.ui.theme.RouteInterGovColor

object KuwaitBusData {

    // Kuwait Geographic Bounding Box for Map Projection
    const val MIN_LAT = 29.045
    const val MAX_LAT = 29.415
    const val MIN_LNG = 47.625
    const val MAX_LNG = 48.185

    // Kuwait City Center (Mirqab / Liberation Tower) fallback reference
    const val KUWAIT_CITY_LAT = 29.3697
    const val KUWAIT_CITY_LNG = 47.9783

    // --- AUTHENTIC KUWAIT BUS STATIONS & STOPS ---
    val stopsMap: Map<String, BusStop> = listOf(
        // Kuwait City Core
        BusStop("shuhada", "Shuhada Street", "شارع الشهداء", "Kuwait City", 29.3768, 47.9835, "Central City Terminal"),
        BusStop("sharq", "Sharq Terminal", "محطة شرق", "Sharq", 29.3832, 47.9942, "Sharq Police Station & Souq Sharq"),
        BusStop("ahmed_jaber", "Ahmed Al Jaber St", "شارع أحمد الجابر", "Sharq", 29.3792, 47.9885, "Commercial Towers"),
        BusStop("dasman", "Dasman Roundabout", "دوار دسمان", "Dasman", 29.3865, 48.0012, "Near Kuwait Towers"),
        BusStop("darwaza", "Darwaza Abdulrazzaq", "دروازة عبدالرزاق", "Kuwait City", 29.3738, 47.9810, "City Interchange"),
        BusStop("mirqab", "Mirqab Central", "المرقاب المركزي", "Mirqab", 29.3672, 47.9788, "KPTC Main Hub & Liberation Tower"),
        BusStop("safat", "Safat Square", "ساحة الصفاة", "Kuwait City", 29.3712, 47.9746, "Fahad Al Salem St"),
        BusStop("muthanna", "Al-Muthanna Complex", "مجمع المثنى", "Kuwait City", 29.3688, 47.9688, "Fahad Al Salem Commercial"),
        BusStop("maliya", "Maliya (Sheraton)", "المالية - الشيراتون", "Kuwait City", 29.3665, 47.9628, "Kuwait City West Hub"),
        BusStop("science_museum", "Science Museum", "المتحف العلمي", "Mirqab", 29.3652, 47.9752, "Abdullah Al Mubarak St"),

        // Shuwaikh, Jahra Rd & Northern West Corridor
        BusStop("jahra_rd_2nd", "Jahra Rd / 2nd Ring", "طريق الجهراء - الدائري الثاني", "Shuwaikh", 29.3578, 47.9495, "Jahra Rd Interchange"),
        BusStop("jamal_abdul_nasser", "Jamal Abdul Nasser St", "شارع جمال عبدالناصر", "Shuwaikh", 29.3555, 47.9340, "Coastal Highway Link"),
        BusStop("flour_mills", "Kuwait Flour Mills", "شركة مطاحن الدقيق", "Shuwaikh Port", 29.3525, 47.9218, "Shuwaikh Port Gate"),
        BusStop("shuwaikh_port", "Shuwaikh Port", "ميناء الشويخ", "Shuwaikh", 29.3542, 47.9125, "Port & Industrial"),
        BusStop("city_center_shuwaikh", "City Center Shuwaikh", "سيتي سنتر الشويخ", "Shuwaikh", 29.3490, 47.9445, "Shopping Complex"),
        BusStop("al_watan", "Al Watan / Press Zone", "الوطن", "Shuwaikh Ind.", 29.3422, 47.9380, "Shuwaikh Industrial"),
        BusStop("canada_dry", "Canada Dry Street", "شارع كندا دراي", "Shuwaikh Ind.", 29.3368, 47.9310, "Auto & Industrial Zone"),
        BusStop("toyota_parts", "Toyota Spare Parts", "قطع غيار تويوتا", "Shuwaikh Ind.", 29.3315, 47.9265, "Shuwaikh Service Hub"),
        BusStop("al_naki", "Al Naki Mosque", "مسجد النقي", "Shuwaikh Ind.", 29.3270, 47.9240, "Industrial Block"),
        BusStop("kuwait_univ_shuwaikh", "Kuwait University (Shuwaikh)", "جامعة الكويت - الشويخ", "Shuwaikh", 29.3515, 47.9012, "University Campus"),
        BusStop("sabah_hospital", "Al-Sabah Medical Area", "منطقة الصباح الطبية", "Shuwaikh", 29.3465, 47.8825, "Sabah & Infectious Hospitals"),
        BusStop("al_razi", "Al-Razi Orthopedic Hosp.", "مستشفى الرازي", "Shuwaikh", 29.3418, 47.8750, "Medical District"),
        BusStop("un_circle", "UN Circle (MOSAL)", "دوار الأمم المتحدة", "Sulaibikhat", 29.3358, 47.8615, "Ministry of Social Affairs"),
        BusStop("ghazali_bridge", "Ghazali Bridge / Risco", "جسر الغزالي", "Shuwaikh", 29.3390, 47.9165, "Ghazali Expressway"),
        BusStop("sulaibikhat", "Sulaibikhat Co-Op", "جمعية الصليبيخات", "Sulaibikhat", 29.3245, 47.8280, "Sulaibikhat Residential"),
        BusStop("nahdha", "Al Nahdha / Doha Rd", "النهضة", "Nahdha", 29.3210, 47.8040, "Jahra Rd Corridor"),
        BusStop("amghara", "Amghara KPTC Garage", "كراج النقل العام - أمغرة", "Amghara", 29.3185, 47.7685, "Amghara Industrial & Depot"),
        BusStop("sulaibiya_ind", "Sulaibiya Industrial", "الصليبية الصناعية", "Sulaibiya", 29.2795, 47.7820, "Warehousing & Industrial"),
        BusStop("jahra_sports", "Al-Jahra Sports Club", "نادي الجهراء الرياضي", "Jahra", 29.3340, 47.7025, "East Jahra"),
        BusStop("jahra_roundabout", "Jahra Roundabout", "دوار الجهراء", "Jahra", 29.3388, 47.6810, "Jahra Center"),
        BusStop("jahra_coop", "Jahra Co-Op & MOC", "جمعية الجهراء", "Jahra", 29.3445, 47.6655, "Jahra Main Terminal"),

        // Al Rai, Avenues, Khaldiya & Riggae
        BusStop("khaldiya", "Khaldiya Campus", "الخالدية", "Khaldiya", 29.3268, 47.9675, "College of Engineering"),
        BusStop("sultan_al_rai", "Sultan Center Al Rai", "مركز سلطان الري", "Al Rai", 29.3170, 47.9345, "4th Ring Commercial"),
        BusStop("safat_alghanim", "Safat Alghanim", "صفاة الغانم", "Al Rai", 29.3135, 47.9325, "Furniture & Auto Mall"),
        BusStop("al_rai", "Al Rai Commercial", "الري", "Al Rai", 29.3105, 47.9305, "Al Rai Spine"),
        BusStop("lulu_al_rai", "Lulu Hypermarket Al Rai", "لولو هايبرماركت الري", "Al Rai", 29.3075, 47.9385, "Near Friday Market"),
        BusStop("avenues_mall", "The Avenues Mall", "الأفنيوز مول", "Al Rai", 29.3038, 47.9338, "Phase 1–4 Main Entrance"),
        BusStop("centerpoint_rai", "Centerpoint / 5th Ring", "سنتربوينت", "Al Rai", 29.3012, 47.9280, "5th Ring Junction"),
        BusStop("riggae", "Al-Riggae Terminal", "الرقعي", "Riggae", 29.3065, 47.9020, "Riggae Court & Blocks"),

        // Omariya, Khaitan, Farwaniya, Dajeej, Airport & Jleeb
        BusStop("kuwait_zoo", "Kuwait Zoo / Omariya", "حديقة الحيوان - العمرية", "Omariya", 29.2935, 47.9538, "Airport Rd 55"),
        BusStop("muscat_st", "Muscat Street", "شارع مسقط", "Khaitan", 29.2905, 47.9652, "North Khaitan"),
        BusStop("khaitan_police", "Khaitan Police Station", "مخفر خيطان", "Khaitan", 29.2862, 47.9695, "Khaitan Center"),
        BusStop("awtad_mall", "Awtad Mall Khaitan", "أوتاد مول خيطان", "Khaitan", 29.2825, 47.9718, "Commercial Hub"),
        BusStop("khaitan_park", "Khaitan Park / Clinic", "حديقة خيطان", "Khaitan", 29.2775, 47.9685, "Abu Dhabi St"),
        BusStop("new_khaitan", "New Khaitan Block", "خيطان الجديدة", "Khaitan", 29.2735, 47.9625, "6th Ring Connector"),
        BusStop("holiday_inn_farwaniya", "Holiday Inn Farwaniya", "هوليدي إن الفروانية", "Farwaniya", 29.2842, 47.9560, "Ghazali / Habib Munawer"),
        BusStop("crowne_plaza", "Crowne Plaza / Airport Rd", "كراون بلازا", "Farwaniya", 29.2785, 47.9585, "Farwaniya South"),
        BusStop("farwaniya_coop", "Farwaniya Co-Op", "جمعية الفروانية", "Farwaniya", 29.2792, 47.9475, "Habib Munawer St"),
        BusStop("dajeej_lulu", "Dajeej Lulu Hypermarket", "لولو الضجيج", "Dajeej", 29.2630, 47.9465, "6th Ring Commercial"),
        BusStop("road_60", "60 Road / Ghazali", "شارع 60", "Farwaniya", 29.2720, 47.9320, "Ghazali South"),
        BusStop("sixth_ring", "6th Ring Road Interchange", "الدائري السادس", "Jleeb East", 29.2650, 47.9255, "Highway Ramp"),
        BusStop("jleeb_coop", "Jleeb Al-Shuyoukh Co-Op", "جمعية جليب الشيوخ", "Jleeb", 29.2690, 47.9195, "Jleeb Central"),
        BusStop("abbassiya", "Abbassiya Center", "العباسية", "Jleeb", 29.2645, 47.9140, "Central Abbassiya"),
        BusStop("german_clinic", "German Clinic Jleeb", "العيادة الألمانية", "Jleeb", 29.2610, 47.9115, "Jleeb Residential"),
        BusStop("jleeb_terminal", "Jleeb / Hassawi Terminal", "محطة جليب الشيوخ / الحساوي", "Jleeb", 29.2575, 47.9085, "West Jleeb Hub"),
        BusStop("airport_t1", "Kuwait Intl Airport (T1/T4)", "مطار الكويت الدولي", "Airport", 29.2268, 47.9690, "Departures & Arrivals"),
        BusStop("subhan_ind", "Subhan Industrial Area", "منطقة صبحان الصناعية", "Subhan", 29.2185, 48.0160, "Industrial & Ministries"),

        // Hawally, Salmiya, South Surra, 360 Mall
        BusStop("dasma", "Dasma Co-Op", "الدسمة", "Dasma", 29.3545, 47.9975, "2nd Ring Rd"),
        BusStop("daiya", "Daiya / Shaab", "الدعية / الشعب", "Daiya", 29.3510, 48.0145, "Gulf Road / Shaab"),
        BusStop("shaab_park", "Shaab Seafront", "الشعب البحري", "Shaab", 29.3490, 48.0340, "Gulf Road Coastal"),
        BusStop("hawally_north", "Hawally North Nugra", "حولي - النقرة", "Hawally", 29.3385, 48.0235, "Beirut St"),
        BusStop("hawally_park", "Hawally Park / Tunis St", "حديقة حولي", "Hawally", 29.3315, 48.0285, "4th Ring Interchange"),
        BusStop("mubarak_hosp", "Mubarak Al-Kabeer Hosp.", "مستشفى مبارك الكبير", "Jabriya", 29.3220, 48.0345, "4th Ring Medical Hub"),
        BusStop("salmiya_marina", "Salmiya Marina / Gulf Rd", "مارينا السالمية", "Salmiya", 29.3420, 48.0685, "Marina Mall & Beach"),
        BusStop("salmiya_coop", "Salmiya Co-Op / Olympia", "جمعية السالمية", "Salmiya", 29.3335, 48.0815, "Salem Al Mubarak St"),
        BusStop("salmiya_garden", "Salmiya Boulevard Park", "بوليفارد السالمية", "Salmiya", 29.3250, 48.0675, "5th Ring Terminal"),
        BusStop("rumaithiya", "Rumaithiya", "الرميثية", "Rumaithiya", 29.3110, 48.0755, "Taawon St"),
        BusStop("salwa", "Salwa Coastal", "سلوى", "Salwa", 29.2915, 48.0825, "Fahaheel Expwy Exit"),
        BusStop("south_surra_paci", "South Surra / PACI", "جنوب السرة - البطاقة المدنية", "South Surra", 29.2775, 48.0155, "Civil ID HQ & Ministries"),
        BusStop("mall_360", "360 Mall", "مول 360", "Al Zahra", 29.2682, 47.9935, "King Faisal / 6th Ring"),

        // Southern Coastal & Suburbs to Fahaheel
        BusStop("sabah_al_salem", "Sabah Al Salem", "صباح السالم", "Sabah Al Salem", 29.2570, 48.0725, "Fahaheel Expressway 30"),
        BusStop("messila", "Messila Water Towers", "المسيلة", "Messila", 29.2475, 48.0890, "6th Ring Coastal Junction"),
        BusStop("masayel", "Al-Masayel", "المسايل", "Masayel", 29.2310, 48.0750, "King Fahd Rd 40"),
        BusStop("qurain", "Al-Qurain Co-Op", "القرين", "Qurain", 29.2085, 48.0780, "Mubarak Al-Kabeer"),
        BusStop("fnaitees", "Al-Fnaitees", "الفنيطيس", "Fnaitees", 29.2110, 48.0965, "Coastal Rd 30"),
        BusStop("abu_fatira", "Abu Fatira", "أبو فطيرة", "Abu Fatira", 29.1935, 48.1010, "Highway 30"),
        BusStop("egaila", "Egaila / Al Bairaq Mall", "العقيلة", "Egaila", 29.1765, 48.0990, "Commercial Complex"),
        BusStop("fintas", "Al-Fintas", "الفنطاس", "Fintas", 29.1730, 48.1180, "Fintas Coastal"),
        BusStop("mahboula", "Mahboula Sea Side", "المهبولة", "Mahboula", 29.1465, 48.1265, "Coastal Boulevard"),
        BusStop("abu_halifa", "Abu Halifa", "أبو حليفة", "Abu Halifa", 29.1275, 48.1305, "Coastal Rd"),
        BusStop("mangaf", "Mangaf Beach", "المنقف", "Mangaf", 29.1010, 48.1345, "Sultan Center Mangaf"),
        BusStop("fahaheel_green_tower", "Fahaheel (Green Tower)", "الفحيحيل - البرج الأخضر", "Fahaheel", 29.0835, 48.1375, "Al Kout & Fahaheel Terminal")
    ).associateBy { it.id }

    private fun stop(id: String): BusStop = stopsMap.getValue(id)

    // --- ALL 23 REQUESTED KPTC KUWAIT BUS ROUTES ---
    val routes: List<BusRoute> = listOf(
        BusRoute(
            number = "11",
            titleEn = "Sharq – Amghara",
            origin = "Sharq",
            destination = "Amghara",
            corridorSummary = "Sharq • Maliya • Shuwaikh Port • Sabah Hosp • UN Circle • Sulaibikhat • Amghara",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("shuhada"), stop("sharq"), stop("ahmed_jaber"), stop("dasman"),
                stop("darwaza"), stop("mirqab"), stop("safat"), stop("muthanna"),
                stop("maliya"), stop("jahra_rd_2nd"), stop("jamal_abdul_nasser"),
                stop("flour_mills"), stop("shuwaikh_port"), stop("kuwait_univ_shuwaikh"),
                stop("sabah_hospital"), stop("al_razi"), stop("un_circle"),
                stop("sulaibikhat"), stop("nahdha"), stop("amghara")
            ),
            firstBus = "04:25 AM",
            lastBus = "10:25 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 58,
            distanceKm = 27.4,
            badgeColor = RouteCityColor,
            operationNotes = "Core northern coastal corridor connecting Kuwait City, Shuwaikh medical/port zones, and Amghara KPTC depot."
        ),
        BusRoute(
            number = "12",
            titleEn = "Kuwait City – Sulaibiya Industrial",
            origin = "Shuhada St",
            destination = "Sulaibiya Ind.",
            corridorSummary = "Shuhada St • Mirqab • Maliya • Shuwaikh Industrial • UN Circle • Sulaibiya",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("shuhada"), stop("darwaza"), stop("mirqab"), stop("safat"),
                stop("maliya"), stop("jahra_rd_2nd"), stop("city_center_shuwaikh"),
                stop("canada_dry"), stop("ghazali_bridge"), stop("un_circle"),
                stop("sulaibikhat"), stop("sulaibiya_ind")
            ),
            firstBus = "04:30 AM",
            lastBus = "10:15 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 52,
            distanceKm = 25.1,
            badgeColor = RouteCityColor,
            operationNotes = "Services central Kuwait City terminals down to Sulaibiya industrial & logistics sectors."
        ),
        BusRoute(
            number = "13",
            titleEn = "Mirqab – Kuwait Intl Airport",
            origin = "Mirqab",
            destination = "Kuwait Airport",
            corridorSummary = "Mirqab • Maliya • Khaldiya • Al Rai • Omariya • Khaitan • Farwaniya • Airport",
            category = RouteCategory.AIRPORT_MALL,
            stops = listOf(
                stop("mirqab"), stop("science_museum"), stop("safat"), stop("muthanna"),
                stop("maliya"), stop("jahra_rd_2nd"), stop("khaldiya"),
                stop("lulu_al_rai"), stop("kuwait_zoo"), stop("muscat_st"),
                stop("khaitan_police"), stop("awtad_mall"), stop("khaitan_park"),
                stop("holiday_inn_farwaniya"), stop("crowne_plaza"), stop("airport_t1")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:45 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 48,
            distanceKm = 22.8,
            badgeColor = RouteAirportMallColor,
            operationNotes = "Primary airport commuter line along Airport Road 55 serving Khaitan, Farwaniya, and Terminals 1 & 4."
        ),
        BusRoute(
            number = "15",
            titleEn = "Maliya – Salmiya",
            origin = "Maliya",
            destination = "Salmiya",
            corridorSummary = "Maliya • Mirqab • Darwaza • Shaab • Hawally • Mubarak Hosp • Salmiya Co-Op",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("maliya"), stop("safat"), stop("mirqab"), stop("darwaza"),
                stop("dasma"), stop("daiya"), stop("shaab_park"), stop("hawally_north"),
                stop("hawally_park"), stop("mubarak_hosp"), stop("salmiya_marina"),
                stop("salmiya_coop"), stop("salmiya_garden")
            ),
            firstBus = "04:25 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 12,
            peakFrequencyMinutes = 8,
            fareFils = 250,
            estimatedDurationMinutes = 38,
            distanceKm = 16.5,
            badgeColor = RouteCityColor,
            operationNotes = "High-frequency urban spine linking Kuwait City business core to Hawally and Salmiya."
        ),
        BusRoute(
            number = "16",
            titleEn = "Maliya – Jleeb Al-Shuyoukh",
            origin = "Maliya",
            destination = "Jleeb Terminal",
            corridorSummary = "Maliya • Jahra Rd • Al Rai • Khaitan • Farwaniya • Abbassiya • Jleeb",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("maliya"), stop("muthanna"), stop("jahra_rd_2nd"),
                stop("city_center_shuwaikh"), stop("sultan_al_rai"), stop("al_rai"),
                stop("muscat_st"), stop("khaitan_police"), stop("farwaniya_coop"),
                stop("jleeb_coop"), stop("abbassiya"), stop("jleeb_terminal")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 46,
            distanceKm = 19.6,
            badgeColor = RouteCityColor,
            operationNotes = "Direct link from Kuwait City Maliya hub through Khaitan and Farwaniya into Jleeb Al-Shuyoukh."
        ),
        BusRoute(
            number = "18",
            titleEn = "Mirqab – UN Circle",
            origin = "Mirqab",
            destination = "UN Circle",
            corridorSummary = "Mirqab • Fahad Al Salem • Canada Dry St • Toyota Parts • Sabah Hospital • UN Circle",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("mirqab"), stop("safat"), stop("maliya"), stop("city_center_shuwaikh"),
                stop("al_watan"), stop("canada_dry"), stop("toyota_parts"),
                stop("kuwait_univ_shuwaikh"), stop("sabah_hospital"), stop("al_razi"),
                stop("un_circle")
            ),
            firstBus = "04:40 AM",
            lastBus = "10:00 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 36,
            distanceKm = 15.2,
            badgeColor = RouteCityColor,
            operationNotes = "Dedicated industrial & medical zone service connecting Mirqab with Shuwaikh workshops and Sabah Medical District."
        ),
        BusRoute(
            number = "21",
            titleEn = "Mirqab – Jleeb (6th Ring)",
            origin = "Mirqab",
            destination = "Jleeb Terminal",
            corridorSummary = "Mirqab • Fahad Al Salem • Airport Rd 55 • Dajeej • 6th Ring • Jleeb • Abbassiya",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("mirqab"), stop("safat"), stop("maliya"), stop("khaldiya"),
                stop("kuwait_zoo"), stop("holiday_inn_farwaniya"), stop("dajeej_lulu"),
                stop("sixth_ring"), stop("jleeb_coop"), stop("abbassiya"),
                stop("german_clinic"), stop("jleeb_terminal")
            ),
            firstBus = "04:30 AM",
            lastBus = "10:20 PM",
            frequencyMinutes = 18,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 44,
            distanceKm = 20.3,
            badgeColor = RouteCityColor,
            operationNotes = "Fast Airport Road & 6th Ring connection from Mirqab into Abbassiya and Jleeb blocks."
        ),
        BusRoute(
            number = "23",
            titleEn = "Mirqab – Khaitan & Subhan",
            origin = "Mirqab",
            destination = "Subhan Ind.",
            corridorSummary = "Mirqab • Maliya • Airport Rd 50 • Khaitan • South Surra PACI • Subhan",
            category = RouteCategory.INTER_GOVERNORATE,
            stops = listOf(
                stop("mirqab"), stop("safat"), stop("maliya"), stop("khaldiya"),
                stop("muscat_st"), stop("khaitan_police"), stop("awtad_mall"),
                stop("new_khaitan"), stop("south_surra_paci"), stop("mall_360"),
                stop("subhan_ind")
            ),
            firstBus = "04:45 AM",
            lastBus = "09:50 PM",
            frequencyMinutes = 22,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 49,
            distanceKm = 23.0,
            badgeColor = RouteInterGovColor,
            operationNotes = "Serves government ministry complexes in South Surra (PACI) and Subhan industrial zone."
        ),
        BusRoute(
            number = "51",
            titleEn = "Sharq – Jleeb Al-Shuyoukh",
            origin = "Sharq",
            destination = "Jleeb Terminal",
            corridorSummary = "Sharq • Mirqab • Maliya • City Center • Lulu Farwaniya • Dajeej • Jleeb Co-Op",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("sharq"), stop("darwaza"), stop("mirqab"), stop("safat"),
                stop("maliya"), stop("jahra_rd_2nd"), stop("city_center_shuwaikh"),
                stop("lulu_al_rai"), stop("holiday_inn_farwaniya"), stop("dajeej_lulu"),
                stop("jleeb_coop"), stop("abbassiya"), stop("jleeb_terminal")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 47,
            distanceKm = 21.8,
            badgeColor = RouteCityColor,
            operationNotes = "Heavy commuter trunk line between Sharq/Mirqab and Dajeej/Jleeb residential blocks."
        ),
        BusRoute(
            number = "66",
            titleEn = "Salmiya – Hassawi (Jleeb)",
            origin = "Salmiya",
            destination = "Hassawi / Jleeb",
            corridorSummary = "Salmiya • Hawally • Mubarak Hosp • 4th Ring • Al Rai • Farwaniya • Hassawi",
            category = RouteCategory.INTER_GOVERNORATE,
            stops = listOf(
                stop("salmiya_coop"), stop("salmiya_garden"), stop("mubarak_hosp"),
                stop("hawally_park"), stop("khaldiya"), stop("sultan_al_rai"),
                stop("lulu_al_rai"), stop("avenues_mall"), stop("farwaniya_coop"),
                stop("jleeb_coop"), stop("abbassiya"), stop("jleeb_terminal")
            ),
            firstBus = "04:25 AM",
            lastBus = "10:25 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 45,
            distanceKm = 20.5,
            badgeColor = RouteInterGovColor,
            operationNotes = "East-West cross-town corridor linking Salmiya coast with Al Rai, The Avenues, and Hassawi."
        ),
        BusRoute(
            number = "66X",
            titleEn = "Salmiya – Hassawi Express",
            origin = "Salmiya",
            destination = "Hassawi Express",
            corridorSummary = "EXPRESS • Salmiya Blvd • 5th Ring Hwy • The Avenues • Farwaniya • Hassawi",
            category = RouteCategory.EXPRESS,
            stops = listOf(
                stop("salmiya_coop"), stop("salmiya_garden"), stop("mubarak_hosp"),
                stop("avenues_mall"), stop("centerpoint_rai"), stop("farwaniya_coop"),
                stop("jleeb_terminal")
            ),
            firstBus = "05:00 AM",
            lastBus = "10:00 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 30,
            distanceKm = 19.2,
            badgeColor = RouteExpressColor,
            operationNotes = "Limited-stop X-Press service bypassing inner Hawally blocks via 5th Ring Road."
        ),
        BusRoute(
            number = "77",
            titleEn = "Salmiya – Khaitan",
            origin = "Salmiya",
            destination = "Khaitan",
            corridorSummary = "Salmiya • Hawally • Mubarak Hosp • 4th Ring • Al Rai • Awtad Mall • Khaitan",
            category = RouteCategory.INTER_GOVERNORATE,
            stops = listOf(
                stop("salmiya_marina"), stop("salmiya_coop"), stop("salmiya_garden"),
                stop("hawally_north"), stop("hawally_park"), stop("mubarak_hosp"),
                stop("sultan_al_rai"), stop("safat_alghanim"), stop("lulu_al_rai"),
                stop("muscat_st"), stop("khaitan_police"), stop("awtad_mall"),
                stop("khaitan_park")
            ),
            firstBus = "04:25 AM",
            lastBus = "10:25 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 40,
            distanceKm = 16.8,
            badgeColor = RouteInterGovColor,
            operationNotes = "Popular daily commuter connection between Salmiya, Hawally, Al Rai, and Khaitan."
        ),
        BusRoute(
            number = "77X",
            titleEn = "Salmiya – Ardiya / Khaitan Express",
            origin = "Salmiya",
            destination = "Khaitan / Riggae",
            corridorSummary = "EXPRESS • Salmiya • 5th Ring • Centerpoint Al Rai • Khaitan • Riggae",
            category = RouteCategory.EXPRESS,
            stops = listOf(
                stop("salmiya_coop"), stop("salmiya_garden"), stop("hawally_park"),
                stop("avenues_mall"), stop("centerpoint_rai"), stop("khaitan_police"),
                stop("awtad_mall"), stop("riggae")
            ),
            firstBus = "05:00 AM",
            lastBus = "09:45 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 28,
            distanceKm = 17.5,
            badgeColor = RouteExpressColor,
            operationNotes = "Fast express corridor via 5th Ring Road linking Salmiya with Al Rai, Khaitan, and Ardiya/Riggae."
        ),
        BusRoute(
            number = "999",
            titleEn = "Maliya – Fahaheel (Coastal Trunk)",
            origin = "Maliya (Sheraton)",
            destination = "Fahaheel",
            corridorSummary = "Maliya • Shaab • Salmiya • Rumaithiya • Salwa • Messila • Fintas • Mangaf • Fahaheel",
            category = RouteCategory.COASTAL_SOUTH,
            stops = listOf(
                stop("maliya"), stop("mirqab"), stop("dasma"), stop("shaab_park"),
                stop("salmiya_marina"), stop("salmiya_coop"), stop("rumaithiya"),
                stop("salwa"), stop("sabah_al_salem"), stop("messila"),
                stop("fnaitees"), stop("abu_fatira"), stop("fintas"),
                stop("mahboula"), stop("abu_halifa"), stop("mangaf"),
                stop("fahaheel_green_tower")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:40 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 10,
            fareFils = 250,
            estimatedDurationMinutes = 68,
            distanceKm = 43.5,
            badgeColor = RouteCoastalColor,
            operationNotes = "Iconic north-to-south coastal flagship connecting Maliya Sheraton through Salmiya to Fahaheel Green Tower."
        ),
        BusRoute(
            number = "103",
            titleEn = "Mirqab – Al Jahra",
            origin = "Mirqab",
            destination = "Jahra Co-Op",
            corridorSummary = "Mirqab • Jahra Rd 80 • Ghazali Bridge • UN Circle • Sulaibikhat • Amghara • Jahra",
            category = RouteCategory.INTER_GOVERNORATE,
            stops = listOf(
                stop("mirqab"), stop("safat"), stop("maliya"), stop("jahra_rd_2nd"),
                stop("city_center_shuwaikh"), stop("ghazali_bridge"), stop("sabah_hospital"),
                stop("un_circle"), stop("sulaibikhat"), stop("nahdha"),
                stop("amghara"), stop("jahra_sports"), stop("jahra_roundabout"),
                stop("jahra_coop")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 56,
            distanceKm = 36.2,
            badgeColor = RouteInterGovColor,
            operationNotes = "Main western inter-governorate trunk along Jahra Road (Route 80) to Al Jahra."
        ),
        BusRoute(
            number = "106",
            titleEn = "Fahaheel – Jleeb / Hassawi",
            origin = "Fahaheel",
            destination = "Jleeb / Hassawi",
            corridorSummary = "Fahaheel • Mangaf • Fintas • Messila • 360 Mall • South Surra PACI • Farwaniya • Jleeb",
            category = RouteCategory.COASTAL_SOUTH,
            stops = listOf(
                stop("fahaheel_green_tower"), stop("mangaf"), stop("abu_halifa"),
                stop("mahboula"), stop("fintas"), stop("egaila"),
                stop("messila"), stop("sabah_al_salem"), stop("south_surra_paci"),
                stop("mall_360"), stop("crowne_plaza"), stop("farwaniya_coop"),
                stop("jleeb_coop"), stop("jleeb_terminal")
            ),
            firstBus = "04:25 AM",
            lastBus = "10:25 PM",
            frequencyMinutes = 18,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 65,
            distanceKm = 40.8,
            badgeColor = RouteCoastalColor,
            operationNotes = "Major southern residential & commercial connector via 6th Ring Road, 360 Mall, and South Surra."
        ),
        BusRoute(
            number = "106X",
            titleEn = "Fahaheel – Jleeb Express",
            origin = "Fahaheel",
            destination = "Jleeb Express",
            corridorSummary = "EXPRESS • Fahaheel • Mangaf • Egaila • 360 Mall • Dajeej • Jleeb Terminal",
            category = RouteCategory.EXPRESS,
            stops = listOf(
                stop("fahaheel_green_tower"), stop("mangaf"), stop("mahboula"),
                stop("egaila"), stop("mall_360"), stop("dajeej_lulu"),
                stop("jleeb_terminal")
            ),
            firstBus = "05:00 AM",
            lastBus = "09:45 PM",
            frequencyMinutes = 25,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 44,
            distanceKm = 39.0,
            badgeColor = RouteExpressColor,
            operationNotes = "Long-distance express line bypassing intermediate local loops along King Fahd Rd 40 & 6th Ring."
        ),
        BusRoute(
            number = "102X",
            titleEn = "Maliya – Fahaheel Express",
            origin = "Maliya",
            destination = "Fahaheel Express",
            corridorSummary = "EXPRESS • Maliya • Hawally • Sabah Al Salem • Fintas • Mangaf • Fahaheel",
            category = RouteCategory.EXPRESS,
            stops = listOf(
                stop("maliya"), stop("mirqab"), stop("hawally_park"),
                stop("sabah_al_salem"), stop("egaila"), stop("fintas"),
                stop("mahboula"), stop("mangaf"), stop("fahaheel_green_tower")
            ),
            firstBus = "05:00 AM",
            lastBus = "10:00 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 46,
            distanceKm = 41.2,
            badgeColor = RouteExpressColor,
            operationNotes = "Express highway variant of 102A skipping neighborhood blocks between Kuwait City and Fahaheel."
        ),
        BusRoute(
            number = "102A",
            titleEn = "Maliya – Fahaheel (Local)",
            origin = "Maliya",
            destination = "Fahaheel",
            corridorSummary = "Maliya • Dasma • Daiya • Hawally • Sabah Al Salem • Messila • Masayel • Fintas • Mangaf • Fahaheel",
            category = RouteCategory.COASTAL_SOUTH,
            stops = listOf(
                stop("maliya"), stop("mirqab"), stop("dasma"), stop("daiya"),
                stop("hawally_north"), stop("hawally_park"), stop("sabah_al_salem"),
                stop("messila"), stop("masayel"), stop("fnaitees"),
                stop("abu_fatira"), stop("egaila"), stop("fintas"),
                stop("mahboula"), stop("abu_halifa"), stop("mangaf"),
                stop("fahaheel_green_tower")
            ),
            firstBus = "04:30 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 72,
            distanceKm = 44.6,
            badgeColor = RouteCoastalColor,
            operationNotes = "Comprehensive south corridor stopping at Dasma, Hawally, Masayel, Fnaitees, Egaila, Mahboula, and Fahaheel."
        ),
        BusRoute(
            number = "507",
            titleEn = "Mirqab – Jleeb Al-Shuyoukh",
            origin = "Mirqab",
            destination = "Jleeb Terminal",
            corridorSummary = "Mirqab • Fahad Al Salem • Maliya • City Center • Canada Dry • Al Rai • 60 Rd • 6th Ring • Jleeb",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("mirqab"), stop("safat"), stop("maliya"), stop("jahra_rd_2nd"),
                stop("city_center_shuwaikh"), stop("al_watan"), stop("canada_dry"),
                stop("toyota_parts"), stop("al_naki"), stop("sultan_al_rai"),
                stop("safat_alghanim"), stop("al_rai"), stop("centerpoint_rai"),
                stop("road_60"), stop("sixth_ring"), stop("jleeb_coop"),
                stop("abbassiya"), stop("german_clinic"), stop("jleeb_terminal")
            ),
            firstBus = "04:20 AM",
            lastBus = "10:30 PM",
            frequencyMinutes = 12,
            peakFrequencyMinutes = 8,
            fareFils = 250,
            estimatedDurationMinutes = 50,
            distanceKm = 21.4,
            badgeColor = RouteCityColor,
            operationNotes = "Full 19-stop classic commuter route traversing Shuwaikh Industrial, Al Rai commercial spine, and Jleeb."
        ),
        BusRoute(
            number = "507S",
            titleEn = "Sharq – Jleeb South Branch",
            origin = "Sharq",
            destination = "Jleeb South",
            corridorSummary = "Sharq • Mirqab • Airport Rd 55 • Holiday Inn Farwaniya • Dajeej • Jleeb Co-Op • Jleeb",
            category = RouteCategory.CITY,
            stops = listOf(
                stop("sharq"), stop("darwaza"), stop("mirqab"), stop("maliya"),
                stop("khaldiya"), stop("lulu_al_rai"), stop("holiday_inn_farwaniya"),
                stop("crowne_plaza"), stop("dajeej_lulu"), stop("sixth_ring"),
                stop("jleeb_coop"), stop("german_clinic"), stop("jleeb_terminal")
            ),
            firstBus = "04:35 AM",
            lastBus = "10:15 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 14,
            fareFils = 250,
            estimatedDurationMinutes = 45,
            distanceKm = 20.1,
            badgeColor = RouteCityColor,
            operationNotes = "Southern branch of 507 routing via Airport Road 55, Farwaniya, and Dajeej instead of Shuwaikh Industrial."
        ),
        BusRoute(
            number = "A2",
            titleEn = "Khaitan – The Avenues – Riggae",
            origin = "Khaitan / Subhan",
            destination = "The Avenues & Riggae",
            corridorSummary = "Subhan • Khaitan • Farwaniya • Al Rai • The Avenues Mall • Riggae",
            category = RouteCategory.AIRPORT_MALL,
            stops = listOf(
                stop("subhan_ind"), stop("new_khaitan"), stop("khaitan_park"),
                stop("awtad_mall"), stop("khaitan_police"), stop("farwaniya_coop"),
                stop("lulu_al_rai"), stop("avenues_mall"), stop("centerpoint_rai"),
                stop("riggae")
            ),
            firstBus = "05:00 AM",
            lastBus = "11:00 PM",
            frequencyMinutes = 15,
            peakFrequencyMinutes = 12,
            fareFils = 250,
            estimatedDurationMinutes = 34,
            distanceKm = 18.4,
            badgeColor = RouteAirportMallColor,
            operationNotes = "Dedicated Avenues Mall feeder linking Subhan, Khaitan, and Farwaniya directly to The Avenues & Riggae."
        ),
        BusRoute(
            number = "105",
            titleEn = "Fahaheel – Hassawi / Jleeb",
            origin = "Fahaheel",
            destination = "Hassawi / Jleeb",
            corridorSummary = "Fahaheel • Mangaf • Abu Halifa • Fintas • Qurain • South Surra PACI • New Khaitan • Hassawi",
            category = RouteCategory.COASTAL_SOUTH,
            stops = listOf(
                stop("fahaheel_green_tower"), stop("mangaf"), stop("abu_halifa"),
                stop("fintas"), stop("qurain"), stop("masayel"),
                stop("south_surra_paci"), stop("new_khaitan"), stop("crowne_plaza"),
                stop("dajeej_lulu"), stop("jleeb_coop"), stop("jleeb_terminal")
            ),
            firstBus = "04:30 AM",
            lastBus = "10:15 PM",
            frequencyMinutes = 20,
            peakFrequencyMinutes = 15,
            fareFils = 250,
            estimatedDurationMinutes = 62,
            distanceKm = 39.4,
            badgeColor = RouteCoastalColor,
            operationNotes = "Inland-coastal link passing through Al-Qurain, South Surra PACI, and New Khaitan to Hassawi."
        )
    )

    val routesByNumber: Map<String, BusRoute> = routes.associateBy { it.number }

    val allStops: List<BusStop> = stopsMap.values.sortedBy { it.nameEn }

    fun routesServingStop(stopId: String): List<BusRoute> {
        return routes.filter { route -> route.stops.any { it.id == stopId } }
    }

    // --- KUWAIT COASTLINE & MAJOR HIGHWAYS FOR VECTOR MAP ---
    val kuwaitCoastline: List<MapPoint> = listOf(
        // North/West Kuwait Bay shore (Subiya / Jahra Bay head)
        MapPoint(29.380, 47.640),
        MapPoint(29.365, 47.705),
        MapPoint(29.350, 47.765),
        MapPoint(29.338, 47.825),
        MapPoint(29.354, 47.882),
        MapPoint(29.362, 47.925),
        MapPoint(29.376, 47.960),
        MapPoint(29.392, 47.982), // Kuwait City North Tip
        MapPoint(29.390, 48.005), // Kuwait Towers / Dasman
        MapPoint(29.366, 48.022), // Bneid Al Gar
        MapPoint(29.352, 48.055), // Shaab
        MapPoint(29.352, 48.096), // Ras Al Ardh / Salmiya Tip
        MapPoint(29.326, 48.094), // Salmiya East Coast
        MapPoint(29.295, 48.092), // Salwa Coast
        MapPoint(29.260, 48.096), // Messila Coast
        MapPoint(29.222, 48.106), // Fnaitees Coast
        MapPoint(29.180, 48.125), // Fintas Coast
        MapPoint(29.146, 48.136), // Mahboula Coast
        MapPoint(29.110, 48.145), // Mangaf Coast
        MapPoint(29.078, 48.150), // Fahaheel Al Kout Coast
        MapPoint(29.040, 48.155), // Ahmadi / Mina Abdullah South
        MapPoint(29.040, 48.195), // Off-map East Sea Corner
        MapPoint(29.425, 48.195), // Off-map NE Sea Corner
        MapPoint(29.425, 47.780), // North Bay Sea
        MapPoint(29.395, 47.640)
    )

    val kuwaitHighways: List<HighwayPath> = listOf(
        HighwayPath(
            name = "Gulf Rd",
            points = listOf(
                MapPoint(29.374, 47.964),
                MapPoint(29.389, 47.985),
                MapPoint(29.388, 48.005),
                MapPoint(29.352, 48.042),
                MapPoint(29.349, 48.091),
                MapPoint(29.318, 48.088),
                MapPoint(29.255, 48.092)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "4th Ring",
            points = listOf(
                MapPoint(29.335, 47.880),
                MapPoint(29.326, 47.940),
                MapPoint(29.324, 48.025),
                MapPoint(29.332, 48.082)
            ),
            isMajor = false
        ),
        HighwayPath(
            name = "5th Ring",
            points = listOf(
                MapPoint(29.310, 47.865),
                MapPoint(29.302, 47.934),
                MapPoint(29.302, 48.020),
                MapPoint(29.318, 48.078)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "6th Ring",
            points = listOf(
                MapPoint(29.295, 47.740),
                MapPoint(29.272, 47.880),
                MapPoint(29.266, 47.945),
                MapPoint(29.268, 48.005),
                MapPoint(29.250, 48.085)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "Jahra Rd (80)",
            points = listOf(
                MapPoint(29.366, 47.962),
                MapPoint(29.345, 47.910),
                MapPoint(29.336, 47.855),
                MapPoint(29.322, 47.780),
                MapPoint(29.340, 47.665)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "Airport Rd (55)",
            points = listOf(
                MapPoint(29.367, 47.975),
                MapPoint(29.328, 47.968),
                MapPoint(29.290, 47.962),
                MapPoint(29.228, 47.969)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "Fahaheel Expwy (30)",
            points = listOf(
                MapPoint(29.368, 48.002),
                MapPoint(29.325, 48.045),
                MapPoint(29.270, 48.074),
                MapPoint(29.190, 48.105),
                MapPoint(29.082, 48.136)
            ),
            isMajor = true
        ),
        HighwayPath(
            name = "King Fahd Rd (40)",
            points = listOf(
                MapPoint(29.367, 47.982),
                MapPoint(29.315, 48.015),
                MapPoint(29.250, 48.045),
                MapPoint(29.165, 48.095),
                MapPoint(29.080, 48.122)
            ),
            isMajor = false
        ),
        HighwayPath(
            name = "Ghazali (60)",
            points = listOf(
                MapPoint(29.354, 47.918),
                MapPoint(29.312, 47.926),
                MapPoint(29.268, 47.935),
                MapPoint(29.228, 47.958)
            ),
            isMajor = false
        )
    )
}
