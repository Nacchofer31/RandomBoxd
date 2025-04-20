package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors

@Composable
fun FilmPoster(
    imageUrl: String,
    onClick: () -> Unit,
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult = if (size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )

    AnimatedContent(
        targetState = imageLoadResult
    ) { result ->
        when (result) {
            null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {

            }

            else -> {
                Image(
                    painter = painter,
                    contentDescription = "film_image",
                    modifier = Modifier
                        .width(200.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(15.dp),
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.7f)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .border(4.dp, RandomBoxdColors.White, RoundedCornerShape(15.dp))
                        .clickable { onClick() },
                    contentScale = if (result.isSuccess) {
                        ContentScale.Crop
                    } else {
                        ContentScale.Fit
                    }
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "info_icon",
                    tint = RandomBoxdColors.White,
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .clickable { onClick() }
                )
            }
        }
    }
}
