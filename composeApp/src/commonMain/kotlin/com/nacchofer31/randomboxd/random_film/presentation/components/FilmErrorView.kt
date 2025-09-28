package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.error_connection_subtitle
import randomboxd.composeapp.generated.resources.error_connection_title
import randomboxd.composeapp.generated.resources.error_generic_subtitle
import randomboxd.composeapp.generated.resources.error_generic_title
import randomboxd.composeapp.generated.resources.error_no_results_subtitle
import randomboxd.composeapp.generated.resources.error_no_results_title

@Composable
internal fun FilmErrorView(
    error: DataError.Remote,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(vertical = 30.dp, horizontal = 20.dp).testTag("test-film-error"),
) {
    Icon(error.icon(), contentDescription = "test-film-icon", tint = RandomBoxdColors.White, modifier = Modifier.size(60.dp))
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        stringResource(error.title()).uppercase(),
        color = RandomBoxdColors.White,
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        stringResource(error.subtitle()),
        color = RandomBoxdColors.BackgroundLightColor,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        modifier = Modifier.fillMaxWidth(),
    )
}

fun DataError.Remote.title(): StringResource =
    when (this) {
        DataError.Remote.NO_RESULTS -> Res.string.error_no_results_title
        DataError.Remote.NO_INTERNET -> Res.string.error_connection_title
        else -> Res.string.error_generic_title
    }

fun DataError.Remote.subtitle(): StringResource =
    when (this) {
        DataError.Remote.NO_RESULTS -> Res.string.error_no_results_subtitle
        DataError.Remote.NO_INTERNET -> Res.string.error_connection_subtitle
        else -> Res.string.error_generic_subtitle
    }

fun DataError.Remote.icon(): ImageVector =
    when (this) {
        DataError.Remote.NO_INTERNET -> Icons.Outlined.WifiOff
        else -> Icons.Outlined.Info
    }
