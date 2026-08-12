package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_favorites_filter
import randomboxd.composeapp.generated.resources.history_subtitle
import randomboxd.composeapp.generated.resources.history_title

@Composable
fun HistoryHeader(
    isFavoritesOnly: Boolean,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val titleText = stringResource(Res.string.history_title)
    val subtitleText = stringResource(Res.string.history_subtitle)
    val favoritesLabel = stringResource(Res.string.history_favorites_filter)

    val titleStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val subtitleStyle = TextStyle(fontSize = 13.sp)
    val chipLabelStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

    val leftTextWidth =
        maxOf(
            with(density) {
                textMeasurer
                    .measure(titleText, titleStyle)
                    .size.width
                    .toDp()
            },
            with(density) {
                textMeasurer
                    .measure(subtitleText, subtitleStyle)
                    .size.width
                    .toDp()
            },
        )
    val chipLabelWidth =
        with(density) {
            textMeasurer
                .measure(favoritesLabel, chipLabelStyle)
                .size.width
                .toDp()
        }

    // left = back button + spacing + title/subtitle column
    val leftGroupWidth = 36.dp + 12.dp + leftTextWidth
    // chip = horizontal padding + icon + spacing + label
    val chipWithLabelWidth = 24.dp + 15.dp + 6.dp + chipLabelWidth
    val deleteButtonWidth = 36.dp
    val rightGap = 8.dp

    BoxWithConstraints(
        modifier =
            modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        val showChipLabel =
            leftGroupWidth + chipWithLabelWidth + rightGap + deleteButtonWidth <= maxWidth

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .background(
                                color = RandomBoxdColors.BackgroundColor,
                                shape = RoundedCornerShape(18.dp),
                            ).clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RandomBoxdColors.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = titleText,
                        color = RandomBoxdColors.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = subtitleText,
                        color = RandomBoxdColors.TextMuted,
                        fontSize = 13.sp,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(rightGap),
            ) {
                FavoritesFilterChip(
                    isActive = isFavoritesOnly,
                    onClick = onFavoritesClick,
                    showLabel = showChipLabel,
                )
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .background(
                                color = RandomBoxdColors.BackgroundColor,
                                shape = RoundedCornerShape(18.dp),
                            ).clickable(onClick = onClearClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Clear history",
                        tint = RandomBoxdColors.BackgroundLightColor,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}
