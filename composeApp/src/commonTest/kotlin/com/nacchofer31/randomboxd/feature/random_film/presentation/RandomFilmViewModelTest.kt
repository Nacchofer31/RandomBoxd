package com.nacchofer31.randomboxd.feature.random_film.presentation

import app.cash.turbine.test
import com.nacchofer31.randomboxd.core.data.RandomBoxdHttpClientFactory
import com.nacchofer31.randomboxd.random_film.data.dto.FilmDto
import com.nacchofer31.randomboxd.random_film.data.repository_impl.RandomFilmRepositoryImpl
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import com.nacchofer31.randomboxd.utils.http.HttpResponseData
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.runBlocking
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

    private var defaultResponseData =
        HttpResponseData(
            content = """{"slug":"test-slug","image_url":"test-image_url","release_year":"2000","film_name":"test-film_name","film_length":""}""",
            statusCode = HttpStatusCode.OK,
        )

    @BeforeTest
    fun setUp() {
        mockEngine =
            MockEngine.create {
                addHandler { request ->
                    val relativeUrl = request.url.encodedPathAndQuery
                    when (relativeUrl) {
                        "/api?users=user" ->
                            respond(
                                content = defaultResponseData.content,
                                status = defaultResponseData.statusCode,
                                headers =
                                    headers {
                                        set("Content-Type", "application/json")
                                    },
                            )
                        else ->
                            respond(
                                content = "Not mocked",
                                status = HttpStatusCode.NotFound,
                            )
                    }
                }
            }
        httpClient =
            RandomBoxdHttpClientFactory.create(
                engine = mockEngine,
            )
        repository = RandomFilmRepositoryImpl(httpClient)
        viewModel = RandomFilmViewModel(repository)
    }

    @Test
    fun `given successful response when submit button clicked then update state with film`() =
        runBlocking {
            viewModel.state.test {
                val expectedFilm =
                    FilmDto(
                        slug = "test-slug",
                        name = "test-film_name",
                        releaseYear = "2000",
                        imageUrl = "test-image_url",
                    )
                defaultResponseData =
                    HttpResponseData(
                        content = """{"slug":"test-slug","image_url":"test-image_url","release_year":"2000","film_name":"test-film_name","film_length":""}""",
                        statusCode = HttpStatusCode.OK,
                    )

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick("user"))

                awaitItem()

                assertSame(true, awaitItem().isLoading)

                val state = awaitItem()

                assertSame(false, state.isLoading)
                assertEquals(expectedFilm.name, state.resultFilm?.name)
                assertEquals(expectedFilm.releaseYear.toInt(), state.resultFilm?.releaseYear)
            }
        }

    @Test
    fun `given error response when submit button clicked then update state with null film`() =
        runTest {
            viewModel.state.test {
                defaultResponseData =
                    HttpResponseData(
                        content = "",
                        statusCode = HttpStatusCode.InternalServerError,
                    )

                setUp()

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick("user"))

                val state = awaitItem()
                assertSame(false, state.isLoading)
                assertNull(state.resultFilm)
            }
        }

    @Test
    fun `when clear button clicked then resultFilm is null`() =
        runTest {
            viewModel.state.test {
                defaultResponseData =
                    HttpResponseData(
                        content = """{"slug":"test-slug","image_url":"test-image_url","release_year":"2000","film_name":"test-film_name","film_length":""}""",
                        statusCode = HttpStatusCode.OK,
                    )

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick("user"))

                awaitItem()

                awaitItem()

                awaitItem()

                viewModel.onAction(RandomFilmAction.OnClearButtonClick)
                assertNull(awaitItem().resultFilm)
            }
        }

    @Test
    fun `when onFilmClicked success`() =
        runTest {
            viewModel.state.test {
                defaultResponseData =
                    HttpResponseData(
                        content = """{"slug":"test-slug","image_url":"test-image_url","release_year":"2000","film_name":"test-film_name","film_length":""}""",
                        statusCode = HttpStatusCode.OK,
                    )

                viewModel.onAction(RandomFilmAction.OnSubmitButtonClick("user"))

                awaitItem()

                awaitItem()

                viewModel.onAction(
                    RandomFilmAction.OnFilmClicked(
                        Film(
                            slug = "test-slug",
                            name = "test-film_name",
                            releaseYear = 2000,
                            imageUrl = "test-image_url",
                        ),
                    ),
                )
                assertNotNull(awaitItem().resultFilm)
            }
        }
}
