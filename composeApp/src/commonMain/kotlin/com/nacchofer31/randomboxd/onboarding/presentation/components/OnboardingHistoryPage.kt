package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_history_feature1
import randomboxd.composeapp.generated.resources.onboarding_history_feature2
import randomboxd.composeapp.generated.resources.onboarding_history_feature3
import randomboxd.composeapp.generated.resources.onboarding_history_footer
import randomboxd.composeapp.generated.resources.onboarding_history_subtitle
import randomboxd.composeapp.generated.resources.onboarding_history_title
import randomboxd.composeapp.generated.resources.onboarding_new_badge

private data class MockHistoryItem(
    val title: String,
    val meta: String,
    val posterGradient: Pair<Color, Color>,
)

private val mockHistoryItems =
    listOf(
        MockHistoryItem("Inception", "2010 · Sci-Fi", Color(0xFF3B82F6) to Color(0xFF1E3A8A)),
        MockHistoryItem("Spirited Away", "2001 · Animation", Color(0xFFF59E0B) to Color(0xFFB45309)),
        MockHistoryItem("The Godfather", "1972 · Crime", Color(0xFF6B7280) to Color(0xFF1F2937)),
    )

@Composable
internal fun OnboardingHistoryPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RandomBoxdColors.CardBackground)
                    .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Your random picks",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RandomBoxdColors.GreenAccent)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "24",
                            color = RandomBoxdColors.BackgroundDarkColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
                mockHistoryItems.forEach { item ->
                    MockHistoryRow(item)
                }
                Text(
                    text = stringResource(Res.string.onboarding_history_footer),
                    color = RandomBoxdColors.BackgroundLightColor,
                    fontSize = 12.sp,
                )
            }
        }

        // Title section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RandomBoxdColors.GreenAccent)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.onboarding_new_badge),
                        color = RandomBoxdColors.BackgroundDarkColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    )
                }
                Text(
                    text = stringResource(Res.string.onboarding_history_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = stringResource(Res.string.onboarding_history_subtitle),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp),
            )
        }
    }

    Spacer(Modifier.height(24.dp))

    // Feature rows
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_history_feature1),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_history_feature2),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_history_feature3),
            iconBackgroundColor = TagGreenBg,
        )
    }
}

@Composable
private fun MockHistoryRow(item: MockHistoryItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 60.dp, height = 84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        item.posterGradient.first,
                                        item.posterGradient.second,
                                    ),
                            ),
                    ),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = item.meta,
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = RandomBoxdColors.GreenAccent,
        )
    }
}
