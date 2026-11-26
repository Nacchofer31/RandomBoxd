package com.randomboxd.history.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.history.presentation.components.UserPill
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserPillTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val longUsername = "extremely_long_username_that_cannot_fit_in_a_narrow_pill"
    private val pillHeight = 24.dp
    private val pillMaxWidth = 120.dp

    private fun setPillContent(modifier: Modifier = Modifier) {
        composeTestRule.setContent {
            // Narrow width mimics the FlowRow of HistoryCard, so a wrapping username
            // has no room to grow horizontally and is forced to break into lines.
            Box(Modifier.width(pillMaxWidth)) {
                UserPill(
                    username = longUsername,
                    searchMode = FilmSearchMode.INTERSECTION,
                    userCount = 2,
                    modifier = modifier,
                )
            }
        }
    }

    private fun textLayoutResult(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        composeTestRule
            .onNodeWithText(text)
            .fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(results)

        assertEquals("GetTextLayoutResult semantics action returned no layout", 1, results.size)
        return results.single()
    }

    @Test
    fun user_pill_keeps_long_username_on_a_single_line() {
        setPillContent()
        composeTestRule.waitForIdle()

        val layoutResult = textLayoutResult(longUsername)

        assertEquals(
            "A long username must stay on one line instead of wrapping inside the pill",
            1,
            layoutResult.lineCount,
        )
        assertTrue(
            "The overflowing username must be ellipsized instead of clipped",
            layoutResult.isLineEllipsized(0),
        )
        assertTrue(
            "The text must fit within the 24.dp pill height",
            layoutResult.size.height.toFloat() <= with(composeTestRule.density) { pillHeight.toPx() },
        )
    }

    @Test
    fun user_pill_exposes_full_username_in_semantics_and_keeps_expected_height() {
        val pillSizes = mutableListOf<IntSize>()
        setPillContent(modifier = Modifier.onGloballyPositioned { pillSizes += it.size })

        composeTestRule.onNodeWithText(longUsername).assertTextEquals(longUsername)
        composeTestRule.waitForIdle()

        assertTrue("The pill was never laid out", pillSizes.isNotEmpty())

        val maxWidthPx = with(composeTestRule.density) { pillMaxWidth.toPx() }
        val expectedHeightPx = with(composeTestRule.density) { pillHeight.toPx() }
        val pillSize = pillSizes.last()

        assertEquals(expectedHeightPx, pillSize.height.toFloat(), 1f)
        assertTrue(
            "The pill must respect the width constraint of the row it lives in",
            pillSize.width.toFloat() <= maxWidthPx + 1f,
        )
    }
}
