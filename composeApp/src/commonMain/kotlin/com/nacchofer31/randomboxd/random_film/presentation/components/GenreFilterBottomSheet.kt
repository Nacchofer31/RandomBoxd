package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.genre_any
import randomboxd.composeapp.generated.resources.genre_apply
import randomboxd.composeapp.generated.resources.genre_filter_title

private fun genreIcon(genre: FilmGenre): ImageVector =
    when (genre) {
        FilmGenre.ACTION -> Icons.Default.LocalFireDepartment
        FilmGenre.ADVENTURE -> Icons.Default.Explore
        FilmGenre.ANIMATION -> Icons.Default.ChildCare
        FilmGenre.COMEDY -> Icons.Default.EmojiEmotions
        FilmGenre.CRIME -> Icons.Default.GppBad
        FilmGenre.DOCUMENTARY -> Icons.Default.Videocam
        FilmGenre.DRAMA -> Icons.Default.Theaters
        FilmGenre.FAMILY -> Icons.Default.Group
        FilmGenre.FANTASY -> Icons.Default.AutoFixHigh
        FilmGenre.HISTORY -> Icons.Default.AccountBalance
        FilmGenre.HORROR -> Icons.Default.DarkMode
        FilmGenre.MUSIC -> Icons.Default.MusicNote
        FilmGenre.MYSTERY -> Icons.Default.Search
        FilmGenre.ROMANCE -> Icons.Default.Favorite
        FilmGenre.SCIENCE_FICTION -> Icons.Default.RocketLaunch
        FilmGenre.THRILLER -> Icons.Default.Warning
        FilmGenre.TV_MOVIE -> Icons.Default.Tv
        FilmGenre.WAR -> Icons.Default.Shield
        FilmGenre.WESTERN -> Icons.Default.WbSunny
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GenreFilterBottomSheet(
    selectedGenres: Set<FilmGenre>,
    onApply: (Set<FilmGenre>) -> Unit,
    onDismiss: () -> Unit,
) {
    var pendingGenres by remember(selectedGenres) { mutableStateOf(selectedGenres) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RandomBoxdColors.CardBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.genre_filter_title),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            color = RandomBoxdColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                GenreItem(
                    label = stringResource(Res.string.genre_any),
                    icon = Icons.Default.AutoAwesome,
                    isSelected = pendingGenres.isEmpty(),
                    onClick = { pendingGenres = emptySet() },
                )
            }
            items(FilmGenre.entries) { genre ->
                GenreItem(
                    label = stringResource(genre.labelRes),
                    icon = genreIcon(genre),
                    isSelected = pendingGenres.contains(genre),
                    onClick = { pendingGenres = toggleGenreInSet(pendingGenres, genre) },
                )
            }
        }

        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 20.dp),
            onClick = { onApply(pendingGenres) },
            color = RandomBoxdColors.GreenAccent,
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.genre_apply),
                    color = RandomBoxdColors.BackgroundDarkColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = RandomBoxdColors.BackgroundDarkColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private fun toggleGenreInSet(
    current: Set<FilmGenre>,
    genre: FilmGenre,
): Set<FilmGenre> {
    val updated = if (current.contains(genre)) current - genre else current + genre
    return if (updated.size == FilmGenre.entries.size) emptySet() else updated
}

@Composable
private fun GenreItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        color = if (isSelected) RandomBoxdColors.GreenAccent else RandomBoxdColors.TextFieldBackgroundColor,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.BackgroundLightColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                color = if (isSelected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}
