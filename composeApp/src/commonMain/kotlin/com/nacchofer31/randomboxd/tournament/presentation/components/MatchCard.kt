package com.nacchofer31.randomboxd.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.tournament.domain.model.Match
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.tournament_versus

@Composable
internal fun MatchCard(
    match: Match,
    onFilmSelected: (Film) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TournamentPosterSide(
                film = match.film1,
                gradient =
                    listOf(
                        RandomBoxdColors.TournamentOrange.copy(alpha = 0.45f),
                        RandomBoxdColors.BackgroundDarkColor,
                    ),
                onClick = { onFilmSelected(match.film1) },
                isWinner = match.winner?.slug == match.film1.slug,
                testTag = "test-match-film1",
            )
            TournamentPosterSide(
                film = match.film2,
                gradient =
                    listOf(
                        RandomBoxdColors.BlueAccent.copy(alpha = 0.45f),
                        RandomBoxdColors.BackgroundDarkColor,
                    ),
                onClick = { onFilmSelected(match.film2) },
                isWinner = match.winner?.slug == match.film2.slug,
                testTag = "test-match-film2",
            )
        }
        Surface(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(52.dp)
                    .testTag("test-match-vs-badge"),
            shape = CircleShape,
            color = RandomBoxdColors.TournamentOrange,
            shadowElevation = 12.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.tournament_versus),
                    color = RandomBoxdColors.BackgroundDarkColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun RowScope.TournamentPosterSide(
    film: Film,
    gradient: List<Color>,
    onClick: () -> Unit,
    isWinner: Boolean,
    testTag: String,
) {
    Column(modifier = Modifier.weight(1f)) {
        Box(
            modifier =
                Modifier
                    .aspectRatio(2f / 3f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(gradient))
                    .clickable(onClick = onClick)
                    .testTag(testTag)
                    .then(
                        if (isWinner) {
                            Modifier.border(
                                2.dp,
                                RandomBoxdColors.GreenAccent,
                                RoundedCornerShape(16.dp),
                            )
                        } else {
                            Modifier
                        },
                    ),
        ) {
            AsyncImage(
                model = film.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = film.name,
            color = RandomBoxdColors.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = film.releaseYear?.toString() ?: "—",
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
