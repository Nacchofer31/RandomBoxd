package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.presentation.components.RandomFilmInfoView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RandomFilmInfoViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun info_view_shows_ready_to_spin_text() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithText("Ready to spin?").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_roulette_icon() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithContentDescription("Roulette").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_tip_username_format() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithText("Try: username or username/list-name").assertIsDisplayed()
    }

    @Test
    fun info_view_shows_tip_hold_submit() {
        composeTestRule.setContent {
            RandomFilmInfoView()
        }

        composeTestRule.onNodeWithText("Long-press Submit or tags for multi-user mode").assertIsDisplayed()
    }
}
