package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_empty_hint
import randomboxd.composeapp.generated.resources.history_empty_state
import randomboxd.composeapp.generated.resources.history_favorites_empty_hint
import randomboxd.composeapp.generated.resources.history_favorites_empty_state

@Composable
fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.history_empty_state),
            color = RandomBoxdColors.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.history_empty_hint),
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun HistoryFavoritesEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.history_favorites_empty_state),
            color = RandomBoxdColors.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.history_favorites_empty_hint),
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}
