package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.enter_description
import randomboxd.composeapp.generated.resources.ready_to_spin
import randomboxd.composeapp.generated.resources.roulette_icon
import randomboxd.composeapp.generated.resources.tip_hold_submit
import randomboxd.composeapp.generated.resources.tip_username_format

@Composable
fun RandomFilmInfoView() {
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Illustration Container
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow =
                            Shadow(
                                radius = 16.dp,
                                color = RandomBoxdColors.GreenAccent.copy(alpha = .6f),
                                spread = 4.dp,
                            ),
                    ).clip(CircleShape)
                    .background(RandomBoxdColors.CardBackground),
            contentAlignment = Alignment.Center,
        ) {
            // Inner Circle with border
            Box(
                modifier =
                    Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = RandomBoxdColors.GreenAccent,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.roulette_icon),
                    contentDescription = "Roulette",
                    tint = RandomBoxdColors.GreenAccent,
                    modifier = Modifier.size(80.dp),
                )
            }
        }

        // Text Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.ready_to_spin),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = RandomBoxdColors.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(Res.string.enter_description),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = RandomBoxdColors.BackgroundLightColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.width(300.dp),
            )
        }

        // Tips Section
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TipCard(
                icon = "💡",
                text = stringResource(Res.string.tip_username_format),
                accentColor = RandomBoxdColors.GreenAccent,
            )
            TipCard(
                icon = "👆",
                text = stringResource(Res.string.tip_hold_submit),
                accentColor = RandomBoxdColors.OrangeAccent,
            )
        }
    }
}

@Composable
private fun TipCard(
    icon: String,
    text: String,
    accentColor: Color,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(RandomBoxdColors.CardBackground)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
            )
        }
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = RandomBoxdColors.BackgroundLightColor,
        )
    }
}
