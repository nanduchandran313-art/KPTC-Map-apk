package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = KptcSkyAccent,
    onPrimary = Color.White,
    primaryContainer = KptcRoyalBlueDark,
    onPrimaryContainer = KptcSkyLight,
    secondary = KuwaitEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0A3E32),
    onSecondaryContainer = KuwaitEmeraldLight,
    tertiary = DesertGold,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF472C04),
    onTertiaryContainer = DesertGoldLight,
    error = ExpressCrimson,
    onError = Color.White,
    background = SurfaceDark,
    onBackground = InkLight,
    surface = SurfaceCardDark,
    onSurface = InkLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = InkMutedDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF1D3148)
)

private val LightColorScheme = lightColorScheme(
    primary = KptcRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = KptcSkyLight,
    onPrimaryContainer = KptcRoyalBlueDark,
    secondary = KuwaitEmerald,
    onSecondary = Color.White,
    secondaryContainer = KuwaitEmeraldLight,
    onSecondaryContainer = Color(0xFF05382B),
    tertiary = DesertGold,
    onTertiary = Color.White,
    tertiaryContainer = DesertGoldLight,
    onTertiaryContainer = Color(0xFF4A2B00),
    error = ExpressCrimson,
    onError = Color.White,
    background = SurfaceLight,
    onBackground = InkDark,
    surface = SurfaceCardLight,
    onSurface = InkDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkMutedLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFE3EAF2)
)

val KptcShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun KptcKuwaitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KptcTypography,
        shapes = KptcShapes,
        content = content
    )
}
