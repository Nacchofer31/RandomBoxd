package com.nacchofer31.randomboxd.core.presentation.components.loading

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RandomBoxdLoadingDiceView(
    modifier: Modifier = Modifier,
) {
    val dice =
        listOf(
            DiceData(color = RandomBoxdColors.OrangeAccent),
            DiceData(color = RandomBoxdColors.GreenAccent),
            DiceData(color = RandomBoxdColors.BlueAccent),
        )

    val offsetAnim = remember { Animatable(0f) }
    val rotationAnim = remember { Animatable(0f) }
    val currentFaces = remember { mutableStateListOf(1, 1, 1) }

    LaunchedEffect(Unit) {
        while (true) {
            offsetAnim.animateTo(-10f, tween(400, easing = LinearEasing))
            rotationAnim.animateTo(rotationAnim.value + 90f, tween(400, easing = LinearEasing))
            currentFaces.indices.forEach { i -> currentFaces[i] = Random.nextInt(1, 7) }

            offsetAnim.animateTo(0f, tween(300, easing = LinearEasing))
            delay(200)

            repeat(5) { step ->
                val duration = 300 - (step * 40)
                offsetAnim.animateTo(-10f, tween(duration, easing = LinearEasing))
                rotationAnim.animateTo(rotationAnim.value + 180f, tween(duration, easing = LinearEasing))
                currentFaces.indices.forEach { i -> currentFaces[i] = Random.nextInt(1, 7) }

                offsetAnim.animateTo(0f, tween(duration, easing = LinearEasing))
            }

            delay(300)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        dice.forEachIndexed { index, die ->
            RandomBoxdLoadingViewDice(
                die = die,
                offset = offsetAnim.value,
                rotation = rotationAnim.value,
                face = currentFaces[index],
            )
        }
    }
}

@Composable
fun RandomBoxdLoadingViewDice(
    die: DiceData,
    offset: Float,
    rotation: Float,
    face: Int,
) {
    Canvas(
        modifier =
            Modifier
                .size(56.dp)
                .offset(y = offset.dp),
    ) {
        rotate(rotation) {
            drawRoundRect(
                color = die.color,
                size = size,
                cornerRadius =
                    CornerRadius(
                        x = size.minDimension * 0.2f,
                        y = size.minDimension * 0.2f,
                    ),
            )

            val pipRadius = size.minDimension * 0.1f
            val spacing = size.minDimension * 0.25f

            fun drawPip(
                x: Float,
                y: Float,
            ) {
                drawCircle(
                    color = Color.White,
                    radius = pipRadius,
                    center =
                        center.copy(
                            x = center.x + x,
                            y = center.y + y,
                        ),
                )
            }

            when (face) {
                1 -> {
                    drawPip(0f, 0f)
                }

                2 -> {
                    drawPip(-spacing, -spacing)
                    drawPip(spacing, spacing)
                }

                3 -> {
                    drawPip(-spacing, -spacing)
                    drawPip(0f, 0f)
                    drawPip(spacing, spacing)
                }

                4 -> {
                    drawPip(-spacing, -spacing)
                    drawPip(spacing, -spacing)
                    drawPip(-spacing, spacing)
                    drawPip(spacing, spacing)
                }

                5 -> {
                    drawPip(-spacing, -spacing)
                    drawPip(spacing, -spacing)
                    drawPip(0f, 0f)
                    drawPip(-spacing, spacing)
                    drawPip(spacing, spacing)
                }

                6 -> {
                    drawPip(-spacing, -spacing)
                    drawPip(spacing, -spacing)
                    drawPip(-spacing, 0f)
                    drawPip(spacing, 0f)
                    drawPip(-spacing, spacing)
                    drawPip(spacing, spacing)
                }
            }
        }
    }
}

data class DiceData(
    val color: Color,
)
