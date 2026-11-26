package com.example.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import kotlinx.coroutines.delay

private val PassDeepIndigo = Color(0xFF151946)
private val PassRoyalViolet = Color(0xFF28205C)
private val AdSkyNavy = Color(0xFF132D5E)
private val AdBlueAccent = Color(0xFF1E4C8F)
private val FleetCyanBlue = Color(0xFF06568E)
private val FleetDeepBlue = Color(0xFF0B3A69)
private val PassAmberGold = Color(0xFFFFC107)
private val FlashBrightYellow = Color(0xFFFFE01B)

private data class PromoAdSlide(
    val id: String,
    @DrawableRes val imageRes: Int,
    val badgeIcon: ImageVector,
    val badgeText: String,
    val headline: String,
    val subheadline: String,
    val featureLine: String,
    val ctaText: String,
    val contentDesc: String,
    val startColor: Color,
    val endColor: Color
)

@Composable
fun KptcPassPromoCard(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSlideIndex by remember { mutableIntStateOf(0) }
    var dialogSlideIndex by remember { mutableStateOf<Int?>(null) }

    val slides = listOf(
        PromoAdSlide(
            id = "kptc_pass",
            imageRes = R.drawable.img_kptc_opening_poster,
            badgeIcon = Icons.Default.ConfirmationNumber,
            badgeText = AppStrings.passAdBadge(language),
            headline = AppStrings.passAdHeadline(language),
            subheadline = AppStrings.passAdPlansSummary(language),
            featureLine = AppStrings.passAdFeaturesLine(language),
            ctaText = AppStrings.passAdSubscribeCta(language),
            contentDesc = AppStrings.passAdPosterDesc(language),
            startColor = PassDeepIndigo,
            endColor = PassRoyalViolet
        ),
        PromoAdSlide(
            id = "advertise_now",
            imageRes = R.drawable.img_kptc_advertise_now_poster,
            badgeIcon = Icons.Default.Campaign,
            badgeText = AppStrings.busAdBadge(language),
            headline = AppStrings.busAdHeadline(language),
            subheadline = AppStrings.busAdSubheadline(language),
            featureLine = AppStrings.busAdFeaturesLine(language),
            ctaText = AppStrings.busAdCta(language),
            contentDesc = AppStrings.busAdPosterDesc(language),
            startColor = AdSkyNavy,
            endColor = AdBlueAccent
        ),
        PromoAdSlide(
            id = "always_ready",
            imageRes = R.drawable.img_kptc_always_ready_ad,
            badgeIcon = Icons.Default.DirectionsBus,
            badgeText = AppStrings.alwaysReadyAdBadge(language),
            headline = AppStrings.alwaysReadyAdHeadline(language),
            subheadline = AppStrings.alwaysReadyAdSubheadline(language),
            featureLine = AppStrings.alwaysReadyAdFeaturesLine(language),
            ctaText = AppStrings.alwaysReadyAdCta(language),
            contentDesc = AppStrings.alwaysReadyAdPosterDesc(language),
            startColor = FleetDeepBlue,
            endColor = FleetCyanBlue
        ),
        PromoAdSlide(
            id = "route_21_quiz",
            imageRes = R.drawable.img_kptc_route21_quiz,
            badgeIcon = Icons.Default.DirectionsBus,
            badgeText = AppStrings.route21AdBadge(language),
            headline = AppStrings.route21AdHeadline(language),
            subheadline = AppStrings.route21AdSubheadline(language),
            featureLine = AppStrings.route21AdFeaturesLine(language),
            ctaText = AppStrings.alwaysReadyAdCta(language),
            contentDesc = AppStrings.route21AdPosterDesc(language),
            startColor = AdSkyNavy,
            endColor = FleetCyanBlue
        )
    )

    LaunchedEffect(activeSlideIndex, dialogSlideIndex) {
        if (dialogSlideIndex == null) {
            delay(4_800L)
            activeSlideIndex = (activeSlideIndex + 1) % slides.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "kptc_pass_ad")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    val floatOffsetY by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "poster_float"
    )

    val badgePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_pulse"
    )

    val dialHotline: () -> Unit = {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AppStrings.KPTC_HOTLINE}"))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, AppStrings.noDialerFeedback(language), Toast.LENGTH_SHORT).show()
        }
    }

    val currentSlide = slides[activeSlideIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { dialogSlideIndex = activeSlideIndex }
            .testTag("kptc_pass_ad_card"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, PassAmberGold.copy(alpha = 0.38f)),
        colors = CardDefaults.cardColors(containerColor = currentSlide.startColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(currentSlide.startColor, currentSlide.endColor)
                    )
                )
                .drawWithContent {
                    drawContent()
                    val bandWidth = size.width * 0.28f
                    val startX = (size.width + bandWidth) * shimmerProgress - bandWidth
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            start = Offset(startX, 0f),
                            end = Offset(startX + bandWidth, size.height)
                        )
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    targetState = activeSlideIndex,
                    transitionSpec = {
                        (slideInHorizontally(animationSpec = tween(360)) { fullWidth -> fullWidth / 4 } +
                            fadeIn(animationSpec = tween(360)))
                            .togetherWith(
                                slideOutHorizontally(animationSpec = tween(260)) { fullWidth -> -fullWidth / 4 } +
                                    fadeOut(animationSpec = tween(260))
                            )
                    },
                    label = "promo_ad_slide"
                ) { slideIndex ->
                    val slide = slides[slideIndex]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Medium side poster image with subtle floating animation
                        Surface(
                            modifier = Modifier
                                .width(102.dp)
                                .height(132.dp)
                                .graphicsLayer { translationY = floatOffsetY }
                                .testTag("kptc_pass_ad_side_image_${slide.id}"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, PassAmberGold.copy(alpha = 0.55f)),
                            shadowElevation = 4.dp,
                            color = slide.startColor
                        ) {
                            Image(
                                painter = painterResource(id = slide.imageRes),
                                contentDescription = slide.contentDesc,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Promotional details + animated badge + Subscribe / Book Ad button
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Surface(
                                color = PassAmberGold.copy(alpha = badgePulseAlpha),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = slide.badgeIcon,
                                        contentDescription = null,
                                        tint = slide.startColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = slide.badgeText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = slide.startColor
                                    )
                                }
                            }

                            Text(
                                text = slide.headline,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = slide.subheadline,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PassAmberGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PassAmberGold.copy(alpha = 0.9f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = slide.featureLine,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Button(
                                onClick = dialHotline,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PassAmberGold,
                                    contentColor = slide.startColor
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("kptc_pass_ad_subscribe_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slide.ctaText,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Interactive animated indicator dots to switch between all ads
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.forEachIndexed { idx, slide ->
                        val isSelected = idx == activeSlideIndex
                        val pillWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = tween(250),
                            label = "ad_dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { activeSlideIndex = idx }
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                .testTag("kptc_ad_dot_${slide.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(pillWidth)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (isSelected) PassAmberGold else Color.White.copy(alpha = 0.35f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    dialogSlideIndex?.let { index ->
        val modalSlide = slides[index]
        PromoAdPosterDialog(
            title = modalSlide.headline,
            imageRes = modalSlide.imageRes,
            contentDesc = modalSlide.contentDesc,
            ctaText = modalSlide.ctaText,
            containerColor = modalSlide.startColor,
            closeDesc = AppStrings.passAdCloseAction(language),
            onCallClick = dialHotline,
            onDismiss = { dialogSlideIndex = null }
        )
    }
}

/**
 * 10-second Side Flash Ad that slides in smoothly from the side after launch,
 * pulses with a subtle flash shimmer, shows a 10-second progress countdown bar,
 * and auto-dismisses cleanly after 10 seconds.
 */
@Composable
fun KptcSideFlashAdOverlay(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var dismissedManually by rememberSaveable { mutableStateOf(false) }
    var showPosterDialog by remember { mutableStateOf(false) }
    val countdownProgress = remember { Animatable(1f) }

    LaunchedEffect(dismissedManually) {
        if (dismissedManually) {
            visible = false
            return@LaunchedEffect
        }
        // Wait for the opening splash to fade out first
        delay(2_200L)
        visible = true
        countdownProgress.snapTo(1f)
        countdownProgress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 10_000, easing = LinearEasing)
        )
        visible = false
        dismissedManually = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "side_flash_ad")
    val flashGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash_border"
    )
    val sheenProgress by infiniteTransition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flash_sheen"
    )

    val dialHotline: () -> Unit = {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AppStrings.KPTC_HOTLINE}"))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, AppStrings.noDialerFeedback(language), Toast.LENGTH_SHORT).show()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(420)) { fullWidth -> fullWidth } +
            fadeIn(animationSpec = tween(360)),
        exit = slideOutHorizontally(animationSpec = tween(340)) { fullWidth -> fullWidth } +
            fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 316.dp)
                .clickable { showPosterDialog = true }
                .testTag("kptc_side_flash_ad"),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, FlashBrightYellow.copy(alpha = flashGlowAlpha)),
            colors = CardDefaults.cardColors(containerColor = FleetDeepBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(FleetDeepBlue, FleetCyanBlue)
                        )
                    )
                    .drawWithContent {
                        drawContent()
                        val bandWidth = size.width * 0.32f
                        val startX = (size.width + bandWidth) * sheenProgress - bandWidth
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Transparent
                                ),
                                start = Offset(startX, 0f),
                                end = Offset(startX + bandWidth, size.height)
                            )
                        )
                    }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(82.dp)
                                .testTag("kptc_side_flash_ad_image"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, FlashBrightYellow.copy(alpha = 0.8f)),
                            color = FleetDeepBlue
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_kptc_always_ready_ad),
                                contentDescription = AppStrings.alwaysReadyAdPosterDesc(language),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Surface(
                                color = FlashBrightYellow,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = AppStrings.alwaysReadyAdBadge(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FleetDeepBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = AppStrings.alwaysReadyAdHeadline(language),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = AppStrings.alwaysReadyAdFeaturesLine(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                dismissedManually = true
                                visible = false
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .testTag("kptc_side_flash_ad_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = AppStrings.passAdCloseAction(language),
                                tint = Color.White
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { countdownProgress.value },
                        color = FlashBrightYellow,
                        trackColor = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    )
                }
            }
        }
    }

    if (showPosterDialog) {
        PromoAdPosterDialog(
            title = AppStrings.alwaysReadyAdHeadline(language),
            imageRes = R.drawable.img_kptc_always_ready_ad,
            contentDesc = AppStrings.alwaysReadyAdPosterDesc(language),
            ctaText = AppStrings.alwaysReadyAdCta(language),
            containerColor = FleetDeepBlue,
            closeDesc = AppStrings.passAdCloseAction(language),
            onCallClick = dialHotline,
            onDismiss = { showPosterDialog = false }
        )
    }
}

@Composable
private fun PromoAdPosterDialog(
    title: String,
    @DrawableRes imageRes: Int,
    contentDesc: String,
    ctaText: String,
    containerColor: Color,
    closeDesc: String,
    onCallClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, PassAmberGold.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .testTag("kptc_pass_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = closeDesc,
                            tint = Color.White
                        )
                    }
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDesc,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Button(
                    onClick = onCallClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PassAmberGold,
                        contentColor = containerColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ctaText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
