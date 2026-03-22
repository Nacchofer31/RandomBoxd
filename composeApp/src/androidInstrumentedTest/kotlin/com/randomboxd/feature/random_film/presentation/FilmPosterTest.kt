package com.randomboxd.feature.random_film.presentation

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.asImage
import coil3.test.FakeImageLoaderEngine
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmPoster
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilmPosterTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    @OptIn(DelicateCoilApi::class)
    fun resetImageLoader() {
        SingletonImageLoader.reset()
    }

    private fun setImageLoader(engine: FakeImageLoaderEngine) {
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(context).components { add(engine) }.build()
        }
    }

    @Test
    fun film_poster_shows_image_with_valid_dimensions() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        composeTestRule.setContent {
            FilmPoster(
                imageUrl = "https://example.com/poster.jpg",
                title = "Test Film",
                releaseYear = "2020",
                onClick = {},
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("film_image").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("info_icon").assertIsDisplayed()
    }

    @Test
    fun film_poster_shows_image_with_invalid_dimensions_fallback() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        composeTestRule.setContent {
            FilmPoster(
                imageUrl = "https://example.com/poster.jpg",
                title = "Test Film",
                releaseYear = "2020",
                onClick = {},
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("test-film-poster").assertIsDisplayed()
    }

    @Test
    fun film_poster_click_triggers_callback_on_loaded_image() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        setImageLoader(
            FakeImageLoaderEngine
                .Builder()
                .default(bitmap.asImage())
                .build(),
        )

        var clicked = false
        composeTestRule.setContent {
            FilmPoster(
                imageUrl = "https://example.com/poster.jpg",
                title = "Inception",
                releaseYear = "2010",
                onClick = { clicked = true },
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("film_image").performClick()

        assertTrue(clicked)
    }
}
