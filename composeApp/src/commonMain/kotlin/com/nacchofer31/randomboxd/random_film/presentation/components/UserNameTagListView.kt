package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val listState = rememberLazyListState()
    // The label stays expanded only while the chips sit at their start, so the button gives its width back to the
    // row as soon as the user scrolls it.
    val showLabel by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    return Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Pinned outside the LazyRow so the entry point to the popular lists never scrolls away with the chips.
        DiscoverListsButton(onAction = onAction, showLabel = showLabel)

        LazyRow(
            state = listState,
            modifier =
                Modifier
                    .weight(1f)
                    .testTag("test-user-name-tag-list"),
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
private fun DiscoverListsButton(
    onAction: (RandomFilmAction) -> Unit,
    showLabel: Boolean,
) {
    val description = stringResource(Res.string.discover_lists)

    Row(
        modifier =
            Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    color = RandomBoxdColors.TextFieldBackgroundColor,
                    shape = RoundedCornerShape(18.dp),
                ).testTag("test-discover-lists-button")
                .semantics { contentDescription = description }
                .clickable(role = Role.Button) { onAction(RandomFilmAction.OnDiscoverListsClick) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            tint = RandomBoxdColors.White,
            modifier = Modifier.size(18.dp),
        )

        // Collapses while the chips are scrolled away from their start, so the label never eats the width the
        // user is browsing.
        AnimatedVisibility(
            visible = showLabel,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
