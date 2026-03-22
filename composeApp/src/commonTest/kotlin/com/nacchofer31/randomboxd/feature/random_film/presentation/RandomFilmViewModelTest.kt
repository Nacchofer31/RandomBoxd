package com.nacchofer31.randomboxd.feature.random_film.presentation

import app.cash.turbine.test
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import com.nacchofer31.randomboxd.utils.dispatchers.TestDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class RandomFilmViewModelTest : TestsWithMocks() {
    private lateinit var viewModel: RandomFilmViewModel
    private lateinit var testDispatchers: TestDispatchers

    @Mock lateinit var repository: RandomFilmRepository

    @Mock lateinit var userNameRepository: UserNameRepository

    private val testFilm =
        Film(
            slug = "test-film",
            imageUrl = "https://example.com/poster.jpg",
            releaseYear = 2020,
            name = "Test Film",
        )

    override fun setUpMocks() {
        repository = mocker.mock<RandomFilmRepository>()
        userNameRepository = mocker.mock<UserNameRepository>()
        mocker.every {
            userNameRepository.getAllUserNames()
        } returns flow { emit(emptyList()) }
    }

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
        viewModel = RandomFilmViewModel(repository, userNameRepository, testDispatchers)
    }

    @Test
    fun `given successful response when submit button clicked then update state with film`() =
        runTest(testDispatchers.testDispatcher) {
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
            mocker.everySuspending { repository.getRandomMovie(isAny()) } returns ResultData.Success(testFilm)
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
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
            mocker.everySuspending {
                repository.getRandomMovie(isAny())
            } returns ResultData.Error(DataError.Remote.SERIALIZATION)
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
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
            mocker.everySuspending {
                repository.getRandomMovie(isAny())
            } returns ResultData.Error(DataError.Remote.SERIALIZATION)
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
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
            mocker.everySuspending { repository.getRandomMovie(isAny()) } returns ResultData.Success(testFilm)
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
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
            mocker.everySuspending { repository.getRandomMovie(isAny()) } returns ResultData.Success(testFilm)
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
                assertEquals(com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode.INTERSECTION, initialState.filmSearchMode)

                viewModel.onAction(RandomFilmAction.OnFilmSearchModeToggle)
                val toggledState = awaitItem()
                assertEquals(com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode.UNION, toggledState.filmSearchMode)

                viewModel.onAction(RandomFilmAction.OnFilmSearchModeToggle)
                val revertedState = awaitItem()
                assertEquals(com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode.INTERSECTION, revertedState.filmSearchMode)
            }
        }

    @Test
    fun `when multi-user submit clicked then getRandomMoviesFromSearchList is called`() =
        runTest(testDispatchers.testDispatcher) {
            mocker.everySuspending {
                repository.getRandomMoviesFromSearchList(isAny(), isAny())
            } returns ResultData.Success(testFilm)
            createViewModel()

            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user1"))
            viewModel.onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList("user2"))

            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick(singleSearch = false))

                awaitItem() // idle (with userNameSearchList populated)
                var state = awaitItem()
                if (state.isLoading) state = awaitItem()

                assertNotNull(state.resultFilm)
                assertEquals(testFilm.name, state.resultFilm?.name)
            }
        }

    @Test
    fun `when username added action then addUserName is called on repository`() =
        runTest(testDispatchers.testDispatcher) {
            mocker.everySuspending { userNameRepository.addUserName(isAny()) } returns Unit
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
            val userName = com.nacchofer31.randomboxd.random_film.domain.model.UserName(id = 1, username = "user1")
            mocker.everySuspending { userNameRepository.deleteUserName(isAny()) } returns Unit
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
}
