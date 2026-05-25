package com.randomboxd.feature.random_film.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.presentation.components.GenreFilterBottomSheet
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenreFilterBottomSheetTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun genre_filter_bottom_sheet_shows_title() {
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Filter by Genre").assertIsDisplayed()
    }

    @Test
    fun genre_filter_bottom_sheet_shows_any_genre_chip() {
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Any genre").assertIsDisplayed()
    }

    @Test
    fun genre_filter_bottom_sheet_shows_genre_chips() {
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Action").assertIsDisplayed()
        composeTestRule.onNodeWithText("Comedy").assertIsDisplayed()
    }

    @Test
    fun genre_filter_bottom_sheet_shows_apply_button() {
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Apply Filter").assertIsDisplayed()
    }

    @Test
    fun clicking_apply_with_no_genres_selected_calls_onApply_with_empty_set() {
        var appliedGenres: Set<FilmGenre>? = null
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = { appliedGenres = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Apply Filter").performClick()

        assertEquals(emptySet<FilmGenre>(), appliedGenres)
    }

    @Test
    fun clicking_genre_chip_then_apply_calls_onApply_with_that_genre() {
        var appliedGenres: Set<FilmGenre>? = null
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = emptySet(),
                onApply = { appliedGenres = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Action").performClick()
        composeTestRule.onNodeWithText("Apply Filter").performClick()

        assertEquals(setOf(FilmGenre.ACTION), appliedGenres)
    }

    @Test
    fun clicking_any_genre_when_genres_selected_calls_onApply_with_empty_set() {
        var appliedGenres: Set<FilmGenre>? = null
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = setOf(FilmGenre.ACTION, FilmGenre.COMEDY),
                onApply = { appliedGenres = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Any genre").performClick()
        composeTestRule.onNodeWithText("Apply Filter").performClick()

        assertEquals(emptySet<FilmGenre>(), appliedGenres)
    }

    @Test
    fun clicking_already_selected_genre_chip_deselects_it_on_apply() {
        var appliedGenres: Set<FilmGenre>? = null
        composeTestRule.setContent {
            GenreFilterBottomSheet(
                selectedGenres = setOf(FilmGenre.ACTION),
                onApply = { appliedGenres = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Action").performClick()
        composeTestRule.onNodeWithText("Apply Filter").performClick()

        assertEquals(emptySet<FilmGenre>(), appliedGenres)
    }
}
