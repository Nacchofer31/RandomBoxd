package com.nacchofer31.randomboxd.history.domain.repository

import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlinx.coroutines.flow.Flow

interface FilmHistoryRepository {
    suspend fun save(
        film: Film,
        userNames: Set<String>,
        searchMode: FilmSearchMode,
        selectedGenres: Set<FilmGenre>,
    )

    fun getAllPicks(): Flow<List<FilmPick>>

    suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean,
    )

    suspend fun deleteAll()
}
