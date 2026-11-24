package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.discover_lists
import randomboxd.composeapp.generated.resources.discover_lists_label

@Composable
fun UserNameTagListView(
    userNameList: Flow<List<UserName>>,
    userNameSearchList: Set<String>,
    onAction: (RandomFilmAction) -> Unit,
    fimSearchMode: FilmSearchMode,
) {
    val userNames by userNameList.collectAsState(initial = emptyList())

    return Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Pinned outside the LazyRow so the entry point to the popular lists never scrolls away with the chips.
        DiscoverListsButton(onAction = onAction)

        LazyRow(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            val sortedUserNames = userNames.filter { it.username in userNameSearchList }.reversed() + userNames.filter { it.username !in userNameSearchList }.reversed()

            items(sortedUserNames) { userName ->
                val isIncludedInSearchList = userNameSearchList.contains(userName.username)
                UserNameTag(userName, isIncludedInSearchList, onAction, fimSearchMode)
            }
        }
    }
}

/** Pill shaped like an unselected chip with an icon and a short label, so it blends with the username tags. */
@Composable
private fun DiscoverListsButton(onAction: (RandomFilmAction) -> Unit) {
    val description = stringResource(Res.string.discover_lists)

    Row(
        modifier =
            Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    color = RandomBoxdColors.Black,
                    shape = RoundedCornerShape(18.dp),
                ).testTag("test-discover-lists-button")
                .semantics { contentDescription = description }
                .clickable(role = Role.Button) { onAction(RandomFilmAction.OnDiscoverListsClick) }
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            tint = RandomBoxdColors.White,
            modifier = Modifier.size(18.dp),
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(Res.string.discover_lists_label),
            color = RandomBoxdColors.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
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
                    onClick = { onAction(RandomFilmAction.OnUserNameChipClicked(userName.username)) },
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
