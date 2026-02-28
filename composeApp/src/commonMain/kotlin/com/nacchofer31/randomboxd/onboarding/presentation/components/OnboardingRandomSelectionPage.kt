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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_random_subtitle
import randomboxd.composeapp.generated.resources.onboarding_random_title

@Composable
internal fun OnboardingRandomSelectionPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // Mockup card
        Box(
            modifier =
                Modifier
                    .size(width = 200.dp, height = 300.dp)
                    .shadow(
                        elevation = 40.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x4000E054),
                    ).clip(RoundedCornerShape(16.dp))
                    .background(RandomBoxdColors.CardBackground),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 180.dp, height = 240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush =
                                    Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                RandomBoxdColors.OrangeAccent,
                                                RandomBoxdColors.BackgroundDarkColor,
                                            ),
                                    ),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color(0x66FFFFFF),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Your Random Pick",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "From your watchlist",
                    color = RandomBoxdColors.BackgroundLightColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Title section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.onboarding_random_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.onboarding_random_subtitle),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp),
            )
        }
    }

    Spacer(Modifier.height(32.dp))

    // Input mockup section
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RandomBoxdColors.TextFieldBackgroundColor)
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "username",
                color = RandomBoxdColors.TextMuted,
                fontSize = 15.sp,
            )
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = RandomBoxdColors.TextMuted,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(width = 120.dp, height = 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgElevated),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "SUBMIT",
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
