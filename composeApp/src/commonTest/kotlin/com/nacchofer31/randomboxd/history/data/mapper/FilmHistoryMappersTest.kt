package com.nacchofer31.randomboxd.history.data.mapper

import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FilmHistoryMappersTest {
    // ── CSV userNames ────────────────────────────────────────────────

    @Test
    fun `encode userNames to CSV`() {
        val result = setOf("alice", "bob").toUserNamesCsv()
        // Order of CSV is deterministic by iteration; both variants are valid
        assertTrue(result == "alice,bob" || result == "bob,alice")
    }

    @Test
    fun `encode empty userNames to empty string`() {
        val result = emptySet<String>().toUserNamesCsv()
        assertEquals("", result)
    }

    @Test
    fun `decode userNames from CSV`() {
        val result = "alice,bob".toUserNamesList()
        assertEquals(listOf("alice", "bob"), result)
    }

    @Test
    fun `decode empty CSV to empty list`() {
        assertEquals(emptyList(), "".toUserNamesList())
    }

    @Test
    fun `decode CSV with blanks trims and filters`() {
        val result = " alice , , bob ".toUserNamesList()
        assertEquals(listOf("alice", "bob"), result)
    }

    // ── CSV genres ───────────────────────────────────────────────────

    @Test
    fun `encode genres to CSV`() {
        val genres = setOf(FilmGenre.ACTION, FilmGenre.DRAMA)
        val result = genres.toGenresCsv()
        assertTrue(result.contains("ACTION"))
        assertTrue(result.contains("DRAMA"))
        assertTrue(result.contains(","))
    }

    @Test
    fun `encode empty genres to empty string`() {
        assertEquals("", emptySet<FilmGenre>().toGenresCsv())
    }

    @Test
    fun `decode genres from CSV`() {
        val result = "ACTION,DRAMA".toFilmGenresSet()
        assertEquals(setOf(FilmGenre.ACTION, FilmGenre.DRAMA), result)
    }

    @Test
    fun `decode unknown genre tokens are skipped`() {
        val result = "ACTION,UNKNOWN_GENRE,DRAMA".toFilmGenresSet()
        assertEquals(setOf(FilmGenre.ACTION, FilmGenre.DRAMA), result)
    }

    @Test
    fun `decode genres CSV with blanks trims and filters`() {
        val result = " ACTION , , DRAMA ".toFilmGenresSet()
        assertEquals(setOf(FilmGenre.ACTION, FilmGenre.DRAMA), result)
    }

    // ── CSV searchMode ───────────────────────────────────────────────

    @Test
    fun `encode searchMode to CSV`() {
        assertEquals("INTERSECTION", FilmSearchMode.INTERSECTION.toSearchModeCsv())
        assertEquals("UNION", FilmSearchMode.UNION.toSearchModeCsv())
    }

    @Test
    fun `decode searchMode from CSV`() {
        assertEquals(FilmSearchMode.INTERSECTION, "INTERSECTION".toFilmSearchMode())
        assertEquals(FilmSearchMode.UNION, "UNION".toFilmSearchMode())
    }

    @Test
    fun `decode unknown searchMode falls back to INTERSECTION`() {
        assertEquals(FilmSearchMode.INTERSECTION, "INVALID_MODE".toFilmSearchMode())
    }

    @Test
    fun `decode empty searchMode falls back to INTERSECTION`() {
        assertEquals(FilmSearchMode.INTERSECTION, "".toFilmSearchMode())
    }

    // ── Entity ↔ Domain round-trip ──────────────────────────────────

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000L)

    @Test
    fun `FilmHistoryEntry to FilmPick maps all fields`() {
        val entry =
            FilmHistoryEntry(
                id = 42,
                filmSlug = "the-matrix",
                filmName = "The Matrix",
                posterUrl = "https://example.com/poster.jpg",
                releaseYear = 1999,
                userNames = "alice,bob",
                searchMode = "INTERSECTION",
                selectedGenres = "ACTION,SCIENCE_FICTION",
                timestamp = now.toEpochMilliseconds(),
                isFavorite = true,
            )
        val pick = entry.toFilmPick()
        assertEquals(42, pick.id)
        assertEquals("the-matrix", pick.filmSlug)
        assertEquals("The Matrix", pick.filmName)
        assertEquals("https://example.com/poster.jpg", pick.posterUrl)
        assertEquals(1999, pick.releaseYear)
        assertEquals(listOf("alice", "bob"), pick.userNames)
        assertEquals(FilmSearchMode.INTERSECTION, pick.searchMode)
        assertEquals(setOf(FilmGenre.ACTION, FilmGenre.SCIENCE_FICTION), pick.selectedGenres)
        assertEquals(now, pick.timestamp)
        assertEquals(true, pick.isFavorite)
    }

    @Test
    fun `FilmHistoryEntry with null releaseYear maps correctly`() {
        val entry =
            FilmHistoryEntry(
                id = 1,
                filmSlug = "no-year",
                filmName = "No Year",
                posterUrl = "",
                releaseYear = null,
                userNames = "charlie",
                searchMode = "UNION",
                selectedGenres = "",
                timestamp = now.toEpochMilliseconds(),
                isFavorite = false,
            )
        val pick = entry.toFilmPick()
        assertEquals(null, pick.releaseYear)
        assertEquals(emptySet<FilmGenre>(), pick.selectedGenres)
    }

    @Test
    fun `round-trip entity to domain preserves data`() {
        val entry =
            FilmHistoryEntry(
                id = 7,
                filmSlug = "inception",
                filmName = "Inception",
                posterUrl = "https://img.example.com/inception.jpg",
                releaseYear = 2010,
                userNames = "dom,arthur",
                searchMode = "UNION",
                selectedGenres = "ACTION,THRILLER",
                timestamp = now.toEpochMilliseconds(),
                isFavorite = false,
            )
        val pick = entry.toFilmPick()

        assertEquals("inception", pick.filmSlug)
        assertEquals("Inception", pick.filmName)
        assertEquals(2010, pick.releaseYear)
        assertEquals(listOf("dom", "arthur"), pick.userNames)
        assertEquals(FilmSearchMode.UNION, pick.searchMode)
        assertEquals(setOf(FilmGenre.ACTION, FilmGenre.THRILLER), pick.selectedGenres)
        assertEquals(now, pick.timestamp)
    }
}
