package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.presentation.components.LoadingOrPrompt
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingOrPromptTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_indicator_is_displayed_when_isLoading_is_true() {
        composeTestRule.setContent {
            LoadingOrPrompt(state = RandomFilmState(isLoading = true))
        }

        composeTestRule.onNodeWithTag("test-loading-indicator").assertIsDisplayed()
    }

    @Test
    fun loading_indicator_does_not_exist_when_isLoading_is_false() {
        composeTestRule.setContent {
            LoadingOrPrompt(state = RandomFilmState(isLoading = false))
        }

        composeTestRule.onNodeWithTag("test-loading-indicator").assertDoesNotExist()
    }

    @Test
    fun rolling_dice_text_is_shown_when_loading() {
        composeTestRule.setContent {
            LoadingOrPrompt(state = RandomFilmState(isLoading = true))
        }

        composeTestRule.onNodeWithText("Rolling the dice...").assertIsDisplayed()
    }

    @Test
    fun finding_random_movie_text_is_shown_when_loading() {
        composeTestRule.setContent {
            LoadingOrPrompt(state = RandomFilmState(isLoading = true))
        }

        composeTestRule.onNodeWithText("Finding your random movie").assertIsDisplayed()
    }
}
