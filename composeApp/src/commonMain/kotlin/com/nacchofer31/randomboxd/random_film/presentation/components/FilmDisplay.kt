package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction

@Composable
internal fun FilmDisplay(
    film: Film,
    onAction: (RandomFilmAction) -> Unit,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(vertical = 20.dp).testTag("test-film-display"),
) {
    FilmPoster(
        imageUrl = film.imageUrl,
        title = film.name,
        releaseYear = film.releaseYear.toString() ?: "-",
        onClick = {
            onAction(RandomFilmAction.OnFilmClicked(film))
        },
    )
}
