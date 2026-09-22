package com.randomboxd.history.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.history.presentation.components.FavoritesFilterChip
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesFilterChipTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setChipContent(
        isActive: Boolean,
        showLabel: Boolean = true,
        onClick: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FavoritesFilterChip(
                isActive = isActive,
                onClick = onClick,
                showLabel = showLabel,
            )
        }
    }

    @Test
    fun favorites_chip_shows_label_when_active_and_label_enabled() {
        setChipContent(isActive = true)

        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun favorites_chip_shows_label_when_inactive_and_label_enabled() {
        setChipContent(isActive = false)

        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun favorites_chip_hides_label_when_label_disabled() {
        setChipContent(isActive = false, showLabel = false)

        composeTestRule.onNodeWithText("Favorites").assertDoesNotExist()
    }

    @Test
    fun favorites_chip_click_triggers_callback() {
        var clicked = false
        setChipContent(isActive = false, onClick = { clicked = true })

        composeTestRule.onNodeWithTag("test-history-favorites-chip").performClick()

        assertTrue(clicked)
    }
}
