package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_multiuser_feature1
import randomboxd.composeapp.generated.resources.onboarding_multiuser_feature2
import randomboxd.composeapp.generated.resources.onboarding_multiuser_feature3
import randomboxd.composeapp.generated.resources.onboarding_multiuser_subtitle
import randomboxd.composeapp.generated.resources.onboarding_multiuser_title

@Composable
internal fun OnboardingMultiUserPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Venn diagram
        Box(
            modifier = Modifier.size(width = 200.dp, height = 140.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleRadius = 50.dp.toPx()
                val leftCenter = Offset(circleRadius, size.height / 2)
                val rightCenter = Offset(size.width - circleRadius, size.height / 2)

                // Left circle (green)
                drawCircle(
                    color = Color(0x4000E054),
                    radius = circleRadius,
                    center = leftCenter,
                )
                drawCircle(
                    color = Color(0xFF00E054),
                    radius = circleRadius,
                    center = leftCenter,
                    style = Stroke(width = 2.dp.toPx()),
                )

                // Right circle (orange)
                drawCircle(
                    color = Color(0x40FF8000),
                    radius = circleRadius,
                    center = rightCenter,
                )
                drawCircle(
                    color = Color(0xFFFF8000),
                    radius = circleRadius,
                    center = rightCenter,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }

            // Intersection symbol
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(RandomBoxdColors.CardBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u2229",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Mode pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f))
            // Intersection pill
            Row(
                modifier =
                    Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(RandomBoxdColors.GreenAccent)
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "\u2229",
                    color = RandomBoxdColors.BackgroundDarkColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "INTERSECTION",
                    color = RandomBoxdColors.BackgroundDarkColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            // Union pill
            Row(
                modifier =
                    Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BgElevated)
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "\u222A",
                    color = RandomBoxdColors.BackgroundLightColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "UNION",
                    color = RandomBoxdColors.BackgroundLightColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.weight(1f))
        }

        // Title section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.onboarding_multiuser_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.onboarding_multiuser_subtitle),
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
                Text(
                    text = "\u2229",
                    color = RandomBoxdColors.GreenAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = stringResource(Res.string.onboarding_multiuser_feature1),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Text(
                    text = "\u222A",
                    color = RandomBoxdColors.OrangeAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = stringResource(Res.string.onboarding_multiuser_feature2),
            iconBackgroundColor = TagOrangeBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.PanTool,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_multiuser_feature3),
            iconBackgroundColor = TagGreenBg,
        )
    }
}
