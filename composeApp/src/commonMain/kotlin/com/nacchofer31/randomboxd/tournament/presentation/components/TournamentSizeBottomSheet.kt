package com.nacchofer31.randomboxd.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.tournament_info_16
import randomboxd.composeapp.generated.resources.tournament_info_2
import randomboxd.composeapp.generated.resources.tournament_info_32
import randomboxd.composeapp.generated.resources.tournament_info_4
import randomboxd.composeapp.generated.resources.tournament_info_8
import randomboxd.composeapp.generated.resources.tournament_not_enough_films
import randomboxd.composeapp.generated.resources.tournament_round_final
import randomboxd.composeapp.generated.resources.tournament_size_quarters
import randomboxd.composeapp.generated.resources.tournament_size_r16
import randomboxd.composeapp.generated.resources.tournament_size_r32
import randomboxd.composeapp.generated.resources.tournament_size_semis
import randomboxd.composeapp.generated.resources.tournament_size_title
import randomboxd.composeapp.generated.resources.tournament_start

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TournamentSizeBottomSheet(
    selectedSize: TournamentSize?,
    availableFilms: Int,
    onStart: (TournamentSize) -> Unit,
    onDismiss: () -> Unit,
) {
    var pendingSize by remember(selectedSize) { mutableStateOf(selectedSize ?: TournamentSize.EIGHT) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val canStart = availableFilms >= pendingSize.filmCount

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RandomBoxdColors.CardBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = RandomBoxdColors.TournamentOrange,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = stringResource(Res.string.tournament_size_title),
                color = RandomBoxdColors.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TournamentSize.entries.forEach { size ->
                TournamentSizeChip(
                    size = size,
                    isSelected = pendingSize == size,
                    onClick = { pendingSize = size },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
                    .background(
                        color = RandomBoxdColors.TextFieldBackgroundColor,
                        shape = RoundedCornerShape(12.dp),
                    ).padding(14.dp),
        ) {
            Text(
                text = infoForSize(pendingSize),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            if (availableFilms < pendingSize.filmCount) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.tournament_not_enough_films),
                    modifier = Modifier.testTag("test-tournament-not-enough-films"),
                    color = RandomBoxdColors.OrangeAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(56.dp)
                    .testTag("test-tournament-start"),
            onClick = { onStart(pendingSize) },
            enabled = canStart,
            color = if (canStart) RandomBoxdColors.GreenAccent else Color(0xff2a3a40),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (canStart) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.tournament_start),
                    color = if (canStart) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TournamentSizeChip(
    size: TournamentSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .height(64.dp)
                .then(
                    if (isSelected) {
                        Modifier.dropShadow(
                            shape = RoundedCornerShape(12.dp),
                            shadow =
                                Shadow(
                                    radius = 12.dp,
                                    color = RandomBoxdColors.GreenAccent.copy(alpha = 0.5f),
                                    spread = 2.dp,
                                ),
                        )
                    } else {
                        Modifier
                    },
                ).testTag("test-tournament-size-${size.filmCount}"),
        onClick = onClick,
        color = if (isSelected) RandomBoxdColors.GreenAccent else RandomBoxdColors.TextFieldBackgroundColor,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = size.filmCount.toString(),
                color = if (isSelected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                text = subLabelForSize(size),
                color = if (isSelected) RandomBoxdColors.BackgroundDarkColor else RandomBoxdColors.BackgroundLightColor,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun subLabelForSize(size: TournamentSize): String =
    stringResource(
        when (size) {
            TournamentSize.TWO -> Res.string.tournament_round_final
            TournamentSize.FOUR -> Res.string.tournament_size_semis
            TournamentSize.EIGHT -> Res.string.tournament_size_quarters
            TournamentSize.SIXTEEN -> Res.string.tournament_size_r16
            TournamentSize.THIRTY_TWO -> Res.string.tournament_size_r32
        },
    )

@Composable
private fun infoForSize(size: TournamentSize): String =
    stringResource(
        when (size) {
            TournamentSize.TWO -> Res.string.tournament_info_2
            TournamentSize.FOUR -> Res.string.tournament_info_4
            TournamentSize.EIGHT -> Res.string.tournament_info_8
            TournamentSize.SIXTEEN -> Res.string.tournament_info_16
            TournamentSize.THIRTY_TWO -> Res.string.tournament_info_32
        },
    )
