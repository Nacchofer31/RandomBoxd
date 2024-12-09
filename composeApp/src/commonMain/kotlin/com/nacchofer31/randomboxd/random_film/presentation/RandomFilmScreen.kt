package com.nacchofer31.randomboxd.random_film.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.presentation.components.UserNameTextField
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.submit


@Composable
fun RandomFilmScreenRoot(
    viewModel: RandomFilmViewModel = koinViewModel()
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
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = RandomBoxdColors.BackgroundLightColor
                ),
                shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                modifier = Modifier
                    .fillMaxHeight()

            ) {
                Text(stringResource(Res.string.submit), color = Color.White, maxLines = 1,)
            }
        }
    }
}