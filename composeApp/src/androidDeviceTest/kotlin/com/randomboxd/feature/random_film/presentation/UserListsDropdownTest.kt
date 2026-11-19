package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.random_film.presentation.components.UserListsDropdown
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserListsDropdownTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val lists =
        listOf(
            UserList(name = "100 Classics", slug = "100-classics", filmCount = 93),
            UserList(name = "Horror Night", slug = "horror-night"),
        )

    @Test
    fun dropdown_shows_loading_indicator() {
        composeTestRule.setContent {
            UserListsDropdown(isLoading = true, lists = emptyList(), error = null, onListSelected = {})
        }

        composeTestRule.onNodeWithTag("test-user-lists-dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test-user-lists-loading").assertIsDisplayed()
    }

    @Test
    fun dropdown_shows_error_message() {
        composeTestRule.setContent {
            UserListsDropdown(
                isLoading = false,
                lists = emptyList(),
                error = DataError.Remote.NO_RESULTS,
                onListSelected = {},
            )
        }

        composeTestRule.onNodeWithText("Couldn't load lists. Try again.").assertIsDisplayed()
    }

    @Test
    fun dropdown_shows_empty_message() {
        composeTestRule.setContent {
            UserListsDropdown(isLoading = false, lists = emptyList(), error = null, onListSelected = {})
        }

        composeTestRule.onNodeWithText("No lists found for this user").assertIsDisplayed()
    }

    @Test
    fun dropdown_shows_list_names_and_film_counts() {
        composeTestRule.setContent {
            UserListsDropdown(isLoading = false, lists = lists, error = null, onListSelected = {})
        }

        composeTestRule.onNodeWithText("100 Classics").assertIsDisplayed()
        composeTestRule.onNodeWithText("93").assertIsDisplayed()
        composeTestRule.onNodeWithText("Horror Night").assertIsDisplayed()
    }

    @Test
    fun dropdown_emits_selected_list() {
        var selected: UserList? = null
        composeTestRule.setContent {
            UserListsDropdown(isLoading = false, lists = lists, error = null, onListSelected = { selected = it })
        }

        composeTestRule.onNodeWithTag("test-user-lists-item-horror-night").performClick()

        assertEquals(lists[1], selected)
    }

    @Test
    fun dropdown_shows_load_more_footer_when_more_pages_exist() {
        composeTestRule.setContent {
            UserListsDropdown(
                isLoading = false,
                lists = lists,
                error = null,
                onListSelected = {},
                canLoadMore = true,
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-load-more").assertIsDisplayed()
    }

    @Test
    fun dropdown_hides_load_more_footer_when_exhausted() {
        composeTestRule.setContent {
            UserListsDropdown(
                isLoading = false,
                lists = lists,
                error = null,
                onListSelected = {},
                canLoadMore = false,
            )
        }

        composeTestRule.onNodeWithTag("test-user-lists-load-more").assertDoesNotExist()
    }

    @Test
    fun dropdown_load_more_footer_triggers_on_load_more() {
        var loadMoreCalls = 0
        composeTestRule.setContent {
            UserListsDropdown(
                isLoading = false,
                lists = lists,
                error = null,
                onListSelected = {},
                canLoadMore = true,
                onLoadMore = { loadMoreCalls++ },
            )
        }

        composeTestRule.waitForIdle()
        val callsAfterScroll = loadMoreCalls
        composeTestRule.onNodeWithTag("test-user-lists-load-more").performClick()

        assertTrue(loadMoreCalls > callsAfterScroll)
    }
}
