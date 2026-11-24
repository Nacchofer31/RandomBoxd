package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
    fun discover_button_click_dispatches_the_discover_action() {
        var capturedAction: RandomFilmAction? = null
        setContent(userNames = listOf(UserName(id = 1, username = "testuser"))) { capturedAction = it }

        composeTestRule.onNodeWithTag("test-discover-lists-button").performClick()

        assertEquals(RandomFilmAction.OnDiscoverListsClick, capturedAction)
    }
}
