package com.randomboxd.feature.random_film.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.presentation.components.UnionIntersectionSwitch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilmSearchModeSwitchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun switch_shows_intersection_and_union_labels() {
        composeTestRule.setContent {
            UnionIntersectionSwitch(
                searchMode = FilmSearchMode.INTERSECTION,
                onModeChange = {},
            )
        }

        composeTestRule.onNodeWithText("INTERSECTION").assertIsDisplayed()
        composeTestRule.onNodeWithText("UNION").assertIsDisplayed()
    }

    @Test
    fun click_union_when_in_intersection_mode_calls_on_mode_change() {
        var toggled = false
        composeTestRule.setContent {
            UnionIntersectionSwitch(
                searchMode = FilmSearchMode.INTERSECTION,
                onModeChange = { toggled = true },
            )
        }

        composeTestRule.onNodeWithText("UNION").performClick()

        assertEquals(true, toggled)
    }

    @Test
    fun click_intersection_when_in_union_mode_calls_on_mode_change() {
        var toggled = false
        composeTestRule.setContent {
            UnionIntersectionSwitch(
                searchMode = FilmSearchMode.UNION,
                onModeChange = { toggled = true },
            )
        }

        composeTestRule.onNodeWithText("INTERSECTION").performClick()

        assertEquals(true, toggled)
    }

    @Test
    fun click_active_mode_does_not_call_on_mode_change() {
        var toggleCount = 0
        composeTestRule.setContent {
            UnionIntersectionSwitch(
                searchMode = FilmSearchMode.INTERSECTION,
                onModeChange = { toggleCount++ },
            )
        }

        composeTestRule.onNodeWithText("INTERSECTION").performClick()

        assertEquals(0, toggleCount)
    }

    @Test
    fun toggle_switches_between_modes() {
        composeTestRule.setContent {
            var mode by remember { mutableStateOf(FilmSearchMode.INTERSECTION) }
            UnionIntersectionSwitch(
                searchMode = mode,
                onModeChange = {
                    mode = if (mode == FilmSearchMode.INTERSECTION) {
                        FilmSearchMode.UNION
                    } else {
                        FilmSearchMode.INTERSECTION
                    }
                },
            )
        }

        composeTestRule.onNodeWithText("INTERSECTION").assertIsDisplayed()

        composeTestRule.onNodeWithText("UNION").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("UNION").assertIsDisplayed()
    }
}
