package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.user_lists_empty
import randomboxd.composeapp.generated.resources.user_lists_error
import randomboxd.composeapp.generated.resources.user_lists_loading

private val DROPDOWN_HEIGHT = 200.dp
private const val LOAD_MORE_ITEM_KEY = "user-lists-load-more"

@Composable
fun UserListsDropdown(
    isLoading: Boolean,
    lists: List<UserList>,
    error: DataError.Remote?,
    onListSelected: (UserList) -> Unit,
    modifier: Modifier = Modifier,
    canLoadMore: Boolean = false,
    isLoadingMore: Boolean = false,
    loadMoreError: Boolean = false,
    onLoadMore: () -> Unit = {},
) = Surface(
    modifier =
        modifier
            .fillMaxWidth()
            .height(DROPDOWN_HEIGHT)
            .testTag("test-user-lists-dropdown"),
    color = RandomBoxdColors.CardBackground,
    shape = RoundedCornerShape(10.dp),
) {
    when {
        isLoading -> {
            DropdownMessage(
                text = stringResource(Res.string.user_lists_loading),
                withProgress = true,
            )
        }

        error != null -> {
            DropdownMessage(text = stringResource(Res.string.user_lists_error))
        }

        lists.isEmpty() -> {
            DropdownMessage(text = stringResource(Res.string.user_lists_empty))
        }

        else -> {
            UserListsContent(
                lists = lists,
                onListSelected = onListSelected,
                canLoadMore = canLoadMore,
                isLoadingMore = isLoadingMore,
                loadMoreError = loadMoreError,
                onLoadMore = onLoadMore,
            )
        }
    }
}

@Composable
private fun UserListsContent(
    lists: List<UserList>,
    onListSelected: (UserList) -> Unit,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    loadMoreError: Boolean,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= layoutInfo.totalItemsCount - 2
        }
    }

    // The footer (or the tap fallback on it) is the only way to ask for the next page, so a short list that
    // does not scroll still paginates and an exhausted user stops requesting pages.
    LaunchedEffect(endReached, canLoadMore, isLoadingMore, loadMoreError, lists.size) {
        if (endReached && canLoadMore && !isLoadingMore && !loadMoreError) onLoadMore()
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(lists, key = { it.slug }) { userList ->
            UserListRow(userList = userList, onListSelected = onListSelected)
        }

        if (canLoadMore) {
            item(key = LOAD_MORE_ITEM_KEY) {
                LoadMoreFooter(
                    isLoadingMore = isLoadingMore,
                    loadMoreError = loadMoreError,
                    onLoadMore = onLoadMore,
                )
            }
        }
    }
}

@Composable
private fun LoadMoreFooter(
    isLoadingMore: Boolean,
    loadMoreError: Boolean,
    onLoadMore: () -> Unit,
) = Box(
    modifier =
        Modifier
            .fillMaxWidth()
            .testTag("test-user-lists-load-more")
            .clickable(enabled = !isLoadingMore) { onLoadMore() }
            .padding(vertical = 10.dp),
    contentAlignment = Alignment.Center,
) {
    if (isLoadingMore) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = RandomBoxdColors.GreenAccent,
            strokeWidth = 2.dp,
        )
    } else {
        Text(
            text =
                stringResource(
                    if (loadMoreError) Res.string.user_lists_error else Res.string.user_lists_loading,
                ),
            color = if (loadMoreError) RandomBoxdColors.OrangeAccent else RandomBoxdColors.BackgroundLightColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun UserListRow(
    userList: UserList,
    onListSelected: (UserList) -> Unit,
) {
    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onListSelected(userList) }
                    .testTag("test-user-lists-item-${userList.slug}")
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = userList.name,
                color = RandomBoxdColors.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )

            userList.filmCount?.let { filmCount ->
                Text(
                    text = filmCount.toString(),
                    color = RandomBoxdColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = RandomBoxdColors.TextFieldBackgroundColor)
    }
}

@Composable
private fun DropdownMessage(
    text: String,
    withProgress: Boolean = false,
) = Box(
    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (withProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp).testTag("test-user-lists-loading"),
                color = RandomBoxdColors.GreenAccent,
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = text,
            color = RandomBoxdColors.BackgroundLightColor,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = if (withProgress) 10.dp else 0.dp),
        )
    }
}
