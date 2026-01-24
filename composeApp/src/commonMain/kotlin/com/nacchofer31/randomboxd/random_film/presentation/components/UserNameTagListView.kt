package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import kotlinx.coroutines.flow.Flow

@Composable
fun UserNameTagListView(
    userNameList: Flow<List<UserName>>,
    userNameSearchList: Set<String>,
    onAction: (RandomFilmAction) -> Unit,
    fimSearchMode: FilmSearchMode,
) {
    val userNames by userNameList.collectAsState(initial = emptyList())

    return LazyRow(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        val sortedUserNames = userNames.filter { it.username in userNameSearchList }.reversed() + userNames.filter { it.username !in userNameSearchList }.reversed()

        items(sortedUserNames) { userName ->
            val isIncludedInSearchList = userNameSearchList.contains(userName.username)
            UserNameTag(userName, isIncludedInSearchList, onAction, fimSearchMode)
        }
    }
}

@Composable
fun UserNameTag(
    userName: UserName,
    isIncludedInSearchList: Boolean,
    onAction: (RandomFilmAction) -> Unit,
    fimSearchMode: FilmSearchMode,
) {
    Row(
        modifier =
            Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .background(
                    if (!isIncludedInSearchList) {
                        RandomBoxdColors.Black
                    } else if (fimSearchMode == FilmSearchMode.INTERSECTION) {
                        RandomBoxdColors.GreenAccent
                    } else {
                        RandomBoxdColors.OrangeAccent
                    },
                    shape = RoundedCornerShape(18.dp),
                ).padding(horizontal = 12.dp, vertical = 6.dp)
                .combinedClickable(
                    onClick = { onAction(RandomFilmAction.OnUserNameChanged(userName.username)) },
                    onLongClick = { onAction(RandomFilmAction.OnAddOrRemoveUserNameSearchList(userName.username)) },
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = userName.username,
            color =
                if (!isIncludedInSearchList) {
                    RandomBoxdColors.White
                } else {
                    RandomBoxdColors.Black
                },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Remove",
            tint =
                if (!isIncludedInSearchList) {
                    RandomBoxdColors.White
                } else {
                    RandomBoxdColors.Black
                },
            modifier =
                Modifier
                    .size(18.dp)
                    .clickable {
                        onAction(RandomFilmAction.OnRemoveUserName(userName))
                    },
        )
    }
}
