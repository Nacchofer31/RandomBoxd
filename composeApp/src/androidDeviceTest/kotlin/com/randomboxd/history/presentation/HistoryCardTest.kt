package com.randomboxd.history.presentation

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.asImage
import coil3.test.FakeImageLoaderEngine
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.presentation.components.HistoryCard
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@RunWith(AndroidJUnit4::class)
class HistoryCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    @OptIn(DelicateCoilApi::class)
    fun resetImageLoader() {
        SingletonImageLoader.reset()
    }

    private fun setImageLoader(engine: FakeImageLoaderEngine) {
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(context).components { add(engine) }.build()
        }
    }

    private fun samplePick(
        filmName: String = "Inception",
        releaseYear: Int? = 2010,
        userNames: List<String> = listOf("user1"),
        searchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
        selectedGenres: Set<FilmGenre> = setOf(FilmGenre.SCIENCE_FICTION),
        isFavorite: Boolean = false,
    ) = FilmPick(
        id = 1,
        filmSlug = "inception",
        filmName = filmName,
        posterUrl = "https://example.com/poster.jpg",
        releaseYear = releaseYear,
        userNames = userNames,
        searchMode = searchMode,
        selectedGenres = selectedGenres,
        timestamp = Instant.fromEpochMilliseconds(0L),
        isFavorite = isFavorite,
    )

    private fun setCardContent(
        pick: FilmPick,
        onPosterClick: (String) -> Unit = {},
        onFavoriteToggle: () -> Unit = {},
        onShareClick: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            HistoryCard(
                pick = pick,
                metaText = "Today · 10:30",
                onPosterClick = onPosterClick,
                onFavoriteToggle = onFavoriteToggle,
                onShareClick = onShareClick,
            )
        }
    }

    @Test
    fun history_card_shows_film_name_and_release_year() {
        setCardContent(samplePick())

        composeTestRule.onNodeWithText("Inception", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2010", substring = true).assertIsDisplayed()
    }

    @Test
    fun history_card_shows_film_name_without_release_year() {
        setCardContent(samplePick(releaseYear = null))

        composeTestRule.onNodeWithText("Inception").assertIsDisplayed()
        composeTestRule.onNodeWithText("2010").assertDoesNotExist()
    }

    @Test
    fun history_card_shows_meta_text() {
        setCardContent(samplePick())

        composeTestRule.onNodeWithText("Today · 10:30").assertIsDisplayed()
    }

    @Test
    fun history_card_shows_all_user_pills() {
        setCardContent(samplePick(userNames = listOf("user1", "user2")))

        composeTestRule.onNodeWithText("user1").assertIsDisplayed()
        composeTestRule.onNodeWithText("user2").assertIsDisplayed()
    }

    @Test
    fun history_card_shows_selected_genre_chips() {
        setCardContent(
            samplePick(
                selectedGenres = setOf(FilmGenre.SCIENCE_FICTION, FilmGenre.ACTION),
            ),
        )

        composeTestRule.onNodeWithText("Science Fiction").assertIsDisplayed()
        composeTestRule.onNodeWithText("Action").assertIsDisplayed()
    }

    @Test
    fun history_card_shows_any_genre_chip_when_no_genres_selected() {
        setCardContent(samplePick(selectedGenres = emptySet()))

        composeTestRule.onNodeWithText("Any genre").assertIsDisplayed()
    }

    @Test
    fun history_card_hides_user_pills_when_user_list_is_empty() {
        setCardContent(samplePick(userNames = emptyList()))

        composeTestRule.onNodeWithText("user1").assertDoesNotExist()
    }

    @Test
    fun history_card_favorite_button_triggers_callback_when_not_favorite() {
        var toggled = false
        setCardContent(
            samplePick(isFavorite = false),
            onFavoriteToggle = { toggled = true },
        )

        composeTestRule.onNodeWithContentDescription("Favorite").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Favorite").performClick()

        assertTrue(toggled)
    }

    @Test
    fun history_card_favorite_button_triggers_callback_when_favorite() {
        var toggled = false
        setCardContent(
            samplePick(isFavorite = true),
            onFavoriteToggle = { toggled = true },
        )

        composeTestRule.onNodeWithContentDescription("Unfavorite").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Unfavorite").performClick()

        assertTrue(toggled)
    }

    @Test
    fun history_card_poster_click_triggers_callback() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        var clickedSlug: String? = null
        setCardContent(
            samplePick(),
            onPosterClick = { clickedSlug = it },
        )

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Inception").performClick()

        assertTrue(clickedSlug == "inception")
    }

    @Test
    fun history_card_share_button_triggers_callback() {
        var shared = false
        setCardContent(
            samplePick(),
            onShareClick = { shared = true },
        )

        composeTestRule.onNodeWithContentDescription("Share").performClick()

        assertTrue(shared)
    }
}
