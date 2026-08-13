package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_favorites_filter

@Composable
fun FavoritesFilterChip(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .testTag("test-history-favorites-chip")
                .clip(RoundedCornerShape(14.dp))
                .background(
                    color =
                        if (isActive) {
                            RandomBoxdColors.GreenAccent
                        } else {
                            RandomBoxdColors.ElevatedBackgroundColor
                        },
                ).clickable(onClick = onClick)
                .padding(horizontal = if (showLabel) 12.dp else 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = if (isActive) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint =
                if (isActive) {
                    RandomBoxdColors.BackgroundDarkColor
                } else {
                    RandomBoxdColors.BackgroundLightColor
                },
            modifier = Modifier.size(20.dp),
        )
        if (showLabel) {
            Text(
                text = stringResource(Res.string.history_favorites_filter),
                color =
                    if (isActive) {
                        RandomBoxdColors.BackgroundDarkColor
                    } else {
                        RandomBoxdColors.BackgroundLightColor
                    },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
