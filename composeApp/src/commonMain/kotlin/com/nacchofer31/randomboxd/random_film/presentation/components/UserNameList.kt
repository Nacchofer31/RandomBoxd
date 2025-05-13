package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import kotlinx.coroutines.flow.Flow

@Composable
fun UserNameList(
    userNameList: Flow<List<UserName>>,
    onAction: (RandomFilmAction) -> Unit,
) {
    val userNames by userNameList.collectAsState(initial = emptyList())

    return LazyRow(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(userNames) { userName ->
            UserNameTag(userName, onAction)
        }
    }
}

@Composable
fun UserNameTag(
    userName: UserName,
    onAction: (RandomFilmAction) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .background(RandomBoxdColors.Black, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable {
                    onAction(RandomFilmAction.OnUserNameChanged(userName.username))
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = userName.username,
            color = RandomBoxdColors.White,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Remove",
            tint = RandomBoxdColors.White,
            modifier =
                Modifier
                    .size(18.dp)
                    .clickable {
                        onAction(RandomFilmAction.OnRemoveUserName(userName))
                    },
        )
    }
}
