package com.nacchofer31.randomboxd.core.presentation.components.loading

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors

@Composable
fun RandomBoxdLoadingView(
    modifier: Modifier = Modifier,
    bubbleData: List<BubbleData>? = null,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val bubbles =
        bubbleData
            ?:
            listOf(
                BubbleData(delay = 0, color = RandomBoxdColors.OrangeAccent),
                BubbleData(delay = 150, color = RandomBoxdColors.GreenAccent),
                BubbleData(delay = 300, color = RandomBoxdColors.BlueAccent),
            )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        bubbles.forEach { bubble ->
            RandomBoxdLoadingViewBubble(bubble, infiniteTransition)
        }
    }
}

@Composable
fun RandomBoxdLoadingViewBubble(
    bubble: BubbleData,
    transition: InfiniteTransition,
) {
    val yOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600, delayMillis = bubble.delay, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Box(
        modifier =
            Modifier
                .size(20.dp)
                .offset(y = yOffset.dp)
                .clip(CircleShape)
                .background(bubble.color),
    )
}

data class BubbleData(
    val delay: Int,
    val color: Color,
)
