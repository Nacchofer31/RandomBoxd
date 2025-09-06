package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.intersection_icon
import randomboxd.composeapp.generated.resources.intersection_label
import randomboxd.composeapp.generated.resources.union_icon
import randomboxd.composeapp.generated.resources.union_label

@Composable
fun UnionIntersectionSwitch(
    modifier: Modifier = Modifier,
    searchMode: FilmSearchMode,
    onModeChange: () -> Unit,
) {
    val unionColor = RandomBoxdColors.OrangeAccent
    val intersectionColor = RandomBoxdColors.GreenAccent
    val selectedColor = if (searchMode == FilmSearchMode.UNION) unionColor else intersectionColor

    Row(
        modifier =
            modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(selectedColor.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 12.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.intersection_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 40.dp, height = 40.dp).padding(end = 4.dp),
                colorFilter = ColorFilter.tint(color = if (searchMode != FilmSearchMode.UNION) selectedColor else RandomBoxdColors.BackgroundLightColor),
            )
            Text(
                text = stringResource(Res.string.intersection_label).uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = if (searchMode != FilmSearchMode.UNION) selectedColor else RandomBoxdColors.BackgroundLightColor,
            )
        }

        Switch(
            checked = searchMode == FilmSearchMode.UNION,
            onCheckedChange = { onModeChange() },
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = unionColor,
                    uncheckedThumbColor = intersectionColor,
                    checkedTrackColor = unionColor.copy(alpha = 0.5f),
                    uncheckedTrackColor = intersectionColor.copy(alpha = 0.5f),
                ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp),
        ) {
            Text(
                text = stringResource(Res.string.union_label).uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = if (searchMode == FilmSearchMode.UNION) selectedColor else RandomBoxdColors.BackgroundLightColor,
            )
            Image(
                painter = painterResource(Res.drawable.union_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 40.dp, height = 40.dp).padding(start = 4.dp),
                colorFilter = ColorFilter.tint(color = if (searchMode == FilmSearchMode.UNION) selectedColor else RandomBoxdColors.BackgroundLightColor),
            )
        }
    }
}
