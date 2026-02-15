package com.nacchofer31.randomboxd.random_film.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmHeader
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
            FilmHeader(
                onInfoClick = {
                    onAction(RandomFilmAction.OnInfoButtonClick)
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color = RandomBoxdColors.BackgroundDarkColor)
                        .padding(paddingValues)
                        .padding(20.dp)
                        .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserNameTagListView(
                        userNameList = userNameList,
                        userNameSearchList = state.userNameSearchList,
                        fimSearchMode = state.filmSearchMode,
                        onAction = onAction,
                    )
                    ActionRow(
                        state.userName,
                        state.userNameSearchList,
                        state.isLoading,
                        focusManager,
                        onAction,
                        state.filmSearchMode,
                    ) { onAction(RandomFilmAction.OnUserNameChanged(it)) }
                    if (state.userNameSearchList.isNotEmpty()) {
                        UnionIntersectionSwitch(
                            modifier = Modifier.padding(top = 10.dp),
                            searchMode = state.filmSearchMode,
                            onModeChange = {
                                onAction(RandomFilmAction.OnFilmSearchModeToggle)
                            },
                        )
                    }
                }
            }
        },
    )
}
