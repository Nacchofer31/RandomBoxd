package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.google_play_store_badge_en
import randomboxd.composeapp.generated.resources.onboarding_welcome_subtitle
import randomboxd.composeapp.generated.resources.random_boxd_logo
import randomboxd.composeapp.generated.resources.share_card_tagline

@Composable
fun ShareCard(
    film: Film,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors =
                            listOf(
                                RandomBoxdColors.BackgroundColor,
                                RandomBoxdColors.BackgroundDarkColor,
                            ),
                        center = Offset(0.5f, 0.35f),
                        radius = 1000f,
                    ),
                ).drawBehind {
                    drawDicePattern()
                },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.share_card_tagline),
                color = RandomBoxdColors.BackgroundLightColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(RandomBoxdColors.BackgroundColor),
                ) {
                    AsyncImage(
                        model = film.imageUrl,
                        contentDescription = film.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = film.name,
                        color = RandomBoxdColors.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = film.releaseYear?.toString() ?: "-",
                        color = RandomBoxdColors.BackgroundLightColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.random_boxd_logo),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "RandomBoxd",
                            color = RandomBoxdColors.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.onboarding_welcome_subtitle),
                        color = RandomBoxdColors.BackgroundLightColor,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.google_play_store_badge_en),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.42f).aspectRatio(180f / 53.333f),
                )
            }
        }
    }
}

private fun DrawScope.drawDicePattern() {
    val cell = 34.dp.toPx()
    val die = 20.dp.toPx()
    val pip = 1.5.dp.toPx()
    val gap = 4.dp.toPx()
    val accentColors =
        listOf(
            RandomBoxdColors.GreenAccent,
            RandomBoxdColors.OrangeAccent,
            RandomBoxdColors.BlueAccent,
        )
    var row = 0
    var y = -die
    while (y < size.height) {
        var x = -die + (if (row % 2 == 0) 0f else cell / 2)
        while (x < size.width) {
            val color = accentColors[(row + (x / cell).toInt()) % accentColors.size].copy(alpha = 0.05f)
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(die, die),
                cornerRadius = CornerRadius(die * 0.18f),
            )
            drawCircle(color, pip, Offset(x + gap, y + gap))
            drawCircle(color, pip, Offset(x + die - gap, y + gap))
            drawCircle(color, pip, Offset(x + gap, y + die - gap))
            drawCircle(color, pip, Offset(x + die - gap, y + die - gap))
            drawCircle(color, pip, Offset(x + die / 2, y + die / 2))
            x += cell
        }
        y += cell * 0.87f
        row++
    }
}
