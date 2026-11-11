package com.nacchofer31.randomboxd.tournament.presentation.viewmodel

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.utils.dispatchers.TestDispatchers
import com.nacchofer31.randomboxd.utils.fakes.FakeFilmHistoryRepository
import com.nacchofer31.randomboxd.utils.fakes.FakeRandomFilmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

private const val MAX_STATE_UPDATES = 50

private val testUserNames = setOf("user1", "user2")
private val testSearchMode = FilmSearchMode.INTERSECTION
private val testGenres = setOf(FilmGenre.ACTION, FilmGenre.COMEDY)

class TournamentViewModelTest {
    private lateinit var viewModel: TournamentViewModel
    private lateinit var testDispatchers: TestDispatchers
    private lateinit var repository: FakeRandomFilmRepository
    private lateinit var historyRepository: FakeFilmHistoryRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        testDispatchers = TestDispatchers()
        repository = FakeRandomFilmRepository()
        historyRepository = FakeFilmHistoryRepository()
        Dispatchers.setMain(testDispatchers.testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        size: TournamentSize = TournamentSize.FOUR,
        films: List<Film> = testFilms(size.filmCount),
        userNames: Set<String> = testUserNames,
        searchMode: FilmSearchMode = testSearchMode,
        genres: Set<FilmGenre> = testGenres,
    ) {
        viewModel =
            TournamentViewModel(
                size = size,
                films = films,
                repository = repository,
                dispatchers = testDispatchers,
                historyRepository = historyRepository,
                userNames = userNames,
                searchMode = searchMode,
                genres = genres,
            )
    }

    @Test
    fun `given valid films when posters load then state reaches match with complete progress`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.EIGHT)

            viewModel.state.test {
                val state = awaitPhase(TournamentPhase.MATCH)

                assertNotNull(state.tournament)
                assertEquals(TournamentSize.EIGHT.filmCount, state.loadingTotal)
                assertEquals(TournamentSize.EIGHT.filmCount, state.loadingProgress)
                assertNull(state.error)
            }
        }

    @Test
    fun `given poster extraction failure when loading then original films are used`() =
        runTest(testDispatchers.testDispatcher) {
            val films = testFilms(TournamentSize.FOUR.filmCount)
            repository.extractResultMovieResult = ResultData.Error(DataError.Remote.SERIALIZATION)
            createViewModel(films = films)

            viewModel.state.test {
                val state = awaitPhase(TournamentPhase.MATCH)
                val bracketFilms =
                    requireNotNull(state.tournament)
                        .currentRoundMatches
                        .flatMap { match -> listOf(match.film1, match.film2) }

                assertEquals(films.size, bracketFilms.size)
                assertEquals(films.toSet(), bracketFilms.toSet())
            }
        }

    @Test
    fun `given invalid film count when created then state is error without loading`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.EIGHT, films = testFilms(3))

            viewModel.state.test {
                val state = awaitPhase(TournamentPhase.ERROR)

                assertEquals(0, state.loadingTotal)
                assertEquals(0, state.loadingProgress)
                assertEquals(DataError.Remote.SERIALIZATION, state.error)
                assertNull(state.tournament)
            }
        }

    @Test
    fun `when film selected then match is decided and phase remains match`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.EIGHT)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)
                val firstMatch = requireNotNull(initialState.tournament).currentRoundMatches.first()

                viewModel.onAction(TournamentAction.OnFilmSelected(0, firstMatch.film1))
                val updatedState = awaitItem()

                assertEquals(TournamentPhase.MATCH, updatedState.phase)
                assertTrue(requireNotNull(updatedState.tournament).currentRoundMatches.first().isDecided)
                assertEquals(1, updatedState.tournament?.decidedMatchesInCurrentRound)
            }
        }

    @Test
    fun `when non final round is decided then summary advances to the next round`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.FOUR)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)
                val summaryState = decideCurrentRound(initialState)

                assertEquals(TournamentPhase.ROUND_SUMMARY, summaryState.phase)
                assertTrue(requireNotNull(summaryState.tournament).isCurrentRoundComplete)

                viewModel.onAction(TournamentAction.OnNextRoundClicked)
                val nextRoundState = awaitPhase(TournamentPhase.MATCH)

                assertEquals(1, nextRoundState.tournament?.currentRoundIndex)
                assertFalse(requireNotNull(nextRoundState.tournament).isCurrentRoundComplete)
            }
        }

    @Test
    fun `when size four tournament is completed then champion phase and winner are emitted`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.FOUR)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)
                val summaryState = decideCurrentRound(initialState)
                assertEquals(TournamentPhase.ROUND_SUMMARY, summaryState.phase)

                viewModel.onAction(TournamentAction.OnNextRoundClicked)
                val finalState = awaitPhase(TournamentPhase.MATCH)
                val expectedChampion =
                    requireNotNull(finalState.tournament).currentRoundMatches.single().film1

                val championState = decideCurrentRound(finalState)

                assertEquals(TournamentPhase.CHAMPION, championState.phase)
                assertTrue(requireNotNull(championState.tournament).isFinished)
                assertEquals(expectedChampion.slug, championState.tournament?.champion?.slug)
            }
        }

    @Test
    fun `when new tournament clicked after champion then a fresh bracket is created`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.FOUR)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)
                decideCurrentRound(initialState)
                viewModel.onAction(TournamentAction.OnNextRoundClicked)
                val finalState = awaitPhase(TournamentPhase.MATCH)
                val championState = decideCurrentRound(finalState)
                assertEquals(TournamentPhase.CHAMPION, championState.phase)

                viewModel.onAction(TournamentAction.OnNewTournamentClicked)
                val freshState = awaitPhase(TournamentPhase.MATCH)
                val freshTournament = requireNotNull(freshState.tournament)

                assertEquals(0, freshTournament.currentRoundIndex)
                assertEquals(1, freshTournament.rounds.size)
                assertTrue(freshTournament.currentRoundMatches.none { match -> match.isDecided })
                assertEquals(0, freshTournament.decidedMatchCount)
            }
        }

    @Test
    fun `when retry clicked after error then posters reload and state reaches match`() =
        runTest(testDispatchers.testDispatcher) {
            val films = testFilms(3).toMutableList()
            createViewModel(size = TournamentSize.FOUR, films = films)

            viewModel.state.test {
                awaitPhase(TournamentPhase.ERROR)
                films += testFilm(3)

                viewModel.onAction(TournamentAction.OnRetryClicked)
                val state = awaitPhase(TournamentPhase.MATCH)

                assertEquals(TournamentSize.FOUR.filmCount, state.loadingTotal)
                assertEquals(TournamentSize.FOUR.filmCount, state.loadingProgress)
                assertNotNull(state.tournament)
                assertNull(state.error)
            }
        }

    @Test
    fun `when selected film is not in match then state is unchanged`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.FOUR)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)

                viewModel.onAction(TournamentAction.OnFilmSelected(0, testFilm(99)))

                expectNoEvents()
                assertEquals(initialState, viewModel.state.value)
            }
        }

    @Test
    fun `when tournament completes then champion is saved to history`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel(size = TournamentSize.FOUR)

            viewModel.state.test {
                val initialState = awaitPhase(TournamentPhase.MATCH)
                decideCurrentRound(initialState)
                viewModel.onAction(TournamentAction.OnNextRoundClicked)
                val finalState = awaitPhase(TournamentPhase.MATCH)
                val championState = decideCurrentRound(finalState)

                assertEquals(TournamentPhase.CHAMPION, championState.phase)
                val champion = requireNotNull(championState.tournament).champion
                assertNotNull(champion)
                assertEquals(1, historyRepository.saveCount)
                assertEquals(champion.slug, historyRepository.savedFilms.single().slug)
                assertEquals(testUserNames, historyRepository.lastSavedUserNames)
                assertEquals(testSearchMode, historyRepository.lastSavedSearchMode)
                assertEquals(testGenres, historyRepository.lastSavedGenres)
            }
        }

    private suspend fun ReceiveTurbine<TournamentState>.awaitPhase(
        phase: TournamentPhase,
    ): TournamentState {
        var lastState: TournamentState? = null
        repeat(MAX_STATE_UPDATES) {
            val state = awaitItem()
            lastState = state
            if (state.phase == phase) return state
        }
        fail("Expected phase $phase within $MAX_STATE_UPDATES updates, last state was $lastState")
    }

    private suspend fun ReceiveTurbine<TournamentState>.decideCurrentRound(
        initialState: TournamentState,
    ): TournamentState {
        var latestState = initialState
        val matches = requireNotNull(initialState.tournament).currentRoundMatches
        matches.forEachIndexed { index, match ->
            viewModel.onAction(TournamentAction.OnFilmSelected(index, match.film1))
            latestState = awaitItem()
        }
        return latestState
    }

    private fun testFilms(count: Int): List<Film> = List(count, ::testFilm)

    private fun testFilm(index: Int): Film =
        Film(
            slug = "f$index",
            imageUrl = "https://example.com/f$index.jpg",
            releaseYear = 2000 + index,
            name = "Film $index",
        )
}
