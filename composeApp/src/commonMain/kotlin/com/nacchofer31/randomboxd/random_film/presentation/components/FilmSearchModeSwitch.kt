package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isIntersection = searchMode == FilmSearchMode.INTERSECTION

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(RandomBoxdColors.CardBackground)
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Intersection Button
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isIntersection) RandomBoxdColors.GreenAccent else RandomBoxdColors.CardBackground,
                    ).clickable { if (!isIntersection) onModeChange() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.intersection_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 30.dp, height = 30.dp).padding(start = 4.dp),
                colorFilter = ColorFilter.tint(color = if (searchMode == FilmSearchMode.UNION) RandomBoxdColors.BackgroundLightColor else RandomBoxdColors.BackgroundColor),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(Res.string.intersection_label).uppercase(),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.padding(top = 5.dp),
                fontWeight = FontWeight.SemiBold,
                color = if (isIntersection) RandomBoxdColors.BackgroundColor else RandomBoxdColors.TextMuted,
            )
        }

        // Union Button
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (!isIntersection) RandomBoxdColors.OrangeAccent else RandomBoxdColors.CardBackground,
                    ).clickable { if (isIntersection) onModeChange() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.union_label).uppercase(),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.padding(top = 5.dp),
                fontWeight = FontWeight.SemiBold,
                color = if (!isIntersection) RandomBoxdColors.BackgroundColor else RandomBoxdColors.TextMuted,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                painter = painterResource(Res.drawable.union_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 30.dp, height = 30.dp).padding(start = 4.dp),
                colorFilter = ColorFilter.tint(color = if (searchMode == FilmSearchMode.UNION) RandomBoxdColors.BackgroundColor else RandomBoxdColors.BackgroundLightColor),
            )
        }
    }
}
