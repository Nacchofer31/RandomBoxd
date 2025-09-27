package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.submit

@Composable
internal fun ActionRow(
    userName: String,
    userNameSearchList: Set<String>,
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
        hint = if (userNameSearchList.isNotEmpty()) userNameSearchList.joinToString(", ") else null,
        onChange = onUserNameChange,
        onRemoveButtonClick = {
            onUserNameChange("")
            onAction(RandomFilmAction.OnClearButtonClick)
        },
        modifier = Modifier.weight(1f).testTag("test-random-film-user-name-text-field"),
    )

    val enabled = (userName.trim().isNotEmpty() || userNameSearchList.isNotEmpty()) && !isLoading

    Surface(
        modifier =
            Modifier
                .fillMaxHeight()
                .testTag("test-random-film-submit-button")
                .combinedClickable(
                    enabled = enabled,
                    onClick = {
                        focusManager.clearFocus()
                        if (userName.isEmpty()) {
                            onAction(RandomFilmAction.OnSubmitButtonClick(false))
                        } else {
                            onAction(RandomFilmAction.OnSubmitButtonClick(true))
                        }
                    },
                    onLongClick = {
                        if (userName.trim().isNotEmpty()) {
                            focusManager.clearFocus()
                            onUserNameChange("")
                            onAction(RandomFilmAction.OnClearButtonClick)
                            onAction(RandomFilmAction.OnUserNameAdded(userName))
                            onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList(userName))
                        }
                    },
                ),
        color = if (enabled) RandomBoxdColors.BackgroundLightColor else Color.Gray,
        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                stringResource(Res.string.submit).uppercase(),
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 15.dp),
            )
        }
    }
}
