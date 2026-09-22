package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTag
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserNameTagTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUserName = UserName(id = 1, username = "testuser")

    @Test
    fun tag_displays_username() {
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = false,
                onAction = {},
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithText("testuser").assertIsDisplayed()
    }

    @Test
    fun tag_click_triggers_on_user_name_changed_action() {
        var capturedAction: RandomFilmAction? = null
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = false,
                onAction = { capturedAction = it },
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithText("testuser").performClick()

        assertEquals(RandomFilmAction.OnUserNameChanged("testuser"), capturedAction)
    }

    @Test
    fun tag_long_click_triggers_add_or_remove_search_list_action() {
        var capturedAction: RandomFilmAction? = null
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = false,
                onAction = { capturedAction = it },
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithText("testuser").performTouchInput { longClick() }

        assertEquals(RandomFilmAction.OnAddOrRemoveUserNameSearchList("testuser"), capturedAction)
    }

    @Test
    fun tag_shows_when_included_in_intersection_search_list() {
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = true,
                onAction = {},
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithText("testuser").assertIsDisplayed()
    }

    @Test
    fun tag_shows_when_included_in_union_search_list() {
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = true,
                onAction = {},
                fimSearchMode = FilmSearchMode.UNION,
            )
        }

        composeTestRule.onNodeWithText("testuser").assertIsDisplayed()
    }

    @Test
    fun remove_icon_click_triggers_remove_username_action() {
        var removeCalled = false
        composeTestRule.setContent {
            UserNameTag(
                userName = testUserName,
                isIncludedInSearchList = false,
                onAction = { action ->
                    if (action is RandomFilmAction.OnRemoveUserName) removeCalled = true
                },
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithContentDescription("Remove").performClick()

        assertTrue(removeCalled)
    }
}
