package com.nacchofer31.randomboxd.tournament.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.tournament_back
import randomboxd.composeapp.generated.resources.tournament_champion_stats
import randomboxd.composeapp.generated.resources.tournament_champion_title
import randomboxd.composeapp.generated.resources.tournament_new

@Composable
internal fun ChampionView(
    champion: Film,
    filmCount: Int,
    roundCount: Int,
    matchCount: Int,
    onNewTournament: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("test-champion-view"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = RandomBoxdColors.TournamentOrange,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.tournament_champion_title),
            color = RandomBoxdColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
        )
        Spacer(Modifier.height(16.dp))
        Box {
            AsyncImage(
                model = champion.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(16.dp))
                        .dropShadow(
                            shape = RoundedCornerShape(16.dp),
                            shadow =
                                Shadow(
                                    radius = 20.dp,
                                    color = RandomBoxdColors.Black.copy(alpha = 0.7f),
                                    spread = 6.dp,
                                ),
                        ),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = champion.name,
            color = RandomBoxdColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = champion.releaseYear?.toString() ?: "",
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.tournament_champion_stats, filmCount, roundCount, matchCount),
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("test-new-tournament"),
                onClick = onNewTournament,
                color = RandomBoxdColors.GreenAccent,
                shape = RoundedCornerShape(14.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = RandomBoxdColors.BackgroundDarkColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.tournament_new),
                            color = RandomBoxdColors.BackgroundDarkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
            Surface(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("test-champion-back"),
                onClick = onBackClick,
                color = RandomBoxdColors.CardBackground,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, RandomBoxdColors.TextFieldBackgroundColor),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.tournament_back),
                        color = RandomBoxdColors.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
