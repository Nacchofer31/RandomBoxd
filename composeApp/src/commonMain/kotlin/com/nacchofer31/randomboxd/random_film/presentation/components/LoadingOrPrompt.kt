package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.core.presentation.components.loading.RandomBoxdLoadingDiceView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState

@Composable
internal fun LoadingOrPrompt(state: RandomFilmState) {
    if (state.isLoading) {
        return Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RandomBoxdLoadingDiceView(
                modifier = Modifier.padding(vertical = 30.dp).testTag("test-loading-indicator"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Rolling the dice...",
                color = RandomBoxdColors.White,
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "Finding your random movie",
                color = RandomBoxdColors.BackgroundLightColor,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
