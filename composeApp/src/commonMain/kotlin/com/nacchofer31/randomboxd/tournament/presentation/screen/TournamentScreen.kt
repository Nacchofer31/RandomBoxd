package com.nacchofer31.randomboxd.tournament.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.tournament.domain.model.Tournament
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentRoundName
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.tournament.presentation.components.ChampionView
import com.nacchofer31.randomboxd.tournament.presentation.components.MatchCard
import com.nacchofer31.randomboxd.tournament.presentation.components.RoundSummaryView
import com.nacchofer31.randomboxd.tournament.presentation.components.TournamentHeader
import com.nacchofer31.randomboxd.tournament.presentation.components.TournamentLoadingView
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentAction
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentPhase
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentState
import com.nacchofer31.randomboxd.tournament.presentation.viewmodel.TournamentViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.tournament_loading_error
import randomboxd.composeapp.generated.resources.tournament_match_progress
import randomboxd.composeapp.generated.resources.tournament_retry
import randomboxd.composeapp.generated.resources.tournament_round_16
import randomboxd.composeapp.generated.resources.tournament_round_32
import randomboxd.composeapp.generated.resources.tournament_round_final
import randomboxd.composeapp.generated.resources.tournament_round_quarterfinals
import randomboxd.composeapp.generated.resources.tournament_round_semifinals

@Composable
fun TournamentScreenRoot(
    size: TournamentSize,
    films: List<Film>,
    userNames: Set<String>,
    searchMode: FilmSearchMode,
    genres: Set<FilmGenre>,
    viewModel: TournamentViewModel = koinViewModel(parameters = { parametersOf(size, films, userNames, searchMode, genres) }),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TournamentScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
fun TournamentScreen(
    state: TournamentState,
    onAction: (TournamentAction) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = { TournamentHeader(onBackClick) },
        containerColor = RandomBoxdColors.BackgroundDarkColor,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(RandomBoxdColors.BackgroundDarkColor)
                    .padding(paddingValues),
        ) {
            when (state.phase) {
                TournamentPhase.LOADING -> {
                    TournamentLoadingView(
                        progress = state.loadingProgress,
                        total = state.loadingTotal,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                TournamentPhase.MATCH -> {
                    MatchPhaseView(
                        tournament = state.tournament!!,
                        onAction = onAction,
                    )
                }

                TournamentPhase.ROUND_SUMMARY -> {
                    val tournament = state.tournament!!
                    RoundSummaryView(
                        winners = tournament.currentRoundMatches.mapNotNull { it.winner },
                        onContinue = { onAction(TournamentAction.OnNextRoundClicked) },
                    )
                }

                TournamentPhase.CHAMPION -> {
                    val tournament = state.tournament!!
                    ChampionView(
                        champion = tournament.champion!!,
                        filmCount = tournament.size.filmCount,
                        roundCount = tournament.size.roundCount,
                        matchCount = tournament.size.totalMatchCount,
                        onNewTournament = { onAction(TournamentAction.OnNewTournamentClicked) },
                        onBackClick = onBackClick,
                    )
                }

                TournamentPhase.ERROR -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.tournament_loading_error),
                            color = RandomBoxdColors.BackgroundLightColor,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 30.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier =
                                Modifier
                                    .height(48.dp)
                                    .testTag("test-retry-button"),
                            onClick = { onAction(TournamentAction.OnRetryClicked) },
                            color = RandomBoxdColors.GreenAccent,
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(Res.string.tournament_retry),
                                    color = RandomBoxdColors.BackgroundDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchPhaseView(
    tournament: Tournament,
    onAction: (TournamentAction) -> Unit,
) {
    val matches = tournament.currentRoundMatches
    val matchIndex = matches.indexOfFirst { !it.isDecided }.coerceAtLeast(0)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            matches.forEachIndexed { index, match ->
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                match.isDecided -> RandomBoxdColors.GreenAccent
                                index == matchIndex -> RandomBoxdColors.TournamentOrange
                                else -> RandomBoxdColors.TextFieldBackgroundColor
                            },
                        ),
                )
            }
        }
        Text(
            text =
                stringResource(
                    Res.string.tournament_match_progress,
                    roundNameLabel(tournament.currentRoundName),
                    matchIndex + 1,
                    matches.size,
                ),
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("test-match-progress"),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = roundNameLabel(tournament.currentRoundName),
            color = RandomBoxdColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(16.dp))
        MatchCard(
            match = matches[matchIndex],
            onFilmSelected = { film ->
                onAction(TournamentAction.OnFilmSelected(matchIndex, film))
            },
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
internal fun roundNameLabel(name: TournamentRoundName): String =
    when (name) {
        TournamentRoundName.ROUND_OF_32 -> stringResource(Res.string.tournament_round_32)
        TournamentRoundName.ROUND_OF_16 -> stringResource(Res.string.tournament_round_16)
        TournamentRoundName.QUARTERFINALS -> stringResource(Res.string.tournament_round_quarterfinals)
        TournamentRoundName.SEMIFINALS -> stringResource(Res.string.tournament_round_semifinals)
        TournamentRoundName.FINAL -> stringResource(Res.string.tournament_round_final)
    }
