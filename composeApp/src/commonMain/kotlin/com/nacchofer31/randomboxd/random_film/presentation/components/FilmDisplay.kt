package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmAction
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.release_year_label

@Composable
internal fun FilmDisplay(
    film: Film,
    onAction: (RandomFilmAction) -> Unit,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(vertical = 20.dp).testTag("test-film-display"),
) {
    FilmPoster(film.imageUrl) {
        onAction(RandomFilmAction.OnFilmClicked(film))
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        film.name,
        color = RandomBoxdColors.White,
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
    Text(
        "${stringResource(Res.string.release_year_label)} ${film.releaseYear ?: '-'}",
        color = RandomBoxdColors.BackgroundLightColor,
        textAlign = TextAlign.Center,
    )
}
