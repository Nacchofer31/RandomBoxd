package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors

@Composable
fun ResultsCountChip(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(28.dp)
                .background(
                    color = RandomBoxdColors.GreenAccent,
                    shape = RoundedCornerShape(14.dp),
                ).padding(horizontal = 10.dp)
                .testTag("results-count-chip"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Movie,
            contentDescription = "results_count_icon",
            tint = RandomBoxdColors.BackgroundDarkColor,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = count.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = RandomBoxdColors.BackgroundDarkColor,
        )
    }
}
