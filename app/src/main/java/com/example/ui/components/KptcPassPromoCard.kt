package com.example.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
private val PassAmberGold = Color(0xFFFFC107)

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

                // Interactive animated indicator dots to switch between both ads
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
        Dialog(onDismissRequest = { dialogSlideIndex = null }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = modalSlide.startColor),
                border = BorderStroke(1.dp, PassAmberGold.copy(alpha = 0.4f))
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
                            text = modalSlide.headline,
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
                            onClick = { dialogSlideIndex = null },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .testTag("kptc_pass_dialog_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = AppStrings.passAdCloseAction(language),
                                tint = Color.White
                            )
                        }
                    }

                    Image(
                        painter = painterResource(id = modalSlide.imageRes),
                        contentDescription = modalSlide.contentDesc,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Button(
                        onClick = dialHotline,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PassAmberGold,
                            contentColor = modalSlide.startColor
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
                            text = modalSlide.ctaText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
