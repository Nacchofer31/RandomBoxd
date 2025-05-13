package com.nacchofer31.randomboxd.feature.random_film.presentation

import app.cash.turbine.test
import com.nacchofer31.randomboxd.core.data.RandomBoxdHttpClientFactory
import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import com.nacchofer31.randomboxd.random_film.data.repository_impl.RandomFilmRepositoryImpl
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import com.nacchofer31.randomboxd.utils.dispatchers.TestDispatchers
import com.nacchofer31.randomboxd.utils.http.HttpResponseData
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class RandomFilmViewModelTest {
    private lateinit var viewModel: RandomFilmViewModel
    private lateinit var repository: RandomFilmRepository
    private lateinit var httpClient: HttpClient
    private lateinit var mockEngine: HttpClientEngine
    private lateinit var testDispatchers: TestDispatchers

    private var defaultResponseData =
        HttpResponseData(
            content = """{"slug":"test-slug","image_url":"test-image_url","release_year":"2000","film_name":"test-film_name","film_length":""}""",
            statusCode = HttpStatusCode.OK,
        )

    @BeforeTest
    fun setUp() {
        testDispatchers = TestDispatchers()
    }

    private fun setUpWithResponse(responseData: HttpResponseData) {
        mockEngine =
            MockEngine.create {
                addHandler { request ->
                    val relativeUrl = request.url.encodedPathAndQuery
                    when (relativeUrl) {
                        "/api?users=user" ->
                            respond(
                                content = responseData.content,
                                status = responseData.statusCode,
                                headers =
                                    headers {
                                        set("Content-Type", "application/json")
                                    },
                            )
                        else -> respond("Not mocked", HttpStatusCode.NotFound)
                    }
                }
            }
        httpClient = RandomBoxdHttpClientFactory.create(engine = mockEngine)
        repository = RandomFilmRepositoryImpl(httpClient)
        viewModel = RandomFilmViewModel(repository, testDispatchers)
    }

    @Test
    fun `given successful response when submit button clicked then update state with film`() =
        runTest(testDispatchers.testDispatcher) {
            setUpWithResponse(defaultResponseData)
            val expectedFilm =
                FilmDto(
                    slug = "test-slug",
                    name = "test-film_name",
                    releaseYear = "2000",
                    imageUrl = "test-image_url",
                )
            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick)

                val idleState = awaitItem()
                assertSame(false, idleState.isLoading)

                val loadingState = awaitItem()
                assertSame(true, loadingState.isLoading)

                val successState = awaitItem()
                assertSame(false, successState.isLoading)
                assertEquals(expectedFilm.name, successState.resultFilm?.name)
                assertEquals(expectedFilm.releaseYear.toInt(), successState.resultFilm?.releaseYear)
            }
        }

    @Test
    fun `given error response when submit button clicked then update state with null film`() =
        runTest(testDispatchers.testDispatcher) {
            setUpWithResponse(
                HttpResponseData(
                    content = """{}""",
                    statusCode = HttpStatusCode.InternalServerError,
                ),
            )
            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick)

                val idleState = awaitItem()
                assertSame(false, idleState.isLoading)

                val loadingState = awaitItem()
                assertSame(true, loadingState.isLoading)

                val errorState = awaitItem()
                assertSame(false, errorState.isLoading)
                assertNull(errorState.resultFilm)
            }
        }

    @Test
    fun `when clear button clicked then resultFilm is null`() =
        runTest(testDispatchers.testDispatcher) {
            setUpWithResponse(defaultResponseData)
            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick)

                awaitItem()
                awaitItem()
                val successState = awaitItem()
                assertNotNull(successState.resultFilm)

                viewModel.onAction(RandomFilmAction.OnClearButtonClick)
                val clearedState = awaitItem()
                assertNull(clearedState.resultFilm)
            }
        }

    @Test
    fun `when onFilmClicked success`() =
        runTest(testDispatchers.testDispatcher) {
            setUpWithResponse(defaultResponseData)
            viewModel.state.test {
                viewModel.onAction(RandomFilmAction.OnUserNameChanged("user"))
                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick)

                val idleState = awaitItem()
                assertSame(false, idleState.isLoading)

                val loadingState = awaitItem()
                assertSame(true, loadingState.isLoading)

                val successState = awaitItem()
                assertNotNull(successState.resultFilm)

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick)

                val newLoadingState = awaitItem()
                assertSame(true, newLoadingState.isLoading)

                val newSuccessState = awaitItem()
                assertNotNull(newSuccessState.resultFilm)
            }
        }
}
