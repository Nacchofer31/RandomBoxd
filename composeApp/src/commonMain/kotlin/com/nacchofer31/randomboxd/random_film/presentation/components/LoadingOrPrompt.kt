package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.components.loading.RandomBoxdLoadingDiceView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState

@Composable
internal fun LoadingOrPrompt(state: RandomFilmState) {
    if (state.isLoading) {
        return RandomBoxdLoadingDiceView(
            modifier = Modifier.padding(vertical = 30.dp).testTag("test-loading-indicator"),
        )
    }
}
