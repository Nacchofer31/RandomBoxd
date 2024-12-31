package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.submit

@Composable
internal fun ActionRow(
    userName: String,
    isLoading: Boolean,
    focusManager: FocusManager,
    onAction: (RandomFilmAction) -> Unit,
    onUserNameChange: (String) -> Unit,
) = Row(
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.height(56.dp),
) {
    UserNameTextField(
        value = userName,
        onChange = onUserNameChange,
        onRemoveButtonClick = {
            onUserNameChange("")
            onAction(RandomFilmAction.OnClearButtonClick)
        },
        modifier = Modifier.weight(1f),
    )
    Button(
        onClick = {
            focusManager.clearFocus()
            onAction(RandomFilmAction.OnSubmitButtonClick(userName))
        },
        enabled = userName.trim().isNotEmpty() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = RandomBoxdColors.BackgroundLightColor,
            disabledContainerColor = Color.Gray,
        ),
        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            stringResource(Res.string.submit).uppercase(),
            color = Color.White,
            maxLines = 1,
        )
    }
}