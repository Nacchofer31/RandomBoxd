package com.randomboxd.history.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
import com.nacchofer31.randomboxd.history.presentation.HistoryScreen
import com.nacchofer31.randomboxd.history.presentation.HistoryScreenRoot
import com.nacchofer31.randomboxd.history.presentation.viewmodel.HistoryAction
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
class HistoryScreenTest {
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
        id: Int,
        filmName: String,
        isFavorite: Boolean = false,
    ) = FilmPick(
        id = id,
        filmSlug = "slug-$id",
        filmName = filmName,
        posterUrl = "https://example.com/poster.jpg",
        releaseYear = 2010,
        userNames = listOf("user1"),
        searchMode = FilmSearchMode.INTERSECTION,
        selectedGenres = setOf(FilmGenre.ACTION),
        timestamp = Instant.fromEpochMilliseconds(0L),
        isFavorite = isFavorite,
    )

    private fun setScreenContent(
        picks: List<FilmPick>,
        showClearConfirmDialog: Boolean = false,
        isFavoritesOnly: Boolean = false,
        isLoading: Boolean = false,
        onBackClick: () -> Unit = {},
        onPosterClick: (String) -> Unit = {},
        onAction: (HistoryAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            HistoryScreen(
                picks = picks,
                showClearConfirmDialog = showClearConfirmDialog,
                isFavoritesOnly = isFavoritesOnly,
                onBackClick = onBackClick,
                onPosterClick = onPosterClick,
                onAction = onAction,
                isLoading = isLoading,
            )
        }
    }

    @Test
    fun history_screen_shows_empty_state_when_there_are_no_picks() {
        setScreenContent(picks = emptyList())

        composeTestRule.onNodeWithText("No movies picked yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit a random pick to start your history").assertIsDisplayed()
    }

    @Test
    fun history_screen_shows_favorites_empty_state_when_favorites_only_and_no_picks() {
        setScreenContent(
            picks = emptyList(),
            isFavoritesOnly = true,
        )

        composeTestRule.onNodeWithText("No favorite movies yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap the heart on a movie to save it").assertIsDisplayed()
    }

    @Test
    fun history_screen_shows_pick_cards_and_footer_count() {
        setScreenContent(
            picks =
                listOf(
                    samplePick(id = 1, filmName = "Inception"),
                    samplePick(id = 2, filmName = "Interstellar"),
                ),
        )

        composeTestRule.onNodeWithText("Inception", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Interstellar", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2 movies picked in total").assertIsDisplayed()
    }

    @Test
    fun history_screen_back_button_triggers_callback() {
        var backClicked = false
        setScreenContent(
            picks = emptyList(),
            onBackClick = { backClicked = true },
        )

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun history_screen_clear_button_opens_confirm_dialog() {
        var showDialog by mutableStateOf(false)
        var clearAction = false
        composeTestRule.setContent {
            HistoryScreen(
                picks = listOf(samplePick(id = 1, filmName = "Inception")),
                showClearConfirmDialog = showDialog,
                isFavoritesOnly = false,
                onBackClick = {},
                onPosterClick = {},
                onAction = {
                    clearAction = it is HistoryAction.ClearAll
                    if (it is HistoryAction.ClearAll) showDialog = true
                },
                isLoading = false,
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear history").performClick()

        composeTestRule.onNodeWithText("Clear History").assertIsDisplayed()
        assertTrue(clearAction)
    }

    @Test
    fun history_screen_confirm_clear_sends_confirm_all_action() {
        var confirmAction = false
        setScreenContent(
            picks = listOf(samplePick(id = 1, filmName = "Inception")),
            showClearConfirmDialog = true,
            onAction = { confirmAction = it is HistoryAction.ConfirmClearAll },
        )

        composeTestRule.onNodeWithText("Clear").performClick()

        assertTrue(confirmAction)
    }

    @Test
    fun history_screen_cancel_clear_sends_dismiss_action() {
        var dismissAction = false
        setScreenContent(
            picks = listOf(samplePick(id = 1, filmName = "Inception")),
            showClearConfirmDialog = true,
            onAction = { dismissAction = it is HistoryAction.DismissClearDialog },
        )

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertTrue(dismissAction)
    }

    @Test
    fun history_screen_favorites_chip_triggers_toggle_action() {
        var toggleAction = false
        setScreenContent(
            picks = listOf(samplePick(id = 1, filmName = "Inception")),
            onAction = { toggleAction = it is HistoryAction.ToggleFavoritesOnly },
        )

        composeTestRule.onNodeWithTag("test-history-favorites-chip").performClick()

        assertTrue(toggleAction)
    }

    @Test
    fun history_screen_card_favorite_toggle_sends_toggle_favorite_action() {
        var toggledPickId: Int? = null
        setScreenContent(
            picks = listOf(samplePick(id = 42, filmName = "Inception")),
            onAction = {
                if (it is HistoryAction.ToggleFavorite) {
                    toggledPickId = it.pickId
                }
            },
        )

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()

        assertTrue(toggledPickId == 42)
    }

    @Test
    fun history_screen_loading_shows_loading_indicator() {
        setScreenContent(
            picks = emptyList(),
            isLoading = true,
        )

        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsDisplayed()
    }

    @Test
    fun history_screen_root_displays_header_with_di() {
        composeTestRule.setContent {
            HistoryScreenRoot(onBackClick = {}, onPosterClick = {})
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clear history").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
    }

    @Test
    fun history_screen_share_button_opens_share_dialog() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        composeTestRule.setContent {
            HistoryScreen(
                picks = listOf(samplePick(id = 1, filmName = "Inception")),
                showClearConfirmDialog = false,
                isFavoritesOnly = false,
                onBackClick = {},
                onPosterClick = {},
                onAction = {},
                isLoading = false,
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Share").performClick()
        composeTestRule.onNodeWithText("The dice has spoken... Today's pick is...").assertIsDisplayed()
    }
}
