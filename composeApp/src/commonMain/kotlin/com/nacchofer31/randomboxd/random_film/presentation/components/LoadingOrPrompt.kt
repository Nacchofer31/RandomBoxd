package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.core.presentation.components.loading.RandomBoxdLoadingDiceView
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.finding_random_movie
import randomboxd.composeapp.generated.resources.rolling_the_dice

@Composable
internal fun LoadingOrPrompt(isLoading: Boolean) {
    if (isLoading) {
        return Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RandomBoxdLoadingDiceView(
                modifier = Modifier.padding(vertical = 30.dp).testTag("test-loading-indicator"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedDotsText(
                baseText = stringResource(Res.string.rolling_the_dice),
                color = RandomBoxdColors.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                stringResource(Res.string.finding_random_movie),
                color = RandomBoxdColors.BackgroundLightColor,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AnimatedDotsText(
    baseText: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    val cleanText = remember(baseText) { baseText.trimEnd { it == '.' } }
    var dotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            delay(500)
        }
    }

    Text(
        text = "$cleanText${".".repeat(dotCount)}",
        color = color,
        textAlign = TextAlign.Center,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier,
    )
}
