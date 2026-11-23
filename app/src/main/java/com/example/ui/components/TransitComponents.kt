package com.example.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OccupancyLevel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.theme.DesertGold
import com.example.ui.theme.ExpressCrimson
import com.example.ui.theme.KuwaitEmerald
import com.example.ui.theme.MonoFontFamily

@Composable
fun LanguageSwitchPill(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onToggle)
            .testTag("language_toggle_button"),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Switch language",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (language == AppLanguage.ENGLISH) "العربية" else "EN",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RouteBadgePill(
    routeNumber: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(if (compact) 8.dp else 12.dp)
    val baseMod = modifier
        .clip(shape)
        .background(badgeColor)
        .then(
            if (onClick != null) {
                Modifier
                    .defaultMinSize(minWidth = 44.dp, minHeight = 32.dp)
                    .clickable(onClick = onClick)
            } else {
                Modifier
            }
        )
        .padding(
            horizontal = if (compact) 8.dp else 12.dp,
            vertical = if (compact) 4.dp else 7.dp
        )

    Box(
        modifier = baseMod,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = routeNumber,
            color = Color.White,
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 12.sp else 16.sp,
            maxLines = 1
        )
    }
}

@Composable
fun TelemetryStatusBadge(
    isServiceOperating: Boolean,
    activeBusCount: Int,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    var showInfoDialog by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseColor by infiniteTransition.animateColor(
        initialValue = KuwaitEmerald,
        targetValue = KuwaitEmerald.copy(alpha = 0.35f),
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_color"
    )

    val label = AppStrings.telemetryLabel(isServiceOperating, activeBusCount, lang)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable { showInfoDialog = true }
            .testTag("telemetry_info_badge"),
        color = KuwaitEmerald.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(pulseColor)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = KuwaitEmerald,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = AppStrings.telemetryDialogTitle(lang),
                tint = KuwaitEmerald,
                modifier = Modifier.size(14.dp)
            )
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(AppStrings.telemetryDialogTitle(lang)) },
            text = { Text(AppStrings.telemetryDialogBody(lang)) },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(AppStrings.gotIt(lang))
                }
            }
        )
    }
}

@Composable
fun OccupancyBadge(
    occupancy: OccupancyLevel,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val color = when (occupancy) {
        OccupancyLevel.LOW -> KuwaitEmerald
        OccupancyLevel.MODERATE -> DesertGold
        OccupancyLevel.HIGH -> ExpressCrimson
    }
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = AppStrings.occupancyLabel(occupancy, lang),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1
        )
    }
}

@Composable
fun MetricChip(
    label: String,
    value: String,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    minWidth: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.defaultMinSize(minWidth = minWidth),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
