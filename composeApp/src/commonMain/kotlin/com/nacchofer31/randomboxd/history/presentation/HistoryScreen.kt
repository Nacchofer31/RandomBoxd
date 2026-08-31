package com.nacchofer31.randomboxd.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.presentation.components.HistoryCard
import com.nacchofer31.randomboxd.history.presentation.components.HistoryEmptyState
import com.nacchofer31.randomboxd.history.presentation.components.HistoryFavoritesEmptyState
import com.nacchofer31.randomboxd.history.presentation.components.HistoryFooter
import com.nacchofer31.randomboxd.history.presentation.components.HistoryHeader
import com.nacchofer31.randomboxd.history.presentation.components.TimestampDisplay
import com.nacchofer31.randomboxd.history.presentation.components.formatPickTimestamp
import com.nacchofer31.randomboxd.history.presentation.viewmodel.HistoryAction
import com.nacchofer31.randomboxd.history.presentation.viewmodel.HistoryViewModel
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.presentation.components.LoadingOrPrompt
import com.nacchofer31.randomboxd.random_film.presentation.components.ShareFilmCardDialog
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_clear_cancel
import randomboxd.composeapp.generated.resources.history_clear_confirm
import randomboxd.composeapp.generated.resources.history_clear_dialog_message
import randomboxd.composeapp.generated.resources.history_clear_dialog_title
import randomboxd.composeapp.generated.resources.history_days_ago
import randomboxd.composeapp.generated.resources.history_today
import randomboxd.composeapp.generated.resources.history_yesterday
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun HistoryScreenRoot(
    viewModel: HistoryViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onPosterClick: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val visiblePicks by viewModel.visiblePicks.collectAsStateWithLifecycle()

    HistoryScreen(
        picks = visiblePicks,
        showClearConfirmDialog = state.showClearConfirmDialog,
        isFavoritesOnly = state.isFavoritesOnly,
        onBackClick = onBackClick,
        onPosterClick = onPosterClick,
        onAction = viewModel::onAction,
        isLoading = state.isLoading,
        onShareImage = viewModel::shareImage,
    )
}

@Composable
fun HistoryScreen(
    picks: List<FilmPick>,
    showClearConfirmDialog: Boolean,
    isFavoritesOnly: Boolean,
    onBackClick: () -> Unit,
    onPosterClick: (String) -> Unit,
    onAction: (HistoryAction) -> Unit,
    isLoading: Boolean,
    onShareImage: (ImageBitmap, String) -> Unit = { _, _ -> },
) {
    val listState = rememberLazyListState()
    var pickToShare by remember { mutableStateOf<FilmPick?>(null) }
    LaunchedEffect(isFavoritesOnly) {
        listState.scrollToItem(0)
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { onAction(HistoryAction.DismissClearDialog) },
            containerColor = RandomBoxdColors.BackgroundColor,
            titleContentColor = RandomBoxdColors.White,
            textContentColor = RandomBoxdColors.BackgroundLightColor,
            title = { Text(stringResource(Res.string.history_clear_dialog_title)) },
            text = { Text(stringResource(Res.string.history_clear_dialog_message)) },
            confirmButton = {
                TextButton(onClick = { onAction(HistoryAction.ConfirmClearAll) }) {
                    Text(
                        stringResource(Res.string.history_clear_confirm),
                        color = RandomBoxdColors.GreenAccent,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(HistoryAction.DismissClearDialog) }) {
                    Text(
                        stringResource(Res.string.history_clear_cancel),
                        color = RandomBoxdColors.GreenAccent,
                    )
                }
            },
        )
    }

    pickToShare?.let { pick ->
        ShareFilmCardDialog(
            film =
                Film(
                    slug = pick.filmSlug,
                    imageUrl = pick.posterUrl,
                    releaseYear = pick.releaseYear,
                    name = pick.filmName,
                ),
            onShare = onShareImage,
            onDismiss = { pickToShare = null },
        )
    }

    Scaffold(
        topBar = {
            HistoryHeader(
                isFavoritesOnly = isFavoritesOnly,
                onBackClick = onBackClick,
                onClearClick = { onAction(HistoryAction.ClearAll) },
                onFavoritesClick = { onAction(HistoryAction.ToggleFavoritesOnly) },
            )
        },
        containerColor = RandomBoxdColors.BackgroundDarkColor,
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                LoadingOrPrompt(isLoading)
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                if (picks.isEmpty()) {
                    if (isFavoritesOnly) {
                        HistoryFavoritesEmptyState(modifier = Modifier.weight(1f))
                    } else {
                        HistoryEmptyState(modifier = Modifier.weight(1f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(picks, key = { it.id }) { pick ->
                            val metaText = buildMetaText(pick)
                            HistoryCard(
                                pick = pick,
                                metaText = metaText,
                                onPosterClick = onPosterClick,
                                onFavoriteToggle = { onAction(HistoryAction.ToggleFavorite(pick.id)) },
                                onShareClick = { pickToShare = pick },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                HistoryFooter(count = picks.size)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun buildMetaText(pick: FilmPick): String {
    val now = Clock.System.now()
    val display = formatPickTimestamp(pick.timestamp, now, TimeZone.currentSystemDefault())
    return when (display) {
        is TimestampDisplay.Today -> "${stringResource(Res.string.history_today)} · ${display.time}"
        is TimestampDisplay.Yesterday -> "${stringResource(Res.string.history_yesterday)} · ${display.time}"
        is TimestampDisplay.DaysAgo -> stringResource(Res.string.history_days_ago, display.days)
    }
}
