package com.nacchofer31.randomboxd.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.tournament_setting_up

@Composable
internal fun TournamentLoadingView(
    progress: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(RandomBoxdColors.BackgroundDarkColor)
                .testTag("test-tournament-loading"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = RandomBoxdColors.TournamentOrange,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.tournament_setting_up),
            color = RandomBoxdColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(4) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) {
                        Box(
                            Modifier
                                .size(90.dp, 135.dp)
                                .background(
                                    RandomBoxdColors.TextFieldBackgroundColor,
                                    RoundedCornerShape(12.dp),
                                ),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) progress.toFloat() / total else 0f },
            modifier =
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("test-tournament-progress"),
            color = RandomBoxdColors.GreenAccent,
            trackColor = RandomBoxdColors.TextFieldBackgroundColor,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$progress / $total",
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 12.sp,
        )
    }
}
