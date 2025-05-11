package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreen
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreenRoot
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RandomFilmScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun all_random_film_screen_initial_components_should_be_displayed() {
        composeTestRule.setContent {
            RandomFilmScreenRoot { }
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-random-film-submit-button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsNotDisplayed()
    }

    @Test
    fun press_on_disabled_button_should_not_show_loading_indicator() {
        composeTestRule.setContent {
            RandomFilmScreenRoot { }
        }

        composeTestRule.onNodeWithTag("test-random-film-submit-button").performClick()
        composeTestRule.onNodeWithTag("test-random-film-submit-button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsNotDisplayed()
    }

    @Test
    fun enter_text_and_submit_button_should_show_loading_indicator() {
        composeTestRule.setContent {
            RandomFilmScreenRoot { }
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").performTextInput("user")
        composeTestRule.onNodeWithTag("test-random-film-submit-button").performClick()
        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsDisplayed()
    }

    @Test
    fun enter_text_and_on_clear_text_field_should_remove_text_from_field() {
        composeTestRule.setContent {
            RandomFilmScreenRoot { }
        }

        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field").performTextInput("user")
        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field-clear-button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-random-film-user-name-text-field-clear-button").performClick()
    }

    @Test
    fun film_poster_should_be_displayed_and_enabled_on_result_film_not_null() {
        composeTestRule.setContent {
            RandomFilmScreen(
                state =
                    RandomFilmState(
                        resultFilm =
                            Film(
                                slug = "test-slug",
                                name = "test-name",
                                releaseYear = 2000,
                                imageUrl = "test-image-url",
                            ),
                    ),
            ) { }
        }

        composeTestRule.onNodeWithTag("test-film-display").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-film-display").performClick()
    }
}
