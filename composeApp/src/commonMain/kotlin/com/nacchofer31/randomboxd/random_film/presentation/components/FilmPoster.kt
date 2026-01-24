package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.release_year_label

@Composable
fun FilmPoster(
    imageUrl: String,
    title: String,
    releaseYear: String,
    onClick: () -> Unit,
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter =
        rememberAsyncImagePainter(
            model = imageUrl,
            onSuccess = {
                val size = it.painter.intrinsicSize
                imageLoadResult =
                    if (size.width > 1 && size.height > 1) {
                        Result.success(it.painter)
                    } else {
                        Result.failure(Exception("Invalid image dimensions"))
                    }
            },
            onError = {
                it.result.throwable.printStackTrace()
            },
        )

    AnimatedContent(
        targetState = imageLoadResult,
    ) { result ->
        when (result) {
            null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clickable { onClick() }
                            .testTag("test-film-poster"),
                    contentAlignment = Alignment.Center,
                ) {
                }
            }

            else -> {
                Box(
                    modifier =
                        Modifier
                            .dropShadow(
                                shape = RoundedCornerShape(16.dp),
                                shadow =
                                    Shadow(
                                        radius = 16.dp,
                                        color = Color.Black.copy(alpha = 0.6f),
                                        spread = 4.dp,
                                    ),
                            ).background(
                                color = RandomBoxdColors.BackgroundColor,
                                shape = RoundedCornerShape(16.dp),
                            ).width(280.dp)
                            .testTag("test-film-poster"),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onClick() },
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "film_image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale =
                                    if (result.isSuccess) {
                                        ContentScale.Crop
                                    } else {
                                        ContentScale.Fit
                                    },
                            )
                            Box(
                                modifier =
                                    Modifier
                                        .padding(8.dp)
                                        .size(28.dp)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(14.dp),
                                        ).align(Alignment.TopStart)
                                        .clickable { onClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "info_icon",
                                    tint = RandomBoxdColors.White,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = title,
                                color = RandomBoxdColors.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "${stringResource(Res.string.release_year_label)} $releaseYear",
                                color = RandomBoxdColors.BackgroundLightColor,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
