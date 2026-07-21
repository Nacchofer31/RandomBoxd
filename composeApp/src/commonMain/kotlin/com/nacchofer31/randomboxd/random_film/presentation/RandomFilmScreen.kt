package com.nacchofer31.randomboxd.random_film.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.components.ActionRow
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmDisplay
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmErrorView
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmHeader
import com.nacchofer31.randomboxd.random_film.presentation.components.GenreFilterBottomSheet
import com.nacchofer31.randomboxd.random_film.presentation.components.LoadingOrPrompt
import com.nacchofer31.randomboxd.random_film.presentation.components.RandomFilmInfoView
import com.nacchofer31.randomboxd.random_film.presentation.components.UnionIntersectionSwitch
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTagListView
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomFilmScreenRoot(
    viewModel: RandomFilmViewModel = koinViewModel(),
    onFilmClicked: (Film) -> Unit,
    onInfoClick: () -> Unit = {},
) {
    val stateFlow = viewModel.state
    val isLoading by stateFlow.map { it.isLoading }.collectAsStateWithLifecycle(initialValue = false)
    val resultFilm by stateFlow.map { it.resultFilm }.collectAsStateWithLifecycle(initialValue = null)
    val resultError by stateFlow.map { it.resultError }.collectAsStateWithLifecycle(initialValue = null)
    val userName by stateFlow.map { it.userName }.collectAsStateWithLifecycle(initialValue = "")
    val userNameSearchList by stateFlow.map { it.userNameSearchList }.collectAsStateWithLifecycle(initialValue = emptySet())
    val filmSearchMode by stateFlow.map { it.filmSearchMode }.collectAsStateWithLifecycle(initialValue = FilmSearchMode.INTERSECTION)
    val selectedGenres by stateFlow.map { it.selectedGenres }.collectAsStateWithLifecycle(initialValue = emptySet())
    val showGenreBottomSheet by stateFlow.map { it.showGenreBottomSheet }.collectAsStateWithLifecycle(initialValue = false)
    val numberOfResults by stateFlow.map { it.numberOfResults }.collectAsStateWithLifecycle(initialValue = 0)

    val usernameState = remember { TextFieldState(userName) }

    // Sync ViewModel → TextFieldState: chip clicks, clear, external changes
    LaunchedEffect(userName) {
        if (usernameState.text.toString() != userName) {
            usernameState.setTextAndPlaceCursorAtEnd(userName)
        }
    }

    // Sync TextFieldState → ViewModel: keyboard input
    LaunchedEffect(usernameState) {
        snapshotFlow { usernameState.text.toString() }.collectLatest {
            viewModel.onAction(RandomFilmAction.OnUserNameChanged(it))
        }
    }

    val onAction =
        remember(viewModel, onFilmClicked, onInfoClick) {
            { action: RandomFilmAction ->
                when (action) {
                    is RandomFilmAction.OnFilmClicked -> onFilmClicked(action.film)
                    is RandomFilmAction.OnInfoButtonClick -> onInfoClick()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        }

    RandomFilmScreen(
        isLoading = isLoading,
        resultFilm = resultFilm,
        resultError = resultError,
        usernameState = usernameState,
        userNameSearchList = userNameSearchList,
        filmSearchMode = filmSearchMode,
        selectedGenres = selectedGenres,
        showGenreBottomSheet = showGenreBottomSheet,
        numberOfResults = numberOfResults,
        userNameList = viewModel.userNameList,
        onAction = onAction,
    )
}

@Composable
fun RandomFilmScreen(
    isLoading: Boolean = false,
    resultFilm: Film? = null,
    resultError: DataError.Remote? = null,
    usernameState: TextFieldState = TextFieldState(""),
    userNameSearchList: Set<String> = emptySet(),
    filmSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
    selectedGenres: Set<FilmGenre> = emptySet(),
    showGenreBottomSheet: Boolean = false,
    numberOfResults: Int = 0,
    userNameList: StateFlow<List<UserName>> = MutableStateFlow(emptyList()),
    onAction: (RandomFilmAction) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    if (showGenreBottomSheet) {
        GenreFilterBottomSheet(
            selectedGenres = selectedGenres,
            onApply = { genres -> onAction(RandomFilmAction.OnGenreSelectionApplied(genres)) },
            onDismiss = { onAction(RandomFilmAction.OnGenreBottomSheetDismiss) },
        )
    }

    Scaffold(
        topBar = {
            FilmHeader(
                showInfoButton = !isLoading,
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
                    resultError?.let {
                        FilmErrorView(it)
                    }
                    resultFilm?.takeIf { !isLoading }?.let {
                        FilmDisplay(it, onAction, numberOfResults)
                    } ?: LoadingOrPrompt(isLoading)

                    if (resultError == null && resultFilm == null && !isLoading) {
                        RandomFilmInfoView()
                    }
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserNameTagListView(
                        userNameList = userNameList,
                        userNameSearchList = userNameSearchList,
                        fimSearchMode = filmSearchMode,
                        onAction = onAction,
                    )
                    ActionRow(
                        usernameState,
                        userNameSearchList,
                        isLoading,
                        focusManager,
                        onAction,
                        filmSearchMode,
                        selectedGenres,
                    )
                    if (userNameSearchList.isNotEmpty()) {
                        UnionIntersectionSwitch(
                            modifier = Modifier.padding(top = 10.dp),
                            searchMode = filmSearchMode,
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
