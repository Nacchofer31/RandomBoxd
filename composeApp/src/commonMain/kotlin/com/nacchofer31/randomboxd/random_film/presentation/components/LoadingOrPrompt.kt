package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.core.presentation.components.loading.RandomBoxdLoadingView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.enter_description

@Composable
internal fun LoadingOrPrompt(state: RandomFilmState) {
    if (state.isLoading) {
        return RandomBoxdLoadingView(
            modifier = Modifier.padding(vertical = 30.dp)
        )
    }
    return Text(
        stringResource(Res.string.enter_description),
        color = RandomBoxdColors.BackgroundLightColor,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
    )
}