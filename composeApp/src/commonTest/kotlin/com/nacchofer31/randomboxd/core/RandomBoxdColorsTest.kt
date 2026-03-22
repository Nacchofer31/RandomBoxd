package com.nacchofer31.randomboxd.core

import androidx.compose.ui.graphics.Color
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomBoxdColorsTest {
    @Test
    fun testColors() {
        val colors =
            listOf(
                Pair(RandomBoxdColors.BackgroundColor, Color(0xff1c2228)),
                Pair(RandomBoxdColors.BackgroundLightColor, Color(0xff99aabb)),
                Pair(RandomBoxdColors.BackgroundDarkColor, Color(0xff14181c)),
                Pair(RandomBoxdColors.GreenAccent, Color(0xff00e054)),
                Pair(RandomBoxdColors.OrangeAccent, Color(0xfff27405)),
                Pair(RandomBoxdColors.BlueAccent, Color(0xff40bcf4)),
                Pair(RandomBoxdColors.White, Color.White),
            )

        for ((colorProperty, expectedColor) in colors) {
            assertEquals(expectedColor, colorProperty)
        }
    }
}
