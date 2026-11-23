package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.KuwaitBusData
import com.example.ui.KptcUiState
import com.example.ui.components.LanguageSwitchPill
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.theme.DesertGold
import com.example.ui.theme.KptcRoyalBlue
import com.example.ui.theme.KptcRoyalBlueDark
import com.example.ui.theme.KuwaitEmerald
import com.example.ui.theme.MonoFontFamily

@Composable
fun CompanyInfoScreen(
    uiState: KptcUiState,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val context = LocalContext.current

    val copyToClipboard: (String, String) -> Unit = { label, text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, AppStrings.copiedFeedback(label, lang), Toast.LENGTH_SHORT).show()
    }

    val dialNumber: (String) -> Unit = { number ->
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, AppStrings.noDialerFeedback(lang), Toast.LENGTH_SHORT).show()
        }
    }

    val sendEmail: (String) -> Unit = { email ->
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, AppStrings.noEmailAppFeedback(lang), Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.companyDetailsTitle(lang),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.companyDetailsSubtitle(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageSwitchPill(
                        language = lang,
                        onToggle = onToggleLanguage
                    )
                }
            }

            // Company Brand Overview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("company_overview_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_kptc_official_logo),
                                contentDescription = "KPTC Kuwait Public Transport Company",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(92.dp)
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(KptcRoyalBlue, KptcRoyalBlueDark)
                                    )
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = AppStrings.companyFullName(lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = AppStrings.companyOverview(lang),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            // Contact Us Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("company_contact_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = AppStrings.contactUsTitle(lang),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Highlighted Hot Line Banner
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = KuwaitEmerald.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { dialNumber(AppStrings.KPTC_HOTLINE) }
                                .testTag("contact_hotline_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(KuwaitEmerald),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = AppStrings.hotLineLabel(lang),
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = AppStrings.hotLineLabel(lang),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = KuwaitEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = AppStrings.KPTC_HOTLINE,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontFamily = MonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    FilledTonalButton(
                                        onClick = { dialNumber(AppStrings.KPTC_HOTLINE) },
                                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(AppStrings.callActionLabel(lang))
                                    }
                                    IconButton(
                                        onClick = {
                                            copyToClipboard(
                                                AppStrings.hotLineLabel(lang),
                                                AppStrings.KPTC_HOTLINE
                                            )
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = AppStrings.copyActionDesc(AppStrings.hotLineLabel(lang), lang),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // P.O.Box Address
                        ContactDetailRow(
                            icon = Icons.Default.LocationOn,
                            iconTint = MaterialTheme.colorScheme.primary,
                            label = AppStrings.poBoxLabel(lang),
                            value = AppStrings.poBoxDisplay(lang),
                            language = lang,
                            useMono = false,
                            onRowClick = {
                                copyToClipboard(
                                    AppStrings.poBoxLabel(lang),
                                    AppStrings.KPTC_PO_BOX_EN
                                )
                            },
                            onCopyClick = {
                                copyToClipboard(
                                    AppStrings.poBoxLabel(lang),
                                    AppStrings.KPTC_PO_BOX_EN
                                )
                            },
                            modifier = Modifier.testTag("contact_address_row")
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Telephone
                        ContactDetailRow(
                            icon = Icons.Default.Call,
                            iconTint = MaterialTheme.colorScheme.primary,
                            label = AppStrings.telLabel(lang),
                            value = AppStrings.KPTC_TEL,
                            language = lang,
                            useMono = true,
                            actionLabel = AppStrings.callActionLabel(lang),
                            onRowClick = { dialNumber(AppStrings.KPTC_TEL) },
                            onCopyClick = {
                                copyToClipboard(AppStrings.telLabel(lang), AppStrings.KPTC_TEL)
                            },
                            modifier = Modifier.testTag("contact_tel_button")
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Fax
                        ContactDetailRow(
                            icon = Icons.Default.Print,
                            iconTint = DesertGold,
                            label = AppStrings.faxLabel(lang),
                            value = AppStrings.KPTC_FAX,
                            language = lang,
                            useMono = true,
                            onRowClick = {
                                copyToClipboard(AppStrings.faxLabel(lang), AppStrings.KPTC_FAX)
                            },
                            onCopyClick = {
                                copyToClipboard(AppStrings.faxLabel(lang), AppStrings.KPTC_FAX)
                            },
                            modifier = Modifier.testTag("contact_fax_row")
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Email
                        ContactDetailRow(
                            icon = Icons.Default.Email,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            label = AppStrings.emailLabel(lang),
                            value = AppStrings.KPTC_EMAIL,
                            language = lang,
                            useMono = false,
                            actionLabel = AppStrings.emailActionLabel(lang),
                            onRowClick = { sendEmail(AppStrings.KPTC_EMAIL) },
                            onCopyClick = {
                                copyToClipboard(AppStrings.emailLabel(lang), AppStrings.KPTC_EMAIL)
                            },
                            modifier = Modifier.testTag("contact_email_button")
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Customer Support Hours
                        ServiceHighlightRow(
                            icon = Icons.Default.Schedule,
                            iconTint = MaterialTheme.colorScheme.primary,
                            label = AppStrings.headOfficeHoursLabel(lang),
                            value = AppStrings.headOfficeHoursValue(lang)
                        )
                    }
                }
            }

            // Service Highlights Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("company_service_highlights_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = AppStrings.serviceHighlightsTitle(lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        ServiceHighlightRow(
                            icon = Icons.Outlined.Payments,
                            iconTint = KuwaitEmerald,
                            label = AppStrings.standardFareTitle(lang),
                            value = AppStrings.fareAmount(250, lang)
                        )
                        ServiceHighlightRow(
                            icon = Icons.Default.Schedule,
                            iconTint = MaterialTheme.colorScheme.primary,
                            label = AppStrings.operatingHoursTitle(lang),
                            value = AppStrings.operatingHoursValue(lang)
                        )
                        ServiceHighlightRow(
                            icon = Icons.Default.DirectionsBus,
                            iconTint = DesertGold,
                            label = AppStrings.networkCoverageTitle(lang),
                            value = AppStrings.networkCoverageValue(
                                routeCount = uiState.allRoutes.size,
                                stopCount = KuwaitBusData.allStops.size,
                                lang = lang
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDetailRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    language: AppLanguage,
    useMono: Boolean,
    actionLabel: String? = null,
    onRowClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onRowClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = if (useMono) MonoFontFamily else MaterialTheme.typography.bodyLarge.fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (actionLabel != null) {
                FilledTonalButton(
                    onClick = onRowClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(actionLabel)
                }
            }
            IconButton(
                onClick = onCopyClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = AppStrings.copyActionDesc(label, language),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceHighlightRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
