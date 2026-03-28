package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmHeader
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilmHeaderTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun film_header_shows_app_name() {
        composeTestRule.setContent {
            FilmHeader(onInfoClick = {})
        }

        composeTestRule.onNodeWithText("RandomBoxd").assertIsDisplayed()
    }

    @Test
    fun film_header_shows_info_button_when_showInfoButton_is_true() {
        composeTestRule.setContent {
            FilmHeader(onInfoClick = {}, showInfoButton = true)
        }

        composeTestRule.onNodeWithContentDescription("Info").assertIsDisplayed()
    }

    @Test
    fun film_header_hides_info_button_when_showInfoButton_is_false() {
        composeTestRule.setContent {
            FilmHeader(onInfoClick = {}, showInfoButton = false)
        }

        composeTestRule.onNodeWithContentDescription("Info").assertDoesNotExist()
    }

    @Test
    fun film_header_info_button_triggers_callback() {
        var clicked = false
        composeTestRule.setContent {
            FilmHeader(onInfoClick = { clicked = true })
        }

        composeTestRule.onNodeWithContentDescription("Info").performClick()

        assertTrue(clicked)
    }

    @Test
    fun film_header_shows_logo_icon() {
        composeTestRule.setContent {
            FilmHeader(onInfoClick = {})
        }

        composeTestRule.onNodeWithContentDescription("Logo").assertIsDisplayed()
    }

    @Test
    fun film_header_hides_info_button_when_showInfoButton_is_null() {
        composeTestRule.setContent {
            FilmHeader(onInfoClick = {}, showInfoButton = null)
        }

        composeTestRule.onNodeWithContentDescription("Info").assertDoesNotExist()
    }
}
