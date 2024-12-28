package com.nacchofer31.randomboxd.random_film.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.presentation.components.FilmPoster
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTextField
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmState
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.release_year_label
import randomboxd.composeapp.generated.resources.submit

@Composable
fun RandomFilmScreenRoot(
    viewModel: RandomFilmViewModel = koinViewModel(),
    onFilmClicked: (Film) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RandomFilmScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is RandomFilmAction.OnFilmClicked -> onFilmClicked(action.film)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun RandomFilmScreen(
    state: RandomFilmState,
    onAction: (RandomFilmAction) -> Unit,
) {

    var userName by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current


    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(RandomBoxdColors.BackgroundColor)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }

    ) {
        if (state.resultFilm != null && !state.isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                FilmPoster(state.resultFilm.imageUrl, onClick = {
                    onAction(
                        RandomFilmAction.OnFilmClicked(
                            film = state.resultFilm
                        )
                    )
                })
                Box(modifier = Modifier.height(20.dp))
                Text(state.resultFilm.name,
                    color = RandomBoxdColors.White,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "${stringResource(Res.string.release_year_label)} ${state.resultFilm.releaseYear}",
                    color = RandomBoxdColors.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (state.isLoading) {
           CircularProgressIndicator(color = RandomBoxdColors.OrangeAccent, modifier = Modifier.padding(vertical = 50.dp))
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 20.dp)
        ) {
            UserNameTextField(
                value = userName,
                onChange = { userName = it },
                onRemoveButtonClick = {
                    userName = ""
                },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick =  {
                    focusManager.clearFocus()
                    onAction(RandomFilmAction.OnSubmitButtonClick(userName))
                },
                enabled = userName.trim().isNotEmpty() && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    //TODO: Check
                    containerColor = RandomBoxdColors.BackgroundLightColor,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                modifier = Modifier
                    .fillMaxHeight()

            ) {
                Text(stringResource(Res.string.submit).uppercase(), color = Color.White, maxLines = 1)
            }
        }
    }
}