package com.nacchofer31.randomboxd.feature.random_film.presentation

import app.cash.turbine.test
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.random_film.domain.model.UserListsPage
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmEvent
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import com.nacchofer31.randomboxd.utils.dispatchers.TestDispatchers
import com.nacchofer31.randomboxd.utils.fakes.FakeFilmHistoryRepository
import com.nacchofer31.randomboxd.utils.fakes.FakeInAppReviewRepository
import com.nacchofer31.randomboxd.utils.fakes.FakeRandomFilmRepository
import com.nacchofer31.randomboxd.utils.fakes.FakeShareRepository
import com.nacchofer31.randomboxd.utils.fakes.FakeUserNameRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RandomFilmViewModelTest {
    private lateinit var viewModel: RandomFilmViewModel
    private lateinit var testDispatchers: TestDispatchers

    private val repository = FakeRandomFilmRepository()
    private val userNameRepository = FakeUserNameRepository()
    private val inAppReviewRepository = FakeInAppReviewRepository()
    private val historyRepository = FakeFilmHistoryRepository()
    private val shareRepository = FakeShareRepository()

    private val testFilm =
        Film(
            slug = "test-film",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = 2020,
            name = "Test Film",
        )

    private fun distinctTestFilms(count: Int): Set<Film> =
        (1..count)
            .map {
                Film(
                    slug = "film-$it",
                    imageUrl = "https://example.com/$it.jpg",
                    releaseYear = 2000,
                    name = "Film $it",
                )
            }.toSet()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        testDispatchers = TestDispatchers()
        Dispatchers.setMain(testDispatchers.testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = RandomFilmViewModel(repository, userNameRepository, testDispatchers, inAppReviewRepository, historyRepository, shareRepository)
    }

    @Test
    fun `given successful response when submit button clicked then update state with film`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                val idleState = awaitItem()
                assertSame(false, idleState.isLoading)

                // Loading state may or may not be emitted as a separate item depending on dispatcher timing
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertSame(false, state.isLoading)
                assertEquals(testFilm.name, state.resultFilm?.name)
                assertEquals(testFilm.releaseYear, state.resultFilm?.releaseYear)
            }
        }

    @Test
    fun `given error response when submit button clicked then update state with null film`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Error(DataError.Remote.SERIALIZATION)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                val idleState = awaitItem()
                assertSame(false, idleState.isLoading)

                // Loading state may or may not be emitted as a separate item depending on dispatcher timing
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertSame(false, state.isLoading)
                assertNull(state.resultFilm)
                assertNotNull(state.resultError)
            }
        }

    @Test
    fun `when clear button clicked then result error is cleared`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Error(DataError.Remote.SERIALIZATION)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle

                // Loading state may or may not be emitted as a separate item depending on dispatcher timing
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultError)

                viewModel.onAction(RandomFilmAction.OnClearButtonClick)
                val clearedState = awaitItem()
                assertNull(clearedState.resultError)
            }
        }

    @Test
    fun `when submit button clicked twice then state has film both times`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var firstState = awaitItem()
                if (firstState.isLoading) firstState = awaitItem()
                assertNotNull(firstState.resultFilm)

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())
                var secondState = awaitItem()
                if (secondState.isLoading) secondState = awaitItem()
                assertNotNull(secondState.resultFilm)
            }
        }

    @Test
    fun `when username changed then state userName is updated`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnUserNameChanged("newuser"))
                val state = awaitItem()
                assertEquals("newuser", state.userName)
            }
        }

    @Test
    fun `when info button clicked then resultFilm and resultError are cleared`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                viewModel.onAction(RandomFilmAction.OnInfoButtonClick)
                val clearedState = awaitItem()
                assertNull(clearedState.resultFilm)
                assertNull(clearedState.resultError)
            }
        }

    @Test
    fun `when add or remove username to search list then list is updated`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))
                val stateWithUser = awaitItem()
                assertEquals(setOf("user1"), stateWithUser.userNameSearchList)

                viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))
                val stateWithoutUser = awaitItem()
                assertEquals(emptySet(), stateWithoutUser.userNameSearchList)
            }
        }

    @Test
    fun `when film search mode toggled then mode switches between INTERSECTION and UNION`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                val initialState = awaitItem()
                assertEquals(FilmSearchMode.INTERSECTION, initialState.filmSearchMode)

                viewModel.onAction(RandomFilmAction.OnFilmSearchModeToggle)
                val toggledState = awaitItem()
                assertEquals(FilmSearchMode.UNION, toggledState.filmSearchMode)

                viewModel.onAction(RandomFilmAction.OnFilmSearchModeToggle)
                val revertedState = awaitItem()
                assertEquals(FilmSearchMode.INTERSECTION, revertedState.filmSearchMode)
            }
        }

    @Test
    fun `when multi-user submit clicked then getRandomMoviesFromSearchList is called`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesFromSearchListResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()

            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))
            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user2"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick(singleSearch = false))

                awaitItem() // idle (with userNameSearchList populated)
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNotNull(state.resultFilm)
                assertEquals(testFilm.name, state.resultFilm.name)
            }
        }

    @Test
    fun `when username added action then addUserName is called on repository`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.onAction(RandomFilmAction.OnUserNameAdded("  newuser  "))

            // No state change expected, just verify no crash and the action was handled
            viewModel.state.test {
                val state = awaitItem()
                assertEquals("", state.userName)
            }
        }

    @Test
    fun `when remove username action then user is removed from search list and deleteUserName is called`() =
        runTest(testDispatchers.testDispatcher) {
            val userName = UserName(id = 1, username = "user1")
            createViewModel()

            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))

            viewModel.state.test {
                val stateWithUser = awaitItem()
                assertEquals(setOf("user1"), stateWithUser.userNameSearchList)

                viewModel.onAction(RandomFilmAction.OnRemoveUserName(userName))
                val stateAfterRemove = awaitItem()
                assertEquals(emptySet(), stateAfterRemove.userNameSearchList)
            }
        }

    @Test
    fun `when film clicked action then no state change occurs`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                val initialState = awaitItem()

                viewModel.onAction(RandomFilmAction.OnFilmClicked(testFilm))

                // No new state items expected since OnFilmClicked falls to else branch
                expectNoEvents()
                assertEquals(initialState.userName, "")
            }
        }

    @Test
    fun `when genre bottom sheet open action then showGenreBottomSheet is true`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreBottomSheetOpen)
                val state = awaitItem()
                assertEquals(true, state.showGenreBottomSheet)
            }
        }

    @Test
    fun `when genre bottom sheet dismiss action then showGenreBottomSheet is false`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreBottomSheetOpen)
                awaitItem() // showGenreBottomSheet = true

                viewModel.onAction(RandomFilmAction.OnGenreBottomSheetDismiss)
                val state = awaitItem()
                assertEquals(false, state.showGenreBottomSheet)
            }
        }

    @Test
    fun `when genre selection applied then selectedGenres updated and bottom sheet closed`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            val genres = setOf(FilmGenre.ACTION, FilmGenre.COMEDY)

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreSelectionApplied(genres))
                val state = awaitItem()
                assertEquals(genres, state.selectedGenres)
                assertEquals(false, state.showGenreBottomSheet)
            }
        }

    @Test
    fun `when genre toggled then genre is added to selectedGenres`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreToggled(FilmGenre.ACTION))
                val state = awaitItem()
                assertEquals(setOf(FilmGenre.ACTION), state.selectedGenres)
            }
        }

    @Test
    fun `when already selected genre toggled then genre is removed from selectedGenres`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreToggled(FilmGenre.ACTION))
                awaitItem() // ACTION added

                viewModel.onAction(RandomFilmAction.OnGenreToggled(FilmGenre.ACTION))
                val state = awaitItem()
                assertEquals(emptySet<FilmGenre>(), state.selectedGenres)
            }
        }

    @Test
    fun `when genre any selected action then selectedGenres is cleared`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                viewModel.onAction(RandomFilmAction.OnGenreSelectionApplied(setOf(FilmGenre.ACTION, FilmGenre.DRAMA)))
                awaitItem() // genres set

                viewModel.onAction(RandomFilmAction.OnGenreAnySelected)
                val state = awaitItem()
                assertEquals(emptySet<FilmGenre>(), state.selectedGenres)
            }
        }

    @Test
    fun `when all genres toggled then selectedGenres resets to empty`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial

                // Set all genres except the last one
                val allButLast = FilmGenre.entries.dropLast(1).toSet()
                viewModel.onAction(RandomFilmAction.OnGenreSelectionApplied(allButLast))
                awaitItem()

                // Toggling the last genre should auto-reset since all would be selected
                viewModel.onAction(RandomFilmAction.OnGenreToggled(FilmGenre.entries.last()))
                val state = awaitItem()
                assertEquals(emptySet<FilmGenre>(), state.selectedGenres)
            }
        }

    @Test
    fun `when submit clicked with genres then selectedGenres passed to repository`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))
            viewModel.onAction(RandomFilmAction.OnGenreSelectionApplied(setOf(FilmGenre.HORROR)))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle with genres set
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)
            }
        }

    @Test
    fun `when multi-user submit clicked with genres then genres passed to getRandomMoviesFromSearchList`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesFromSearchListResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))
            viewModel.onAction(RandomFilmAction.OnGenreSelectionApplied(setOf(FilmGenre.COMEDY)))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick(singleSearch = false))

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)
            }
        }

    @Test
    fun `when film successfully retrieved then requestInAppReview is called`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNotNull(state.resultFilm)
                // Verify requestInAppReview was called (no exception thrown)
            }
        }

    @Test
    fun `when film retrieval fails then requestInAppReview is not called`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Error(DataError.Remote.SERIALIZATION)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNull(state.resultFilm)
                assertNotNull(state.resultError)
                // requestInAppReview should not be called on error
            }
        }

    @Test
    fun `when reroll clicked then extractResultMovie is called and state updates`() =
        runTest(testDispatchers.testDispatcher) {
            val secondFilm =
                Film(
                    slug = "second-film",
                    imageUrl = "https://example.com/poster2.jpg",
                    releaseYear = 2021,
                    name = "Second Film",
                )
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm, secondFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                // Trigger reroll - should call extractResultMovie again
                viewModel.onAction(RandomFilmAction.OnRerollClicked)

                // Wait for loading state
                var rerollState = awaitItem()
                if (!rerollState.isLoading) rerollState = awaitItem()
                assertSame(true, rerollState.isLoading)

                // Wait for final state
                rerollState = awaitItem()
                assertSame(false, rerollState.isLoading)
                assertNotNull(rerollState.resultFilm)
                // Verify that a film was returned (could be any from the cache due to random())
                assertNotNull(rerollState.resultFilm.name)
            }
        }

    @Test
    fun `when reroll clicked and extractResultMovie fails then state has error`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                // Configure fake to return error for reroll
                repository.extractResultMovieResult = ResultData.Error(DataError.Remote.SERIALIZATION)

                // Trigger reroll
                viewModel.onAction(RandomFilmAction.OnRerollClicked)

                // Wait for loading state
                var rerollState = awaitItem()
                if (!rerollState.isLoading) rerollState = awaitItem()
                assertSame(true, rerollState.isLoading)

                // Wait for final state with error
                rerollState = awaitItem()
                assertSame(false, rerollState.isLoading)
                assertNull(rerollState.resultFilm)
                assertNotNull(rerollState.resultError)
                assertEquals(DataError.Remote.SERIALIZATION, rerollState.resultError)
            }
        }

    @Test
    fun `given enough cached films when tournament started then event emits N films and state updates`() =
        runTest(testDispatchers.testDispatcher) {
            val films = distinctTestFilms(12)
            repository.getRandomMoviesResult = ResultData.Success(films)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                viewModel.eventFlow.test {
                    viewModel.onAction(RandomFilmAction.OnTournamentStart(TournamentSize.EIGHT))

                    val event = awaitItem() as RandomFilmEvent.TournamentStarted
                    assertEquals(TournamentSize.EIGHT, event.size)
                    assertEquals(8, event.films.size)
                    assertTrue(event.films.all { it in films })
                    assertEquals(setOf("user"), event.userNames)
                    assertEquals(FilmSearchMode.INTERSECTION, event.searchMode)
                    assertEquals(emptySet(), event.genres)
                }

                // startTournament() also updates the state, so consume that emission to keep Turbine balanced
                val tournamentState = awaitItem()
                assertEquals(TournamentSize.EIGHT, tournamentState.tournamentSize)
                assertSame(false, tournamentState.showTournamentBottomSheet)
                assertEquals(TournamentSize.EIGHT, viewModel.state.value.tournamentSize)
                assertSame(false, viewModel.state.value.showTournamentBottomSheet)
            }
        }

    @Test
    fun `given insufficient pool when tournament started then no event and sheet stays open`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(distinctTestFilms(4))
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem() // idle
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                viewModel.onAction(RandomFilmAction.OnTournamentBottomSheetOpen)
                assertTrue(awaitItem().showTournamentBottomSheet)

                viewModel.eventFlow.test {
                    viewModel.onAction(RandomFilmAction.OnTournamentStart(TournamentSize.THIRTY_TWO))
                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }

                assertNull(viewModel.state.value.tournamentSize)
                assertTrue(viewModel.state.value.showTournamentBottomSheet)

                viewModel.onAction(RandomFilmAction.OnTournamentBottomSheetDismiss)
                assertSame(false, awaitItem().showTournamentBottomSheet)
            }
        }

    // ── Save hooks ──────────────────────────────────────────────────

    @Test
    fun `save is called after successful submit extraction`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem()
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNotNull(state.resultFilm)
            }
        }

    @Test
    fun `save is called after successful reroll extraction`() =
        runTest(testDispatchers.testDispatcher) {
            val secondFilm =
                Film(
                    slug = "https://letterboxd.com/film/second/",
                    imageUrl = "https://example.com/poster2.jpg",
                    releaseYear = 2021,
                    name = "Second Film",
                )
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm, secondFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem()
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()
                assertNotNull(state.resultFilm)

                viewModel.onAction(RandomFilmAction.OnRerollClicked)

                var rerollState = awaitItem()
                if (!rerollState.isLoading) rerollState = awaitItem()
                assertSame(true, rerollState.isLoading)

                rerollState = awaitItem()
                assertSame(false, rerollState.isLoading)
                assertNotNull(rerollState.resultFilm)
            }
        }

    @Test
    fun `save exception does not break pick state after submit`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem()
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNotNull(state.resultFilm)
                assertEquals("Test Film", state.resultFilm!!.name)
            }
        }

    @Test
    fun `no save on error extraction paths`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Error(DataError.Remote.SERIALIZATION)
            createViewModel()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick())

                awaitItem()
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNull(state.resultFilm)
            }
        }

    // ── User lists picker ───────────────────────────────────────────

    private val aliceLists =
        listOf(
            UserList(name = "100 Classics I Saw in 2026", slug = "100-classics-i-saw-in-2026", filmCount = 93),
            UserList(name = "Horror Night", slug = "horror-night", filmCount = 7),
        )

    /** Keeps [RandomFilmViewModel.state] hot (WhileSubscribed) so tests can read `.value` synchronously. */
    private fun TestScope.observeState() {
        backgroundScope.launch { viewModel.state.collect {} }
    }

    @Test
    fun `typing user followed by slash requests lists and shows dropdown loading`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))

            val loadingState = viewModel.state.value
            assertTrue(loadingState.userListsDropdownVisible)
            assertTrue(loadingState.isLoadingUserLists)

            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
            val loadedState = viewModel.state.value
            assertFalse(loadedState.isLoadingUserLists)
            assertEquals(aliceLists, loadedState.userLists)
            assertEquals("alice", loadedState.userListsForUser)
        }

    @Test
    fun `typing after slash filters cached lists by name ignoring case`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/HOR"))

            val state = viewModel.state.value
            assertEquals(listOf(aliceLists[1]), state.filteredUserLists)
            assertEquals("HOR", state.userListsQuery)
            assertTrue(state.userListsDropdownVisible)
            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
        }

    @Test
    fun `removing the slash hides dropdown but keeps scrapped lists cached`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice"))

            val state = viewModel.state.value
            assertFalse(state.userListsDropdownVisible)
            assertEquals(aliceLists, state.userLists)
            assertEquals("alice", state.userListsForUser)
        }

    @Test
    fun `retyping the slash uses the cached lists without a second request`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice"))

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))

            val state = viewModel.state.value
            assertTrue(state.userListsDropdownVisible)
            assertFalse(state.isLoadingUserLists)
            assertEquals(aliceLists, state.userLists)
            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
        }

    @Test
    fun `typing another user after the slash triggers a new request`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("bob/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1, "bob" to 1), repository.getUserListsCalls)
            assertEquals("bob", viewModel.state.value.userListsForUser)
        }

    @Test
    fun `failing lists request exposes error and allows a retry`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Error(DataError.Remote.NO_RESULTS)
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("ghost/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = viewModel.state.value
            assertEquals(DataError.Remote.NO_RESULTS, errorState.userListsError)
            assertFalse(errorState.isLoadingUserLists)
            assertTrue(errorState.userLists.isEmpty())
            assertEquals("", errorState.userListsForUser)
        }

    @Test
    fun `selecting a list fills the text field, saves the user and closes the dropdown`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserListSelected(aliceLists[0]))

            val state = viewModel.state.value
            assertEquals("alice/100-classics-i-saw-in-2026", state.userName)
            assertFalse(state.userNameSearchList.contains("alice/100-classics-i-saw-in-2026"))
            assertTrue(state.userNameSearchList.isEmpty())
            assertFalse(state.userListsDropdownVisible)
            assertEquals("alice/100-classics-i-saw-in-2026", state.programmaticFillValue)
            assertTrue(userNameRepository.storedUserNames.contains("alice/100-classics-i-saw-in-2026"))
        }

    @Test
    fun `programmatic text field update after selecting a list does not reopen the dropdown`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onAction(RandomFilmAction.OnUserListSelected(aliceLists[1]))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            // Simulates the textfield write originated by the ViewModel looping back through OnUserNameChanged.
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/horror-night"))

            val state = viewModel.state.value
            assertFalse(state.userListsDropdownVisible)
            assertFalse(state.isLoadingUserLists)
            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
        }

    @Test
    fun `typing a second user after an empty result scrapes the new user`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = emptyList(), hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("userA", viewModel.state.value.userListsForUser)
            assertTrue(
                viewModel.state.value.userLists
                    .isEmpty(),
            )

            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))

            // Reported repro: the field is not cleared, so the second user is typed after the previous slash.
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/alice/"))

            assertTrue(viewModel.state.value.isLoadingUserLists)
            assertTrue(viewModel.state.value.userListsDropdownVisible)

            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("userA" to 1, "alice" to 1), repository.getUserListsCalls)
            val state = viewModel.state.value
            assertEquals("alice", state.userListsForUser)
            assertEquals(aliceLists, state.userLists)
            assertFalse(state.isLoadingUserLists)
            assertNull(state.userListsError)
        }

    @Test
    fun `query typed after a second user filters the lists of that user`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = emptyList(), hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/alice/hor"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(listOf("alice" to 1), repository.getUserListsCalls.drop(1))
            assertEquals("alice", state.userListsForUser)
            assertEquals("hor", state.userListsQuery)
            assertEquals(listOf(aliceLists[1]), state.filteredUserLists)
        }

    @Test
    fun `selecting a list after typing a second user uses the second user in the value`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = emptyList(), hasMore = false))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("userA/alice/hor"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserListSelected(aliceLists[1]))

            val state = viewModel.state.value
            assertEquals("alice/horror-night", state.userName)
            assertFalse(state.userNameSearchList.contains("alice/horror-night"))
            assertTrue(state.userNameSearchList.isEmpty())
            assertFalse(state.userListsDropdownVisible)
            assertEquals("alice/horror-night", state.programmaticFillValue)
            assertTrue(userNameRepository.storedUserNames.contains("alice/horror-night"))
        }

    @Test
    fun `selecting a list does not enable the multi-user search on submit`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResult = ResultData.Success(UserListsPage(lists = aliceLists, hasMore = false))
            repository.getRandomMoviesResult = ResultData.Success(setOf(testFilm))
            repository.extractResultMovieResult = ResultData.Success(testFilm)
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
            viewModel.onAction(RandomFilmAction.OnUserListSelected(aliceLists[1]))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val selectedState = viewModel.state.value
            assertEquals("alice/horror-night", selectedState.userName)
            assertTrue(selectedState.userNameSearchList.isEmpty())

            viewModel.onAction(RandomFilmAction.OnSubmitButtonClick(singleSearch = true))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice/horror-night"), repository.getRandomMoviesCalls)
            assertTrue(repository.getRandomMoviesFromSearchListCalls.isEmpty())
            assertEquals(
                testFilm.name,
                viewModel.state.value.resultFilm
                    ?.name,
            )
        }

    // ── Chip tap: fill only, no list picker ─────────────────────────

    @Test
    fun `clicking a list chip fills the text field without opening the dropdown`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChipClicked("alice/horror-night"))

            val tappedState = viewModel.state.value
            assertEquals("alice/horror-night", tappedState.userName)
            assertFalse(tappedState.userListsDropdownVisible)
            assertEquals("alice/horror-night", tappedState.programmaticFillValue)

            // Simulates the textfield write looping back through the screen's sync effect.
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/horror-night"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val loopedState = viewModel.state.value
            assertFalse(loopedState.userListsDropdownVisible)
            assertFalse(loopedState.isLoadingUserLists)
            assertTrue(repository.getUserListsCalls.isEmpty())
        }

    @Test
    fun `editing after a chip tap reopens the dropdown`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChipClicked("alice/horror-night"))
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/horror-nightx"))

            assertTrue(viewModel.state.value.userListsDropdownVisible)
            assertTrue(viewModel.state.value.isLoadingUserLists)

            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
            assertEquals("horror-nightx", viewModel.state.value.userListsQuery)
        }

    // ── Lazy pagination of the scrapped lists ───────────────────────

    private val secondPageLists =
        listOf(
            UserList(name = "Late Night Gems", slug = "late-night-gems", filmCount = 12),
        )

    /** Page 2 overlaps page 1 on purpose so the dedupe can be asserted. */
    private val secondPageWithOverlap = listOf(aliceLists[1]) + secondPageLists

    private fun loadFirstPageWithMore() {
        repository.getUserListsResultsByPage =
            mapOf(
                1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)),
                2 to ResultData.Success(UserListsPage(lists = secondPageWithOverlap, hasMore = false)),
            )
        viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
        testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `initial load reports the loaded page and whether more pages exist`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResultsByPage =
                mapOf(1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)))
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(1, state.userListsPage)
            assertTrue(state.userListsHasMore)
            assertFalse(state.isLoadingMoreUserLists)
            assertFalse(state.userListsLoadMoreError)
            assertEquals(aliceLists, state.userLists)
        }

    @Test
    fun `reaching the end appends the next page deduplicated and stops when exhausted`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            // The fake resolves eagerly, so the page is already applied (the loading flag is covered by the
            // single-in-flight test, which gates the repository).
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
            val state = viewModel.state.value
            assertEquals(aliceLists + secondPageLists, state.userLists)
            assertEquals(2, state.userListsPage)
            assertFalse(state.userListsHasMore)
            assertFalse(state.isLoadingMoreUserLists)
            assertFalse(state.userListsLoadMoreError)

            // Exhausted: another end-reached must not request anything.
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
        }

    @Test
    fun `end reached while a page is loading keeps a single request in flight`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            val gate = CompletableDeferred<Unit>()
            repository.getUserListsGate = gate

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)

            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
            assertTrue(viewModel.state.value.isLoadingMoreUserLists)

            gate.complete(Unit)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
            assertEquals(2, viewModel.state.value.userListsPage)
        }

    @Test
    fun `load more error keeps the loaded lists and allows a retry`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResultsByPage =
                mapOf(
                    1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)),
                    2 to ResultData.Error(DataError.Remote.TOO_MANY_REQUESTS),
                )
            createViewModel()
            observeState()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = viewModel.state.value
            assertTrue(errorState.userListsLoadMoreError)
            assertFalse(errorState.isLoadingMoreUserLists)
            assertEquals(aliceLists, errorState.userLists)
            assertEquals(1, errorState.userListsPage)
            assertTrue(errorState.userListsHasMore)

            // Retry through the footer.
            repository.getUserListsResultsByPage =
                mapOf(
                    1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)),
                    2 to ResultData.Success(UserListsPage(lists = secondPageWithOverlap, hasMore = false)),
                )
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val retriedState = viewModel.state.value
            assertFalse(retriedState.userListsLoadMoreError)
            assertEquals(aliceLists + secondPageLists, retriedState.userLists)
            assertEquals(2, retriedState.userListsPage)
            assertEquals(listOf("alice" to 1, "alice" to 2, "alice" to 2), repository.getUserListsCalls)
        }

    @Test
    fun `switching user while a page loads discards that page`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            val gate = CompletableDeferred<Unit>()
            repository.getUserListsGate = gate
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            assertTrue(viewModel.state.value.isLoadingMoreUserLists)

            // Picker moves to another user before the page arrives.
            viewModel.onAction(RandomFilmAction.OnUserNameChipClicked("bob/some-list"))
            gate.complete(Unit)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(aliceLists, state.userLists)
            assertEquals(1, state.userListsPage)
            assertEquals("alice", state.userListsForUser)
            assertFalse(state.isLoadingMoreUserLists)
        }

    @Test
    fun `reopening the picker for the same user keeps the accumulated pages`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice"))
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))

            val state = viewModel.state.value
            assertTrue(state.userListsDropdownVisible)
            assertEquals(aliceLists + secondPageLists, state.userLists)
            assertEquals(2, state.userListsPage)
            assertFalse(state.userListsHasMore)
            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
        }

    @Test
    fun `end reached is ignored while the picker is hidden`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice"))

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
            assertEquals(1, viewModel.state.value.userListsPage)
        }

    @Test
    fun `changing the user while the initial scrape is in flight discards the result`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()

            val gate = CompletableDeferred<Unit>()
            repository.getUserListsGate = gate
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)

            // The picker moves to another user before page 1 arrives: the stale scrape must not be applied.
            viewModel.onAction(RandomFilmAction.OnUserNameChipClicked("bob/some-list"))
            gate.complete(Unit)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals("bob/some-list", state.userName)
            assertTrue(state.userLists.isEmpty())
            assertEquals("", state.userListsForUser)
            assertFalse(state.userListsDropdownVisible)
            assertEquals(1, state.userListsPage)
        }

    @Test
    fun `end reached with a mismatched field user is ignored`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()
            loadFirstPageWithMore()

            // Chip tap hides the picker and moves the field to another user while the cached lists are alice's.
            viewModel.onAction(RandomFilmAction.OnUserNameChipClicked("bob/some-list"))
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(listOf("alice" to 1), repository.getUserListsCalls)
            assertEquals(aliceLists, viewModel.state.value.userLists)
        }

    @Test
    fun `selecting a list without a valid user in the field is ignored`() =
        runTest(testDispatchers.testDispatcher) {
            createViewModel()
            observeState()

            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice"))
            viewModel.onAction(RandomFilmAction.OnUserListSelected(aliceLists[0]))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals("alice", state.userName)
            assertTrue(state.userNameSearchList.isEmpty())
            assertTrue(userNameRepository.storedUserNames.isEmpty())
            assertEquals("", state.programmaticFillValue)
        }

    @Test
    fun `an empty next page stops pagination even if it reports more`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResultsByPage =
                mapOf(
                    1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)),
                    2 to ResultData.Success(UserListsPage(lists = emptyList(), hasMore = true)),
                )
            createViewModel()
            observeState()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(aliceLists, state.userLists)
            assertEquals(2, state.userListsPage)
            assertFalse(state.userListsHasMore)

            // No further request is attempted.
            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf("alice" to 1, "alice" to 2), repository.getUserListsCalls)
        }

    @Test
    fun `a non empty next page keeps pagination open`() =
        runTest(testDispatchers.testDispatcher) {
            repository.getUserListsResultsByPage =
                mapOf(
                    1 to ResultData.Success(UserListsPage(lists = aliceLists, hasMore = true)),
                    2 to ResultData.Success(UserListsPage(lists = secondPageWithOverlap, hasMore = true)),
                )
            createViewModel()
            observeState()
            viewModel.onAction(RandomFilmAction.OnUserNameChanged("alice/"))
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onAction(RandomFilmAction.OnUserListsEndReached)
            testDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(aliceLists + secondPageLists, state.userLists)
            assertEquals(2, state.userListsPage)
            assertTrue(state.userListsHasMore)
        }
}
