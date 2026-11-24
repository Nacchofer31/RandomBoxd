package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTagListView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.discover_lists
import randomboxd.composeapp.generated.resources.discover_lists_label

@RunWith(AndroidJUnit4::class)
class DiscoverListsButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        userNames: List<UserName> = emptyList(),
        onAction: (RandomFilmAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            UserNameTagListView(
                userNameList = MutableStateFlow(userNames),
                userNameSearchList = emptySet(),
                onAction = onAction,
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }
    }

    /** Long enough to overflow the row on any screen, so the lazy row really scrolls. */
    private val scrollingUserNames =
        (1..8).map { index -> UserName(id = index, username = "user-name-with-a-close-icon-$index") }

    private fun setContentWithScrollableChips(): String {
        var label = ""
        val userNames = MutableStateFlow(scrollingUserNames)
        composeTestRule.setContent {
            label = stringResource(Res.string.discover_lists_label)
            UserNameTagListView(
                userNameList = userNames,
                userNameSearchList = emptySet(),
                onAction = {},
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }
        return label
    }

    @Test
    fun discover_button_is_displayed_on_an_empty_chip_row() {
        setContent()

        composeTestRule.onNodeWithTag("test-discover-lists-button").assertIsDisplayed()
    }

    @Test
    fun discover_button_is_displayed_before_the_chips() {
        setContent(userNames = listOf(UserName(id = 1, username = "testuser")))

        composeTestRule.onNodeWithTag("test-discover-lists-button").assertIsDisplayed()
    }

    @Test
    fun discover_button_has_a_content_description() {
        var description = ""
        composeTestRule.setContent {
            description = stringResource(Res.string.discover_lists)
            UserNameTagListView(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userNameSearchList = emptySet(),
                onAction = {},
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithContentDescription(description).assertIsDisplayed()
    }

    @Test
    fun discover_button_shows_its_label_next_to_the_icon() {
        var label = ""
        composeTestRule.setContent {
            label = stringResource(Res.string.discover_lists_label)
            UserNameTagListView(
                userNameList = MutableStateFlow<List<UserName>>(emptyList()),
                userNameSearchList = emptySet(),
                onAction = {},
                fimSearchMode = FilmSearchMode.INTERSECTION,
            )
        }

        composeTestRule.onNodeWithText(label).assertIsDisplayed()
    }

    @Test
    fun discover_button_click_dispatches_the_discover_action() {
        var capturedAction: RandomFilmAction? = null
        setContent(userNames = listOf(UserName(id = 1, username = "testuser"))) { capturedAction = it }

        composeTestRule.onNodeWithTag("test-discover-lists-button").performClick()

        assertEquals(RandomFilmAction.OnDiscoverListsClick, capturedAction)
    }

    @Test
    fun discover_button_collapses_its_label_when_the_chips_scroll() {
        val label = setContentWithScrollableChips()

        composeTestRule.onNodeWithText(label).assertIsDisplayed()

        composeTestRule.onNodeWithTag("test-user-name-tag-list").performScrollToIndex(scrollingUserNames.lastIndex)

        composeTestRule.onNodeWithText(label).assertDoesNotExist()
        // The button itself stays reachable while collapsed.
        composeTestRule.onNodeWithTag("test-discover-lists-button").assertIsDisplayed()
    }

    @Test
    fun discover_button_expands_its_label_again_at_the_start_of_the_chips() {
        val label = setContentWithScrollableChips()

        composeTestRule.onNodeWithTag("test-user-name-tag-list").performScrollToIndex(scrollingUserNames.lastIndex)
        composeTestRule.onNodeWithText(label).assertDoesNotExist()

        composeTestRule.onNodeWithTag("test-user-name-tag-list").performScrollToIndex(0)

        composeTestRule.onNodeWithText(label).assertIsDisplayed()
    }
}
