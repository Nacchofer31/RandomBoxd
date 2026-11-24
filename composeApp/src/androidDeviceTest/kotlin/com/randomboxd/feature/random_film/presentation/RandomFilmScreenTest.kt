package com.randomboxd.feature.random_film.presentation

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.asImage
import coil3.test.FakeImageLoaderEngine
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreen
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreenRoot
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.utils.startTestKoin
import com.nacchofer31.randomboxd.utils.stopTestKoin
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RandomFilmScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUpKoin() {
        startTestKoin()
    }

    @After
    fun tearDownKoin() {
        stopTestKoin()
    }

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

    @Test
    fun all_random_film_screen_initial_components_should_be_displayed() {
        composeTestRule.setContent {
            RandomFilmScreenRoot(onFilmClicked = {})
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-random-film-submit-button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertDoesNotExist()
    }

    @Test
    fun press_on_disabled_button_should_not_show_loading_indicator() {
        composeTestRule.setContent {
            RandomFilmScreenRoot(onFilmClicked = {})
        }

        composeTestRule.onNodeWithTag("test-random-film-submit-button").performClick()
        composeTestRule.onNodeWithTag("test-random-film-submit-button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertDoesNotExist()
    }

    @Test
    fun enter_text_and_submit_button_should_show_loading_indicator() {
        composeTestRule.setContent {
            RandomFilmScreenRoot(onFilmClicked = {})
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").performTextInput("user")
        composeTestRule.onNodeWithTag("test-random-film-submit-button").performClick()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsDisplayed()
    }

    @Test
    fun enter_text_and_on_clear_text_field_should_remove_text_from_field() {
        composeTestRule.setContent {
            RandomFilmScreenRoot(onFilmClicked = {})
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").performTextInput("user")
        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field-clear-button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field-clear-button").performClick()
    }

    @Test
    fun film_poster_should_be_displayed_and_enabled_on_result_film_not_null() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultFilm =
                    Film(
                        slug = "test-slug",
                        name = "test-name",
                        releaseYear = 2000,
                        imageUrl = "test-image-url",
                    ),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-display").assertExists()
        composeTestRule.onNodeWithTag("test-film-display").performClick()
    }

    @Test
    fun error_view_should_be_displayed_when_state_has_error() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultError = DataError.Remote.NO_RESULTS,
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-error").assertIsDisplayed()
    }

    @Test
    fun error_view_should_be_displayed_for_no_internet_error() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultError = DataError.Remote.NO_INTERNET,
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-error").assertIsDisplayed()
    }

    @Test
    fun union_intersection_switch_visible_when_user_search_list_not_empty() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                userNameSearchList = setOf("user1", "user2"),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-random-film-submit-button").assertIsDisplayed()
    }

    @Test
    fun submit_button_long_click_adds_username_to_search_list() {
        composeTestRule.setContent {
            RandomFilmScreenRoot(onFilmClicked = {})
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").performTextInput("testuser")
        composeTestRule.onNodeWithTag("test-random-film-submit-button").performTouchInput { longClick() }
    }

    @Test
    fun film_display_shows_with_null_release_year() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultFilm =
                    Film(
                        slug = "test-slug",
                        name = "test-name",
                        releaseYear = null,
                        imageUrl = "test-image-url",
                    ),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-display").assertExists()
    }

    @Test
    fun error_view_shows_for_generic_error() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultError = DataError.Remote.UNKNOWN,
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-error").assertIsDisplayed()
    }

    @Test
    fun genre_bottom_sheet_is_visible_when_showGenreBottomSheet_is_true() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                showGenreBottomSheet = true,
            ) { }
        }

        composeTestRule.onNodeWithText("Filter by Genre").assertIsDisplayed()
    }

    @Test
    fun genre_bottom_sheet_is_not_visible_when_showGenreBottomSheet_is_false() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                showGenreBottomSheet = false,
            ) { }
        }

        composeTestRule.onNodeWithText("Filter by Genre").assertDoesNotExist()
    }

    @Test
    fun genre_badge_is_visible_when_genres_are_selected() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                selectedGenres = setOf(FilmGenre.ACTION, FilmGenre.COMEDY),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-random-film-genre-badge").assertIsDisplayed()
    }

    @Test
    fun genre_badge_is_not_shown_when_no_genres_selected() {
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                selectedGenres = emptySet(),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-random-film-genre-badge").assertDoesNotExist()
    }

    @Test
    fun reroll_button_click_triggers_reroll_action() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        var rerollClicked = false
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultFilm =
                    Film(
                        slug = "test-slug",
                        name = "test-name",
                        releaseYear = 2000,
                        imageUrl = "test-image-url",
                    ),
                numberOfResults = 2,
            ) { action ->
                if (action is RandomFilmAction.OnRerollClicked) {
                    rerollClicked = true
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("test-reroll-button").performClick()
        assert(rerollClicked)
    }

    @Test
    fun share_button_opens_share_card_dialog() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultFilm =
                    Film(
                        slug = "test-slug",
                        name = "test-name",
                        releaseYear = 2000,
                        imageUrl = "test-image-url",
                    ),
            ) { }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("test-share-button").performClick()
        composeTestRule.onNodeWithText("The dice has spoken... Today's pick is...").assertIsDisplayed()
    }

    @Test
    fun film_poster_click_triggers_film_clicked_action() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        var filmClicked = false
        composeTestRule.setContent {
            val mutableUserNamesFlow = MutableStateFlow<List<UserName>>(emptyList())
            RandomFilmScreen(
                userNameList = mutableUserNamesFlow,
                resultFilm =
                    Film(
                        slug = "test-slug",
                        name = "test-name",
                        releaseYear = 2000,
                        imageUrl = "test-image-url",
                    ),
            ) { action ->
                if (action is RandomFilmAction.OnFilmClicked) {
                    filmClicked = true
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("test-film-poster").performClick()
        assert(filmClicked)
    }

    // ── User lists dropdown ─────────────────────────────────────────

    private val screenLists =
        listOf(
            UserList(name = "100 Classics", slug = "100-classics", filmCount = 93),
            UserList(name = "Horror Night", slug = "horror-night"),
        )

    @Test
    fun discover_lists_button_is_displayed_and_dispatches_the_action() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-discover-lists-button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-discover-lists-button").performClick()

        assertTrue(actions.contains(RandomFilmAction.OnDiscoverListsClick))
    }

    @Test
    fun user_lists_dropdown_is_not_rendered_when_hidden() {
        composeTestRule.setContent {
            RandomFilmScreen(userNameList = MutableStateFlow<List<UserName>>(emptyList())) { }
        }

        composeTestRule.onNodeWithTag("test-user-lists-dropdown").assertDoesNotExist()
    }

    @Test
    fun user_lists_dropdown_shows_loading_state() {
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                isLoadingUserLists = true,
            ) { }
        }

        composeTestRule.onNodeWithTag("test-user-lists-dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-user-lists-loading").assertIsDisplayed()
    }

    @Test
    fun user_lists_dropdown_shows_error_state() {
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                userListsError = DataError.Remote.NO_RESULTS,
            ) { }
        }

        composeTestRule.onNodeWithText("Couldn't load lists. Try again.").assertIsDisplayed()
    }

    @Test
    fun user_lists_dropdown_click_dispatches_selection() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                filteredUserLists = screenLists,
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-item-horror-night").performClick()

        assertTrue(actions.contains(RandomFilmAction.OnUserListSelected(screenLists[1])))
    }

    @Test
    fun user_lists_dropdown_footer_dispatches_end_reached() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                filteredUserLists = screenLists,
                userListsCanLoadMore = true,
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-load-more").performClick()

        assertTrue(actions.contains(RandomFilmAction.OnUserListsEndReached))
    }

    @Test
    fun user_lists_dropdown_footer_shows_loading_more_spinner() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                filteredUserLists = screenLists,
                userListsCanLoadMore = true,
                isLoadingMoreUserLists = true,
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-load-more").assertIsDisplayed()
    }

    @Test
    fun user_lists_dropdown_footer_shows_load_more_error() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                filteredUserLists = screenLists,
                userListsCanLoadMore = true,
                userListsLoadMoreError = true,
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-load-more").assertIsDisplayed()
    }

    @Test
    fun user_lists_dropdown_hides_footer_while_a_filter_is_active() {
        val actions = mutableListOf<RandomFilmAction>()
        composeTestRule.setContent {
            RandomFilmScreen(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userListsDropdownVisible = true,
                // Root derives `userListsCanLoadMore = userListsHasMore && userListsQuery.isBlank()`.
                filteredUserLists = listOf(screenLists[1]),
                userListsCanLoadMore = false,
                onAction = { actions += it },
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-user-lists-item-horror-night").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-user-lists-load-more").assertDoesNotExist()
    }
}
