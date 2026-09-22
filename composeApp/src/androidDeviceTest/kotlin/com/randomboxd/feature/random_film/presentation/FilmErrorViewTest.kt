package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmErrorView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilmErrorViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun error_view_shows_no_results_title_for_no_results_error() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.NO_RESULTS)
        }

        composeTestRule.onNodeWithText("NO RESULTS FOUND").assertIsDisplayed()
    }

    @Test
    fun error_view_shows_no_results_subtitle_for_no_results_error() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.NO_RESULTS)
        }

        composeTestRule.onNodeWithText("No movie matches the search. Please try again with another one.").assertIsDisplayed()
    }

    @Test
    fun error_view_shows_connection_title_for_no_internet_error() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.NO_INTERNET)
        }

        composeTestRule.onNodeWithText("CONNECTION LOST").assertIsDisplayed()
    }

    @Test
    fun error_view_shows_generic_title_for_unknown_error() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.UNKNOWN)
        }

        composeTestRule.onNodeWithText("SOMETHING WENT WRONG!").assertIsDisplayed()
    }

    @Test
    fun error_view_shows_generic_title_for_server_error() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.SERVER)
        }

        composeTestRule.onNodeWithText("SOMETHING WENT WRONG!").assertIsDisplayed()
    }

    @Test
    fun error_view_has_test_tag() {
        composeTestRule.setContent {
            FilmErrorView(error = DataError.Remote.NO_RESULTS)
        }

        composeTestRule.onNodeWithTag("test-film-error").assertIsDisplayed()
    }
}
