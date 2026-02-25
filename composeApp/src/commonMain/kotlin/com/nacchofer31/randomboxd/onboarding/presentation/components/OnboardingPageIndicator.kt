package com.nacchofer31.randomboxd.onboarding.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors

@Composable
fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val size by animateDpAsState(
                targetValue = if (isActive) 10.dp else 8.dp,
                animationSpec = tween(300),
            )
            val color by animateColorAsState(
                targetValue = if (isActive) RandomBoxdColors.GreenAccent else RandomBoxdColors.TextMuted,
                animationSpec = tween(300),
            )
            Box(
                modifier =
                    Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(color),
            )
        }
    }
}
