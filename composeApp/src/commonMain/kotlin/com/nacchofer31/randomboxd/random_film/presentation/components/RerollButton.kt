package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.reroll

@Composable
internal fun RerollButton(onClick: () -> Unit) =
    Surface(
        onClick = { onClick() },
        modifier =
            Modifier
                .padding(top = 5.dp)
                .dropShadow(
                    shape = RoundedCornerShape(100),
                    shadow =
                        Shadow(
                            radius = 10.dp,
                            color = RandomBoxdColors.GreenAccent.copy(alpha = 0.25f),
                            spread = 4.dp,
                        ),
                ),
        shape = RoundedCornerShape(100),
        color = RandomBoxdColors.GreenAccent,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = null,
                tint = RandomBoxdColors.BackgroundDarkColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(Res.string.reroll),
                color = RandomBoxdColors.BackgroundDarkColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
