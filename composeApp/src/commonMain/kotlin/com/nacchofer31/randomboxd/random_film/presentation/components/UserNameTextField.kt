package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.enter_your_user_name

@Composable
fun UserNameTextField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String?,
    onRemoveButtonClick: () -> Unit,
) = CompositionLocalProvider(
    LocalTextSelectionColors provides
        TextSelectionColors(
            handleColor = RandomBoxdColors.OrangeAccent,
            backgroundColor = Color.White,
        ),
) {
    TextField(
        value = value,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        placeholder = {
            Text(
                hint ?: stringResource(Res.string.enter_your_user_name),
                maxLines = 1,
                color = RandomBoxdColors.BackgroundLightColor,
                overflow = TextOverflow.Ellipsis,
            )
        },
        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = RandomBoxdColors.TextFieldBackgroundColor,
                focusedContainerColor = RandomBoxdColors.TextFieldBackgroundColor,
                unfocusedTextColor = RandomBoxdColors.White,
                focusedTextColor = RandomBoxdColors.White,
                selectionColors =
                    TextSelectionColors(
                        handleColor = RandomBoxdColors.OrangeAccent,
                        backgroundColor = RandomBoxdColors.OrangeAccent,
                    ),
            ),
        modifier = modifier,
        trailingIcon =
            if (value.trim().isNotEmpty()) {
                {
                    IconButton(
                        onClick = onRemoveButtonClick,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = RandomBoxdColors.BackgroundColor,
                            ),
                        modifier =
                            Modifier
                                .testTag("test-random-film-user-name-text-field-clear-button")
                                .size(20.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                            tint = RandomBoxdColors.BackgroundLightColor,
                        )
                    }
                }
            } else {
                null
            },
    )
}
