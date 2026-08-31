package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.genre_any

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryCard(
    pick: FilmPick,
    metaText: String,
    onPosterClick: (String) -> Unit,
    onFavoriteToggle: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RandomBoxdColors.BackgroundColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(width = 60.dp, height = 84.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = RandomBoxdColors.ElevatedBackgroundColor,
                            shape = RoundedCornerShape(8.dp),
                        ).clickable { onPosterClick(pick.filmSlug) },
                contentAlignment = Alignment.Center,
            ) {
                coil3.compose.AsyncImage(
                    model = pick.posterUrl,
                    contentDescription = pick.filmName,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (pick.releaseYear != null) {
                    Text(
                        text =
                            buildAnnotatedString {
                                append(pick.filmName)
                                append("\n")
                                append(pick.releaseYear.toString())
                                addStyle(
                                    style = SpanStyle(fontSize = 13.sp),
                                    start = pick.filmName.length + 1,
                                    end = length,
                                )
                            },
                        color = RandomBoxdColors.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = pick.filmName,
                        color = RandomBoxdColors.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = metaText,
                    color = RandomBoxdColors.BackgroundLightColor,
                    fontSize = 12.sp,
                )
                if (pick.userNames.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        pick.userNames.forEach { username ->
                            UserPill(
                                username = username,
                                searchMode = pick.searchMode,
                                userCount = pick.userNames.size,
                            )
                        }
                    }
                }
                if (pick.selectedGenres.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        pick.selectedGenres.forEach { genre ->
                            GenreChip(
                                label = stringResource(genre.labelRes),
                                selected = true,
                            )
                        }
                    }
                } else {
                    GenreChip(label = stringResource(Res.string.genre_any), selected = false)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .background(
                                color =
                                    if (pick.isFavorite) {
                                        RandomBoxdColors.TagGreenColor
                                    } else {
                                        RandomBoxdColors.ElevatedBackgroundColor
                                    },
                                shape = RoundedCornerShape(18.dp),
                            ).clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = if (pick.isFavorite) "Unfavorite" else "Favorite",
                        tint =
                            if (pick.isFavorite) {
                                RandomBoxdColors.GreenAccent
                            } else {
                                RandomBoxdColors.TextMuted
                            },
                        modifier = Modifier.size(16.dp),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .background(
                                color = RandomBoxdColors.ElevatedBackgroundColor,
                                shape = RoundedCornerShape(18.dp),
                            ).clickable(onClick = onShareClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = RandomBoxdColors.BlueAccent,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
