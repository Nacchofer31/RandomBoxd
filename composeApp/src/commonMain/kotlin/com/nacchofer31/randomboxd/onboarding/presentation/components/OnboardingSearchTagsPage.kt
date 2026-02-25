package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_search_feature1
import randomboxd.composeapp.generated.resources.onboarding_search_feature2
import randomboxd.composeapp.generated.resources.onboarding_search_subtitle
import randomboxd.composeapp.generated.resources.onboarding_search_title

@Composable
internal fun OnboardingSearchTagsPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // Search mockup card
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(RandomBoxdColors.CardBackground)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Search bar
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RandomBoxdColors.TextFieldBackgroundColor)
                        .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.TextMuted,
                )
                Text(
                    text = "lhaneke/films-directed-by-women",
                    color = Color.White,
                    fontSize = 13.sp,
                )
            }

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Orange tag
                Row(
                    modifier =
                        Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(RandomBoxdColors.OrangeAccent)
                            .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "nacchofer31",
                        color = RandomBoxdColors.BackgroundDarkColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = RandomBoxdColors.BackgroundDarkColor,
                    )
                }
                // Dark tag
                Row(
                    modifier =
                        Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "shoegazer94",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White,
                    )
                }
            }
        }

        // Title section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.onboarding_search_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.onboarding_search_subtitle),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
        }

        // Feature rows
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OnboardingFeatureRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = RandomBoxdColors.GreenAccent,
                    )
                },
                text = stringResource(Res.string.onboarding_search_feature1),
                iconBackgroundColor = TagGreenBg,
            )
            OnboardingFeatureRow(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = RandomBoxdColors.OrangeAccent,
                    )
                },
                text = stringResource(Res.string.onboarding_search_feature2),
                iconBackgroundColor = TagOrangeBg,
            )
        }
    }
}
