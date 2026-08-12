package com.nacchofer31.randomboxd.history.data.repository_impl

import com.nacchofer31.randomboxd.history.data.mapper.toFilmPick
import com.nacchofer31.randomboxd.history.data.mapper.toGenresCsv
import com.nacchofer31.randomboxd.history.data.mapper.toSearchModeCsv
import com.nacchofer31.randomboxd.history.data.mapper.toUserNamesCsv
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryDao
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class FilmHistoryRepositoryImpl(
    private val dao: FilmHistoryDao,
    private val clock: Clock,
) : FilmHistoryRepository {
    override suspend fun save(
        film: Film,
        userNames: Set<String>,
        searchMode: FilmSearchMode,
        selectedGenres: Set<FilmGenre>,
    ) {
        val entry =
            FilmHistoryEntry(
                filmSlug = film.slug,
                filmName = film.name,
                posterUrl = film.imageUrl,
                releaseYear = film.releaseYear,
                userNames = userNames.toUserNamesCsv(),
                searchMode = searchMode.toSearchModeCsv(),
                selectedGenres = selectedGenres.toGenresCsv(),
                timestamp = clock.now().toEpochMilliseconds(),
                isFavorite = false,
            )
        dao.insert(entry)
    }

    override fun getAllPicks(): Flow<List<FilmPick>> = dao.getAllPicks().map { entries -> entries.map { it.toFilmPick() } }

    override suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean,
    ) {
        dao.updateFavorite(id, isFavorite)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
