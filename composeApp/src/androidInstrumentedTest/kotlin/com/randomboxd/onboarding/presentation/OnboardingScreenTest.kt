package com.randomboxd.onboarding.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.onboarding.presentation.OnboardingScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboarding_screen_shows_first_page_on_launch() {
        composeTestRule.setContent {
            OnboardingScreen(onFinish = {})
        }

        composeTestRule.onNodeWithText("RandomBoxd").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").assertIsDisplayed()
    }

    @Test
    fun onboarding_skip_button_calls_on_finish() {
        var finished = false
        composeTestRule.setContent {
            OnboardingScreen(onFinish = { finished = true })
        }

        composeTestRule.onNodeWithText("Skip").performClick()

        assertTrue(finished)
    }

    @Test
    fun onboarding_next_button_advances_to_second_page() {
        composeTestRule.setContent {
            OnboardingScreen(onFinish = {})
        }

        composeTestRule.onNodeWithText("Get Started").performClick()

        composeTestRule.onNodeWithText("Random Selection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun onboarding_navigates_through_all_pages_to_finish() {
        var finished = false
        composeTestRule.setContent {
            OnboardingScreen(onFinish = { finished = true })
        }

        // Page 1 → 2
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        // Page 2 → 3
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        // Page 3 → 4
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        // Page 4 — last page, skip button should NOT be visible
        composeTestRule.onNodeWithText("Start Exploring").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Exploring").performClick()

        assertTrue(finished)
    }
}
