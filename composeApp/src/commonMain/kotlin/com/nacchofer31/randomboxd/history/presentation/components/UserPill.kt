package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode

@Composable
fun UserPill(
    username: String,
    searchMode: FilmSearchMode,
    userCount: Int,
    modifier: Modifier = Modifier,
) {
    val (bgColor, textColor) =
        when {
            userCount <= 1 -> {
                RandomBoxdColors.Black to RandomBoxdColors.White
            }

            searchMode == FilmSearchMode.INTERSECTION -> {
                RandomBoxdColors.GreenAccent to RandomBoxdColors.BackgroundDarkColor
            }

            else -> {
                RandomBoxdColors.OrangeAccent to RandomBoxdColors.BackgroundDarkColor
            }
        }

    Box(
        modifier =
            modifier
                .height(24.dp)
                .background(color = bgColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = username,
            color = textColor,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            style =
                TextStyle(
                    lineHeightStyle =
                        LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None,
                        ),
                ),
        )
    }
}
