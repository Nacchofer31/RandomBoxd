package com.nacchofer31.randomboxd.random_film.domain.repository

import androidx.compose.ui.graphics.ImageBitmap

interface ShareRepository {
    suspend fun shareImage(
        image: ImageBitmap,
        fileName: String,
    )
}
