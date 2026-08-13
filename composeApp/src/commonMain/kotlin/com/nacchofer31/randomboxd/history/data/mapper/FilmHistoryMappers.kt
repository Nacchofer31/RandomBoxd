package com.nacchofer31.randomboxd.history.data.mapper

import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// ─── CSV Encoding ────────────────────────────────────────────────────

internal fun Set<String>.toUserNamesCsv(): String = this.joinToString(",")

internal fun Set<FilmGenre>.toGenresCsv(): String = this.joinToString(",") { it.name }

internal fun FilmSearchMode.toSearchModeCsv(): String = this.name

// ─── CSV Decoding ────────────────────────────────────────────────────

internal fun String.toUserNamesList(): List<String> =
    this
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }

internal fun String.toFilmGenresSet(): Set<FilmGenre> =
    this
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .mapNotNull { token ->
            try {
                FilmGenre.valueOf(token)
            } catch (_: IllegalArgumentException) {
                null
            }
        }.toSet()

internal fun String.toFilmSearchMode(): FilmSearchMode =
    try {
        FilmSearchMode.valueOf(this.trim())
    } catch (_: IllegalArgumentException) {
        FilmSearchMode.INTERSECTION
    }

// ─── Entity ↔ Domain ─────────────────────────────────────────────────

@OptIn(ExperimentalTime::class)
internal fun FilmHistoryEntry.toFilmPick(): FilmPick =
    FilmPick(
        id = id,
        filmSlug = filmSlug,
        filmName = filmName,
        posterUrl = posterUrl,
        releaseYear = releaseYear,
        userNames = userNames.toUserNamesList(),
        searchMode = searchMode.toFilmSearchMode(),
        selectedGenres = selectedGenres.toFilmGenresSet(),
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        isFavorite = isFavorite,
    )
