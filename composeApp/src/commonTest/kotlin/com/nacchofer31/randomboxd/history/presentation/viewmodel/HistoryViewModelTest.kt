package com.nacchofer31.randomboxd.history.presentation.viewmodel

import app.cash.turbine.test
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.generated.mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class HistoryViewModelTest : TestsWithMocks() {
    @Mock lateinit var repository: FilmHistoryRepository

    private lateinit var viewModel: HistoryViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val olderPick =
        FilmPick(
            id = 1,
            filmSlug = "older",
            filmName = "Older Film",
            posterUrl = "",
            releaseYear = 2020,
            userNames = listOf("a"),
            searchMode = FilmSearchMode.INTERSECTION,
            selectedGenres = emptySet(),
            timestamp = Instant.fromEpochMilliseconds(1000L),
            isFavorite = false,
        )
    private val newerPick =
        FilmPick(
            id = 2,
            filmSlug = "newer",
            filmName = "Newer Film",
            posterUrl = "",
            releaseYear = 2021,
            userNames = listOf("b"),
            searchMode = FilmSearchMode.UNION,
            selectedGenres = emptySet(),
            timestamp = Instant.fromEpochMilliseconds(2000L),
            isFavorite = true,
        )

    override fun setUpMocks() {
        repository = mocker.mock<FilmHistoryRepository>()
    }

    private fun createViewModel() {
        viewModel = HistoryViewModel(repository)
    }

    @Test
    fun `picks are emitted in repo order`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick, olderPick))
            createViewModel()

            viewModel.historyPicks.test {
                val picks = awaitItem()
                assertEquals(2, picks.size)
                assertEquals("Newer Film", picks[0].filmName)
                assertEquals("Older Film", picks[1].filmName)
                assertTrue(picks[0].isFavorite)
                assertFalse(picks[1].isFavorite)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggle favorite calls repo`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick))
            mocker.everySuspending { repository.updateFavorite(2, false) } returns Unit
            createViewModel()

            viewModel.onAction(HistoryAction.ToggleFavorite(2))
            viewModel.historyPicks.test {
                val picks = awaitItem()
                assertEquals(1, picks.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clear all shows dialog`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick))
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial
                viewModel.onAction(HistoryAction.ClearAll)
                val state = awaitItem()
                assertTrue(state.showClearConfirmDialog)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `confirm clear all calls repo and closes dialog`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick))
            mocker.everySuspending { repository.deleteAll() } returns Unit
            createViewModel()

            // Show dialog first, then confirm closes it
            viewModel.onAction(HistoryAction.ClearAll)
            viewModel.onAction(HistoryAction.ConfirmClearAll)
            // Advance to let coroutines execute
            testScheduler.advanceUntilIdle()

            val currentState = viewModel.state.value
            assertFalse(currentState.showClearConfirmDialog)
        }

    @Test
    fun `dismiss clear hides dialog without deleting`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick))
            createViewModel()

            viewModel.state.test {
                awaitItem() // initial
                viewModel.onAction(HistoryAction.ClearAll)
                awaitItem() // dialog shown
                viewModel.onAction(HistoryAction.DismissClearDialog)
                val state = awaitItem()
                assertFalse(state.showClearConfirmDialog)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `empty picks state has empty list`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(emptyList())
            createViewModel()

            viewModel.historyPicks.test {
                val picks = awaitItem()
                assertTrue(picks.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
            assertFalse(viewModel.state.value.showClearConfirmDialog)
        }

    @Test
    fun `starts in loading state until first emission`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(emptyList())
            createViewModel()

            assertTrue(viewModel.state.value.isLoading)

            viewModel.historyPicks.test {
                awaitItem()
                assertFalse(viewModel.state.value.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `isLoading stays false once data has been emitted`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick))
            createViewModel()

            viewModel.historyPicks.test {
                awaitItem()
                assertFalse(viewModel.state.value.isLoading)
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.historyPicks.test {
                awaitItem()
                assertFalse(viewModel.state.value.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `visiblePicks shows all picks when favorites filter is off`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick, olderPick))
            createViewModel()

            assertFalse(viewModel.state.value.isFavoritesOnly)

            viewModel.visiblePicks.test {
                val picks = awaitItem()
                assertEquals(2, picks.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggling favorites filter shows only favorites`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick, olderPick))
            createViewModel()

            viewModel.onAction(HistoryAction.ToggleFavoritesOnly)

            assertTrue(viewModel.state.value.isFavoritesOnly)

            viewModel.visiblePicks.test {
                val picks = awaitItem()
                assertEquals(1, picks.size)
                assertEquals("Newer Film", picks[0].filmName)
                assertTrue(picks[0].isFavorite)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggling favorites filter off restores all picks`() =
        runTest(testDispatcher) {
            mocker.every { repository.getAllPicks() } returns flowOf(listOf(newerPick, olderPick))
            createViewModel()

            viewModel.onAction(HistoryAction.ToggleFavoritesOnly)
            viewModel.onAction(HistoryAction.ToggleFavoritesOnly)

            assertFalse(viewModel.state.value.isFavoritesOnly)

            viewModel.visiblePicks.test {
                val picks = awaitItem()
                assertEquals(2, picks.size)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
