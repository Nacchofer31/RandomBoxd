package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
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
    hint: String = stringResource(Res.string.enter_your_user_name),
    onRemoveButtonClick: () -> Unit,
) {

    CompositionLocalProvider(LocalTextSelectionColors provides TextSelectionColors(
        handleColor = RandomBoxdColors.OrangeAccent,
        backgroundColor = Color.White
    )) {
        TextField(
            value = value,
            onValueChange = onChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = RandomBoxdColors.OrangeAccent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = RandomBoxdColors.TextColor
            ),
            placeholder = { Text(hint) },
            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
            singleLine = true,
            modifier = modifier,
            trailingIcon = {
                if (value.trim().isNotEmpty()) {
                    IconButton(onClick = onRemoveButtonClick) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }

}