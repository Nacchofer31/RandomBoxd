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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_genre_feature1
import randomboxd.composeapp.generated.resources.onboarding_genre_feature2
import randomboxd.composeapp.generated.resources.onboarding_genre_feature3
import randomboxd.composeapp.generated.resources.onboarding_genre_new_badge
import randomboxd.composeapp.generated.resources.onboarding_genre_subtitle
import randomboxd.composeapp.generated.resources.onboarding_genre_title

@Composable
internal fun OnboardingGenreFiltersPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Genre chips mockup
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RandomBoxdColors.CardBackground)
                    .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GenreChipRow(
                    chips =
                        listOf(
                            Triple("Action", Icons.Filled.LocalFireDepartment, true),
                            Triple("Comedy", Icons.Filled.EmojiEmotions, false),
                            Triple("Drama", Icons.Filled.Theaters, false),
                        ),
                )
                GenreChipRow(
                    chips =
                        listOf(
                            Triple("Horror", Icons.Filled.DarkMode, true),
                            Triple("Thriller", Icons.Filled.Warning, false),
                            Triple("Romance", Icons.Filled.Favorite, false),
                        ),
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
                        text = stringResource(Res.string.onboarding_genre_new_badge),
                        color = RandomBoxdColors.BackgroundDarkColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    )
                }
                Text(
                    text = stringResource(Res.string.onboarding_genre_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = stringResource(Res.string.onboarding_genre_subtitle),
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
                    imageVector = Icons.Filled.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_genre_feature1),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_genre_feature2),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Layers,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_genre_feature3),
            iconBackgroundColor = TagGreenBg,
        )
    }
}

@Composable
private fun GenreChipRow(chips: List<Triple<String, ImageVector, Boolean>>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        chips.forEach { (label, icon, selected) ->
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) RandomBoxdColors.GreenAccent else BgElevated)
                        .padding(vertical = 7.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = if (selected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.BackgroundLightColor,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = label,
                    color = if (selected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.BackgroundLightColor,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                )
            }
        }
    }
}
