package com.nacchofer31.randomboxd.random_film.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.components.ActionRow
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmDisplay
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmErrorView
import com.nacchofer31.randomboxd.random_film.presentation.components.LoadingOrPrompt
import com.nacchofer31.randomboxd.random_film.presentation.components.RandomFilmInfoView
import com.nacchofer31.randomboxd.random_film.presentation.components.UnionIntersectionSwitch
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTagListView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomFilmScreenRoot(
    viewModel: RandomFilmViewModel = koinViewModel(),
    onFilmClicked: (Film) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RandomFilmScreen(
        state = state,
        userNameList = viewModel.userNameList,
        onAction = { action ->
            when (action) {
                is RandomFilmAction.OnFilmClicked -> onFilmClicked(action.film)
                else -> Unit
            }
            viewModel.onAction(action)
        },
    )
}

@Composable
fun RandomFilmScreen(
    state: RandomFilmState,
    userNameList: StateFlow<List<UserName>>,
    onAction: (RandomFilmAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            if (!(state.resultError == null && state.resultFilm == null)) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(8.dp),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    IconButton(
                        onClick = {
                            onAction(RandomFilmAction.OnInfoButtonClick)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = RandomBoxdColors.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        RandomBoxdColors.BackgroundDarkColor,
                                        RandomBoxdColors.BackgroundColor,
                                    ),
                            ),
                        ).statusBarsPadding()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                        .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
            ) {
                state.resultError?.let {
                    FilmErrorView(it)
                }
                state.resultFilm?.takeIf { !state.isLoading }?.let {
                    FilmDisplay(it, onAction)
                } ?: LoadingOrPrompt(state)

                if (state.resultError == null && state.resultFilm == null && !state.isLoading) {
                    RandomFilmInfoView()
                }
                ActionRow(
                    state.userName,
                    state.userNameSearchList,
                    state.isLoading,
                    focusManager,
                    onAction,
                ) { onAction(RandomFilmAction.OnUserNameChanged(it)) }
                UserNameTagListView(
                    userNameList = userNameList,
                    userNameSearchList = state.userNameSearchList,
                    fimSearchMode = state.filmSearchMode,
                    onAction = onAction,
                )
                if (state.userNameSearchList.isNotEmpty()) {
                    UnionIntersectionSwitch(
                        searchMode = state.filmSearchMode,
                        onModeChange = {
                            onAction(RandomFilmAction.OnFilmSearchModeToggle)
                        },
                    )
                }
            }
        },
    )
}
