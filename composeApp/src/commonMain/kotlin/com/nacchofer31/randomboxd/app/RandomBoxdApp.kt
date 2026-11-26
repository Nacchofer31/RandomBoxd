package com.nacchofer31.randomboxd.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nacchofer31.randomboxd.core.data.OnboardingPreferences
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdTypography
import com.nacchofer31.randomboxd.history.presentation.HistoryScreenRoot
import com.nacchofer31.randomboxd.onboarding.presentation.OnboardingScreen
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreenRoot
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import com.nacchofer31.randomboxd.tournament.presentation.TournamentFilmProvider
import com.nacchofer31.randomboxd.tournament.presentation.screen.TournamentScreenRoot
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun RandomBoxdApp() {
    MaterialTheme(
        typography = RandomBoxdTypography(),
    ) {
        val navController = rememberNavController()
        val localUriHandler = LocalUriHandler.current
        val onboardingPreferences = remember { getKoin().get<OnboardingPreferences>() }
        val tournamentFilmProvider = remember { getKoin().get<TournamentFilmProvider>() }
        val startDestination: RandomBoxdRoute =
            remember {
                if (onboardingPreferences.isFirstRun()) {
                    RandomBoxdRoute.Onboarding
                } else {
                    RandomBoxdRoute.Home
                }
            }

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable<RandomBoxdRoute.Onboarding> {
                OnboardingScreen(
                    onFinish = {
                        onboardingPreferences.setOnboardingCompleted()
                        navController.navigate(RandomBoxdRoute.Home) {
                            popUpTo(RandomBoxdRoute.Onboarding) { inclusive = true }
                        }
                    },
                )
            }
            navigation<RandomBoxdRoute.Home>(
                startDestination = RandomBoxdRoute.RandomFilm,
            ) {
                composable<RandomBoxdRoute.RandomFilm> {
                    val viewModel = koinViewModel<RandomFilmViewModel>()
                    // Consume the champion handed back by the tournament: it lives in a singleton, so the
                    // LaunchedEffect can restart (recomposition, rotation) without applying it twice.
                    LaunchedEffect(tournamentFilmProvider) {
                        tournamentFilmProvider.champion.collect {
                            val champion = tournamentFilmProvider.consumeChampion() ?: return@collect
                            viewModel.onAction(RandomFilmAction.OnTournamentChampion(champion))
                        }
                    }
                    RandomFilmScreenRoot(
                        viewModel = viewModel,
                        onFilmClicked = { film ->
                            localUriHandler.openUri(film.slug)
                        },
                        onInfoClick = {
                            navController.navigate(RandomBoxdRoute.Onboarding)
                        },
                        onHistoryClick = {
                            navController.navigate(RandomBoxdRoute.History)
                        },
                        onTournamentStarted = { size, films, userNames, searchMode, genres ->
                            tournamentFilmProvider.pendingFilms = films
                            tournamentFilmProvider.pendingUserNames = userNames
                            tournamentFilmProvider.pendingSearchMode = searchMode
                            tournamentFilmProvider.pendingGenres = genres
                            navController.navigate(RandomBoxdRoute.Tournament(size))
                        },
                    )
                }
                composable<RandomBoxdRoute.History> {
                    HistoryScreenRoot(
                        onBackClick = { navController.popBackStack() },
                        onPosterClick = { url -> localUriHandler.openUri(url) },
                    )
                }
                composable<RandomBoxdRoute.Tournament> { entry ->
                    val route = entry.toRoute<RandomBoxdRoute.Tournament>()
                    val films = tournamentFilmProvider.pendingFilms
                    val userNames = tournamentFilmProvider.pendingUserNames
                    val searchMode = tournamentFilmProvider.pendingSearchMode
                    val genres = tournamentFilmProvider.pendingGenres
                    tournamentFilmProvider.pendingFilms = emptyList()
                    tournamentFilmProvider.pendingUserNames = emptySet()
                    tournamentFilmProvider.pendingSearchMode = FilmSearchMode.INTERSECTION
                    tournamentFilmProvider.pendingGenres = emptySet()
                    // A tap and the system back can both land during the same transition: the exit may only be
                    // started once (a second popBackStack would remove RandomFilm too).
                    var exitRequested by remember(entry) { mutableStateOf(false) }
                    val leaveTournament: (Film?) -> Unit = { champion ->
                        if (!exitRequested) {
                            exitRequested = true
                            // Only publish when the exit is actually happening, so a stray call leaves no champion behind.
                            if (champion != null) tournamentFilmProvider.publishChampion(champion)
                            navController.popBackStack()
                        }
                    }
                    TournamentScreenRoot(
                        size = route.size,
                        films = films,
                        userNames = userNames,
                        searchMode = searchMode,
                        genres = genres,
                        onBackClick = { leaveTournament(null) },
                        onChampionBack = { champion -> leaveTournament(champion) },
                    )
                }
            }
        }
    }
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(navGraphRoute)
        }
    return koinViewModel(
        viewModelStoreOwner = parentEntry,
    )
}
