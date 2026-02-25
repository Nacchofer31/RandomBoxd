package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_feature_deeplink
import randomboxd.composeapp.generated.resources.onboarding_feature_multi_user
import randomboxd.composeapp.generated.resources.onboarding_feature_random
import randomboxd.composeapp.generated.resources.onboarding_welcome_description
import randomboxd.composeapp.generated.resources.onboarding_welcome_subtitle
import randomboxd.composeapp.generated.resources.onboarding_welcome_title
import randomboxd.composeapp.generated.resources.random_boxd_logo

@Composable
internal fun OnboardingWelcomePage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(140.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0x40000000),
                    ).clip(RoundedCornerShape(24.dp))
                    .background(RandomBoxdColors.CardBackground),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.random_boxd_logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.onboarding_welcome_title),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.onboarding_welcome_subtitle),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.onboarding_welcome_description),
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(300.dp),
        )

        Spacer(Modifier.height(8.dp))

        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_feature_random),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = RandomBoxdColors.OrangeAccent,
                )
            },
            text = stringResource(Res.string.onboarding_feature_multi_user),
            iconBackgroundColor = TagOrangeBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_feature_deeplink),
            iconBackgroundColor = TagGreenBg,
        )
    }
}
