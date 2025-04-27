package com.nacchofer31.randomboxd.core

import androidx.compose.ui.graphics.Color
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomBoxdColorsTest {

    @Test
    fun testColors() {
        val colors = listOf(
            Pair(RandomBoxdColors.BackgroundColor, Color(0xff2C343F)),
            Pair(RandomBoxdColors.BackgroundLightColor, Color(0xff556678)),
            Pair(RandomBoxdColors.BackgroundDarkColor, Color(0xff14171C)),
            Pair(RandomBoxdColors.GreenAccent, Color(0xff00B021)),
            Pair(RandomBoxdColors.OrangeAccent, Color(0xfff27405)),
            Pair(RandomBoxdColors.BlueAccent, Color(0xff40bcf4)),
            Pair(RandomBoxdColors.White, Color.White)
        )

        for ((colorProperty, expectedColor) in colors) {
            assertEquals(expectedColor, colorProperty)
        }
    }

}
