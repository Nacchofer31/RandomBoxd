package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_new_badge
import randomboxd.composeapp.generated.resources.onboarding_tournament_feature1
import randomboxd.composeapp.generated.resources.onboarding_tournament_feature2
import randomboxd.composeapp.generated.resources.onboarding_tournament_feature3
import randomboxd.composeapp.generated.resources.onboarding_tournament_subtitle
import randomboxd.composeapp.generated.resources.onboarding_tournament_title
import randomboxd.composeapp.generated.resources.tournament_champion_title
import randomboxd.composeapp.generated.resources.tournament_round_semifinals
import randomboxd.composeapp.generated.resources.tournament_versus

private data class MockBracketFilm(
    val title: String,
    val posterGradient: Pair<Color, Color>,
)

private val mockSemifinalOne =
    listOf(
        MockBracketFilm("Inception", Color(0xFF3B82F6) to Color(0xFF1E3A8A)),
        MockBracketFilm("Whiplash", Color(0xFFF59E0B) to Color(0xFFB45309)),
    )

private val mockSemifinalTwo =
    listOf(
        MockBracketFilm("Her", Color(0xFFEC4899) to Color(0xFF831843)),
        MockBracketFilm("Arrival", Color(0xFF14B8A6) to Color(0xFF134E4A)),
    )

private val mockChampion = mockSemifinalOne.first()

@Composable
internal fun OnboardingTournamentPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        TournamentBracketMockup()

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
                    text = stringResource(Res.string.onboarding_tournament_title),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = stringResource(Res.string.onboarding_tournament_subtitle),
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
                    imageVector = Icons.Filled.GridView,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.TournamentOrange,
                )
            },
            text = stringResource(Res.string.onboarding_tournament_feature1),
            iconBackgroundColor = TagOrangeBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_tournament_feature2),
            iconBackgroundColor = TagGreenBg,
        )
        OnboardingFeatureRow(
            icon = {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = RandomBoxdColors.GreenAccent,
                )
            },
            text = stringResource(Res.string.onboarding_tournament_feature3),
            iconBackgroundColor = TagGreenBg,
        )
    }
}

@Composable
private fun TournamentBracketMockup() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(RandomBoxdColors.CardBackground)
                .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(Res.string.tournament_round_semifinals),
                color = RandomBoxdColors.TournamentOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )
            BracketMatchRow(
                left = mockSemifinalOne[0],
                right = mockSemifinalOne[1],
                winnerIsLeft = true,
            )
            BracketSpine()
            BracketMatchRow(
                left = mockSemifinalTwo[0],
                right = mockSemifinalTwo[1],
                winnerIsLeft = false,
            )
            BracketSpine()
            BracketChampionRow(champion = mockChampion)
        }
    }
}

@Composable
private fun BracketMatchRow(
    left: MockBracketFilm,
    right: MockBracketFilm,
    winnerIsLeft: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BracketPoster(
            film = left,
            isWinner = winnerIsLeft,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(Res.string.tournament_versus),
            color = RandomBoxdColors.TournamentOrange,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
        )
        BracketPoster(
            film = right,
            isWinner = !winnerIsLeft,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun BracketPoster(
    film: MockBracketFilm,
    isWinner: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier =
            modifier
                .height(42.dp)
                .alpha(if (isWinner) 1f else 0.45f)
                .clip(shape)
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(film.posterGradient.first, film.posterGradient.second),
                        ),
                ).then(
                    if (isWinner) {
                        Modifier.border(width = 1.5.dp, color = RandomBoxdColors.GreenAccent, shape = shape)
                    } else {
                        Modifier
                    },
                ).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = film.title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (isWinner) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = RandomBoxdColors.GreenAccent,
            )
        }
    }
}

@Composable
private fun ColumnScope.BracketSpine() {
    Box(
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally)
                .width(1.dp)
                .height(10.dp)
                .background(RandomBoxdColors.TournamentOrange.copy(alpha = 0.4f)),
    )
}

@Composable
private fun BracketChampionRow(champion: MockBracketFilm) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(TagGreenBg)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = RandomBoxdColors.TournamentOrange,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(Res.string.tournament_champion_title),
                color = RandomBoxdColors.TournamentOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )
            Text(
                text = champion.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = RandomBoxdColors.GreenAccent,
        )
    }
}
