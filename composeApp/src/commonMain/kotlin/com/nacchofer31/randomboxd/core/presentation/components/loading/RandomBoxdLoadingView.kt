package com.nacchofer31.randomboxd.core.presentation.components.loading

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    val bubbles =
        bubbleData
            ?: listOf(
                BubbleData(delay = 0, color = RandomBoxdColors.OrangeAccent),
                BubbleData(delay = 150, color = RandomBoxdColors.GreenAccent),
                BubbleData(delay = 300, color = RandomBoxdColors.BlueAccent),
            )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        bubbles.forEach { bubble ->
            RandomBoxdLoadingViewBubble(bubble)
        }
    }
}

@Composable
fun RandomBoxdLoadingViewBubble(
    bubble: BubbleData,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(bubble.delay.toLong())
        while (true) {
            animatable.animateTo(
                targetValue = -10f,
                animationSpec = tween(600, easing = LinearEasing),
            )
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(600, easing = LinearEasing),
            )
        }
    }

    Box(
        modifier =
            Modifier
                .size(20.dp)
                .offset(y = animatable.value.dp)
                .clip(CircleShape)
                .background(bubble.color),
    )
}

data class BubbleData(
    val delay: Int,
    val color: Color,
)
