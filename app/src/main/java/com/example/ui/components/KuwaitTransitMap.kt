package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.KuwaitBusData
import com.example.location.UserCoordinates
import com.example.model.BusRoute
import com.example.model.BusStop
import com.example.model.LiveBus
import com.example.ui.localization.AppStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KptcSkyAccent
import com.example.ui.theme.KuwaitEmerald
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun KuwaitTransitMap(
    routes: List<BusRoute>,
    liveBuses: List<LiveBus>,
    focusedRoute: BusRoute?,
    selectedBusId: String?,
    selectedStopId: String?,
    userCoordinates: UserCoordinates?,
    onBusTapped: (LiveBus) -> Unit,
    onStopTapped: (BusStop) -> Unit,
    onLocationButtonClick: (() -> Unit)? = null,
    showControls: Boolean = true,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isDark = isSystemInDarkTheme()

    var zoom by remember { mutableFloatStateOf(1.15f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Auto-frame focused route when selected
    LaunchedEffect(focusedRoute?.number, canvasSize) {
        if (focusedRoute != null && canvasSize.width > 0f && canvasSize.height > 0f) {
            val lats = focusedRoute.stops.map { it.latitude }
            val lngs = focusedRoute.stops.map { it.longitude }
            val minLat = lats.minOrNull() ?: KuwaitBusData.MIN_LAT
            val maxLat = lats.maxOrNull() ?: KuwaitBusData.MAX_LAT
            val minLng = lngs.minOrNull() ?: KuwaitBusData.MIN_LNG
            val maxLng = lngs.maxOrNull() ?: KuwaitBusData.MAX_LNG

            val spanLat = (maxLat - minLat).coerceAtLeast(0.04)
            val spanLng = (maxLng - minLng).coerceAtLeast(0.05)
            val totalLat = KuwaitBusData.MAX_LAT - KuwaitBusData.MIN_LAT
            val totalLng = KuwaitBusData.MAX_LNG - KuwaitBusData.MIN_LNG

            val targetZoom = minOf(
                (totalLng / spanLng).toFloat() * 0.68f,
                (totalLat / spanLat).toFloat() * 0.68f
            ).coerceIn(1.1f, 3.6f)

            val centerLat = (minLat + maxLat) / 2.0
            val centerLng = (minLng + maxLng) / 2.0
            val baseCenter = projectToBase(centerLat, centerLng, canvasSize)
            val screenCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

            zoom = targetZoom
            panOffset = Offset(
                x = (screenCenter.x - baseCenter.x) * targetZoom,
                y = (screenCenter.y - baseCenter.y) * targetZoom
            )
        } else if (focusedRoute == null) {
            zoom = 1.15f
            panOffset = Offset.Zero
        }
    }

    val landBg = if (isDark) Color(0xFF0E1B2B) else Color(0xFFF3EFE6)
    val seaBg = if (isDark) Color(0xFF072642) else Color(0xFFBEE3F8)
    val seaStroke = if (isDark) Color(0xFF1A4F7A) else Color(0xFF7BBCE3)
    val roadColorMajor = if (isDark) Color(0xFF283E59) else Color(0xFFDAD3C5)
    val roadColorMinor = if (isDark) Color(0xFF1D3047) else Color(0xFFE6E0D4)
    val gridColor = if (isDark) Color(0xFF16273B) else Color(0xFFEAE4D7)
    val labelTextColor = if (isDark) Color(0xFF9AB4CC) else Color(0xFF4B5E71)

    val displayedRoutes = remember(routes, focusedRoute) {
        if (focusedRoute != null) listOf(focusedRoute) else routes
    }
    val displayedStops = remember(displayedRoutes) {
        displayedRoutes.flatMap { it.stops }.distinctBy { it.id }
    }
    val displayedBuses = remember(liveBuses, focusedRoute) {
        if (focusedRoute != null) {
            liveBuses.filter { it.routeNumber == focusedRoute.number }
        } else {
            liveBuses
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(landBg)
            .testTag("kuwait_transit_map")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        val newZoom = (zoom * gestureZoom).coerceIn(0.85f, 5.0f)
                        val maxPanX = size.width * newZoom * 0.7f
                        val maxPanY = size.height * newZoom * 0.7f
                        zoom = newZoom
                        panOffset = Offset(
                            x = (panOffset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                            y = (panOffset.y + pan.y).coerceIn(-maxPanY, maxPanY)
                        )
                    }
                }
                .pointerInput(displayedBuses, displayedStops, zoom, panOffset) {
                    detectTapGestures { tapOffset ->
                        // Check live buses first (higher priority)
                        val tappedBus = displayedBuses.minByOrNull { bus ->
                            val pos = toScreen(bus.latitude, bus.longitude, canvasSize, zoom, panOffset)
                            hypot((pos.x - tapOffset.x).toDouble(), (pos.y - tapOffset.y).toDouble())
                        }
                        if (tappedBus != null) {
                            val busPos = toScreen(tappedBus.latitude, tappedBus.longitude, canvasSize, zoom, panOffset)
                            if (hypot((busPos.x - tapOffset.x).toDouble(), (busPos.y - tapOffset.y).toDouble()) < 54.0) {
                                onBusTapped(tappedBus)
                                return@detectTapGestures
                            }
                        }

                        // Otherwise check bus stops
                        val tappedStop = displayedStops.minByOrNull { stop ->
                            val pos = toScreen(stop.latitude, stop.longitude, canvasSize, zoom, panOffset)
                            hypot((pos.x - tapOffset.x).toDouble(), (pos.y - tapOffset.y).toDouble())
                        }
                        if (tappedStop != null) {
                            val stopPos = toScreen(tappedStop.latitude, tappedStop.longitude, canvasSize, zoom, panOffset)
                            if (hypot((stopPos.x - tapOffset.x).toDouble(), (stopPos.y - tapOffset.y).toDouble()) < 48.0) {
                                onStopTapped(tappedStop)
                            }
                        }
                    }
                }
        ) {
            canvasSize = size

            // 1. Subtle Cartographic Grid
            val step = 60f * zoom.coerceAtMost(2f)
            var gx = (panOffset.x % step)
            while (gx < size.width) {
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
                gx += step
            }
            var gy = (panOffset.y % step)
            while (gy < size.height) {
                drawLine(gridColor, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
                gy += step
            }

            // 2. Persian Gulf & Kuwait Bay Sea Polygon
            val seaPath = Path()
            KuwaitBusData.kuwaitCoastline.forEachIndexed { idx, pt ->
                val p = toScreen(pt.latitude, pt.longitude, size, zoom, panOffset)
                if (idx == 0) seaPath.moveTo(p.x, p.y) else seaPath.lineTo(p.x, p.y)
            }
            seaPath.close()
            drawPath(seaPath, color = seaBg)
            drawPath(seaPath, color = seaStroke, style = Stroke(width = 2.5f))

            // 3. Major Kuwait Highways & Ring Roads
            KuwaitBusData.kuwaitHighways.forEach { hw ->
                val path = Path()
                hw.points.forEachIndexed { idx, pt ->
                    val p = toScreen(pt.latitude, pt.longitude, size, zoom, panOffset)
                    if (idx == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                drawPath(
                    path = path,
                    color = if (hw.isMajor) roadColorMajor else roadColorMinor,
                    style = Stroke(
                        width = if (hw.isMajor) 4.5f * zoom.coerceAtMost(1.8f) else 2.5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // 4. Watermark Label for Kuwait Bay
            val bayLabelPos = toScreen(29.386, 47.870, size, zoom, panOffset)
            drawMapLabel(
                text = AppStrings.kuwaitBayLabel(lang),
                x = bayLabelPos.x,
                y = bayLabelPos.y,
                color = seaStroke.copy(alpha = 0.85f),
                textSizePx = 24f,
                bold = true
            )

            // 5. Route Polylines (unfocused dimmed when showing all, vibrant when focused)
            val routeAlpha = if (focusedRoute != null) 0.92f else 0.42f
            val routeStrokeWidth = if (focusedRoute != null) 8f else 3.5f

            displayedRoutes.forEach { route ->
                if (route.stops.size >= 2) {
                    val path = Path()
                    route.stops.forEachIndexed { idx, st ->
                        val p = toScreen(st.latitude, st.longitude, size, zoom, panOffset)
                        if (idx == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                    }
                    if (focusedRoute != null) {
                        // Outer soft glow
                        drawPath(
                            path = path,
                            color = route.badgeColor.copy(alpha = 0.22f),
                            style = Stroke(
                                width = 18f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    drawPath(
                        path = path,
                        color = route.badgeColor.copy(alpha = routeAlpha),
                        style = Stroke(
                            width = routeStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = if (route.number.endsWith("X") && focusedRoute != null) {
                                PathEffect.dashPathEffect(floatArrayOf(22f, 10f), 0f)
                            } else {
                                null
                            }
                        )
                    )
                }
            }

            // 6. Bus Stops
            displayedStops.forEachIndexed { idx, stop ->
                val p = toScreen(stop.latitude, stop.longitude, size, zoom, panOffset)
                if (p.x < -60f || p.x > size.width + 60f || p.y < -60f || p.y > size.height + 60f) return@forEachIndexed

                val isSelected = stop.id == selectedStopId
                val isTerminal = focusedRoute != null &&
                    (stop.id == focusedRoute.stops.firstOrNull()?.id || stop.id == focusedRoute.stops.lastOrNull()?.id)

                val radius = when {
                    isSelected -> 11f
                    isTerminal -> 9f
                    focusedRoute != null -> 6.5f
                    else -> 4.2f
                }

                drawCircle(
                    color = if (isSelected) DesertGold else Color.White,
                    radius = radius + 2.5f,
                    center = p
                )
                drawCircle(
                    color = when {
                        isSelected -> DesertGold
                        isTerminal -> focusedRoute?.badgeColor ?: KptcRoyalBlue
                        focusedRoute != null -> focusedRoute.badgeColor
                        else -> KptcRoyalBlue
                    },
                    radius = radius,
                    center = p
                )

                val showStopLabel = isSelected || isTerminal || (focusedRoute != null && (zoom >= 1.5f || idx % 2 == 0)) ||
                    (focusedRoute == null && stop.id in MAJOR_HUB_IDS && zoom >= 1.0f)

                if (showStopLabel) {
                    drawMapLabel(
                        text = AppStrings.stopName(stop, lang),
                        x = p.x,
                        y = p.y - radius - 10f,
                        color = if (isSelected) DesertGold else labelTextColor,
                        textSizePx = if (isSelected || isTerminal) 25f else 20f,
                        bold = isSelected || isTerminal
                    )
                }
            }

            // 7. User GPS Location Marker (if available)
            if (userCoordinates != null) {
                val up = toScreen(userCoordinates.latitude, userCoordinates.longitude, size, zoom, panOffset)
                drawCircle(color = KptcSkyAccent.copy(alpha = 0.22f), radius = 24f, center = up)
                drawCircle(color = Color.White, radius = 10f, center = up)
                drawCircle(color = KptcSkyAccent, radius = 7f, center = up)
            }

            // 8. Live Moving Bus Markers
            displayedBuses.forEach { bus ->
                val p = toScreen(bus.latitude, bus.longitude, size, zoom, panOffset)
                if (p.x < -80f || p.x > size.width + 80f || p.y < -80f || p.y > size.height + 80f) return@forEach

                val routeColor = KuwaitBusData.routesByNumber[bus.routeNumber]?.badgeColor ?: KptcRoyalBlue
                val isSelected = bus.busId == selectedBusId

                // Live pulse halo
                drawCircle(
                    color = (if (isSelected) DesertGold else KuwaitEmerald).copy(alpha = 0.28f),
                    radius = if (isSelected) 28f else 20f,
                    center = p
                )

                // Directional heading cone
                val angleRad = (bus.headingDegrees - 90f) * (PI / 180.0)
                val tip = Offset(
                    x = p.x + (cos(angleRad) * 24f).toFloat(),
                    y = p.y + (sin(angleRad) * 24f).toFloat()
                )
                val left = Offset(
                    x = p.x + (cos(angleRad + 2.4) * 11f).toFloat(),
                    y = p.y + (sin(angleRad + 2.4) * 11f).toFloat()
                )
                val right = Offset(
                    x = p.x + (cos(angleRad - 2.4) * 11f).toFloat(),
                    y = p.y + (sin(angleRad - 2.4) * 11f).toFloat()
                )
                val arrowPath = Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(left.x, left.y)
                    lineTo(right.x, right.y)
                    close()
                }
                drawPath(arrowPath, color = if (isSelected) DesertGold else routeColor)

                // Pill badge for Bus Route Number
                val pillWidth = if (bus.routeNumber.length >= 4) 58f else 46f
                val pillHeight = 28f
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(p.x - pillWidth / 2f - 2f, p.y - pillHeight / 2f - 2f),
                    size = Size(pillWidth + 4f, pillHeight + 4f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = if (isSelected) DesertGold else routeColor,
                    topLeft = Offset(p.x - pillWidth / 2f, p.y - pillHeight / 2f),
                    size = Size(pillWidth, pillHeight),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                drawMapLabel(
                    text = bus.routeNumber,
                    x = p.x,
                    y = p.y + 7f,
                    color = Color.White,
                    textSizePx = 20f,
                    bold = true
                )
            }
        }

        // Top-Left Kuwait Map Legend Chip
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = AppStrings.mapOverlayLegend(focusedRoute, displayedBuses.size, lang),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        // Map Zoom & Recenter Controls
        if (showControls) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onLocationButtonClick != null) {
                    FilledTonalIconButton(
                        onClick = onLocationButtonClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("map_my_location_button"),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Locate nearest Kuwait stations"
                        )
                    }
                }
                FilledTonalIconButton(
                    onClick = { zoom = (zoom * 1.3f).coerceAtMost(5.0f) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("map_zoom_in_button"),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom in")
                }
                FilledTonalIconButton(
                    onClick = { zoom = (zoom / 1.3f).coerceAtLeast(0.85f) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("map_zoom_out_button"),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom out")
                }
                FilledTonalIconButton(
                    onClick = {
                        zoom = 1.15f
                        panOffset = Offset.Zero
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("map_recenter_button"),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Reset Kuwait map view"
                    )
                }
            }
        }
    }
}

private val MAJOR_HUB_IDS = setOf(
    "mirqab", "maliya", "sharq", "salmiya_coop", "airport_t1",
    "avenues_mall", "jleeb_terminal", "jahra_coop", "fahaheel_green_tower",
    "khaitan_police", "mall_360", "un_circle", "amghara"
)

private fun projectToBase(lat: Double, lng: Double, size: Size): Offset {
    if (size.width <= 0f || size.height <= 0f) return Offset.Zero
    val pad = 32f
    val usableW = (size.width - pad * 2f).coerceAtLeast(1f)
    val usableH = (size.height - pad * 2f).coerceAtLeast(1f)

    // Aspect-preserving equirectangular projection adjusted for Kuwait latitude (~29.3°N)
    val cosLat = cos(29.3 * PI / 180.0)
    val worldW = (KuwaitBusData.MAX_LNG - KuwaitBusData.MIN_LNG) * cosLat
    val worldH = (KuwaitBusData.MAX_LAT - KuwaitBusData.MIN_LAT)
    val scale = minOf(usableW / worldW.toFloat(), usableH / worldH.toFloat())

    val renderedW = worldW.toFloat() * scale
    val renderedH = worldH.toFloat() * scale
    val offsetX = (size.width - renderedW) / 2f
    val offsetY = (size.height - renderedH) / 2f

    val x = offsetX + ((lng - KuwaitBusData.MIN_LNG) * cosLat).toFloat() * scale
    val y = offsetY + (KuwaitBusData.MAX_LAT - lat).toFloat() * scale
    return Offset(x, y)
}

private fun toScreen(lat: Double, lng: Double, size: Size, zoom: Float, pan: Offset): Offset {
    val base = projectToBase(lat, lng, size)
    val cx = size.width / 2f
    val cy = size.height / 2f
    return Offset(
        x = cx + (base.x - cx) * zoom + pan.x,
        y = cy + (base.y - cy) * zoom + pan.y
    )
}

private fun DrawScope.drawMapLabel(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    textSizePx: Float,
    bold: Boolean
) {
    val paint = Paint().apply {
        this.color = color.toArgb()
        this.textSize = textSizePx
        this.textAlign = Paint.Align.CENTER
        this.isAntiAlias = true
        this.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}
