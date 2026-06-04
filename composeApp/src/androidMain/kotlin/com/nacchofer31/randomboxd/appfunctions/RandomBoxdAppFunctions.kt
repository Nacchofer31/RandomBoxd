package com.nacchofer31.randomboxd.appfunctions

import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionLimitExceededException
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionStringValueConstraint
import androidx.appfunctions.service.AppFunction
import com.nacchofer31.randomboxd.core.data.RandomBoxdEndpoints
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository

/**
 * Genre slugs allowed in [RandomBoxdAppFunctions.PickRandomMovieParams.genres] and the
 * multi-user equivalent. Kept in sync with [FilmGenre] by the unit test
 * `genreSlugConstantsStayInSyncWithFilmGenre`.
 */
internal object GenreSlugs {
    const val ACTION = "action"
    const val ADVENTURE = "adventure"
    const val ANIMATION = "animation"
    const val COMEDY = "comedy"
    const val CRIME = "crime"
    const val DOCUMENTARY = "documentary"
    const val DRAMA = "drama"
    const val FAMILY = "family"
    const val FANTASY = "fantasy"
    const val HISTORY = "history"
    const val HORROR = "horror"
    const val MUSIC = "music"
    const val MYSTERY = "mystery"
    const val ROMANCE = "romance"
    const val SCIENCE_FICTION = "science-fiction"
    const val THRILLER = "thriller"
    const val TV_MOVIE = "tv-movie"
    const val WAR = "war"
    const val WESTERN = "western"
}

class RandomBoxdAppFunctions(
    private val repository: RandomFilmRepository,
) {
    /** A movie returned by a RandomBoxd AppFunction. */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class MovieResult(
        /** The movie title. */
        val title: String,
        /** Release year if known, null otherwise. */
        val year: Int?,
        /** Letterboxd URL for the film page, suitable for opening in the Letterboxd app or browser. */
        val letterboxdUrl: String,
        /** URL of the movie poster image. */
        val posterUrl: String,
    )

    /** Parameters for picking a random movie from a single user's watchlist. */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class PickRandomMovieParams(
        /** Letterboxd username whose watchlist will be used. Required. */
        val userName: String,
        /**
         * Optional genre slugs to filter by (e.g. "horror", "comedy", "science-fiction").
         * Unknown slugs are silently ignored. Leave empty or null to disable filtering.
         */
        @property:AppFunctionStringValueConstraint(
            enumValues = [
                GenreSlugs.ACTION, GenreSlugs.ADVENTURE, GenreSlugs.ANIMATION, GenreSlugs.COMEDY,
                GenreSlugs.CRIME, GenreSlugs.DOCUMENTARY, GenreSlugs.DRAMA, GenreSlugs.FAMILY,
                GenreSlugs.FANTASY, GenreSlugs.HISTORY, GenreSlugs.HORROR, GenreSlugs.MUSIC,
                GenreSlugs.MYSTERY, GenreSlugs.ROMANCE, GenreSlugs.SCIENCE_FICTION,
                GenreSlugs.THRILLER, GenreSlugs.TV_MOVIE, GenreSlugs.WAR, GenreSlugs.WESTERN,
            ],
        )
        val genres: List<String>? = null,
    )

    /** Parameters for picking a random movie shared between several Letterboxd users. */
    @AppFunctionSerializable(isDescribedByKDoc = true)
    data class PickRandomMovieFromUsersParams(
        /** Letterboxd usernames whose watchlists will be combined. At least one is required. */
        val userNames: List<String>,
        /**
         * How to combine the watchlists. Allowed values (case-insensitive):
         * - "INTERSECTION" (default when null): only pick from movies present in every user's watchlist.
         * - "UNION": pick from the combined pool of all users' watchlists.
         */
        @property:AppFunctionStringValueConstraint(enumValues = ["INTERSECTION", "UNION"])
        val mode: String? = null,
        /**
         * Optional genre slugs to filter by. See [PickRandomMovieParams.genres].
         */
        @property:AppFunctionStringValueConstraint(
            enumValues = [
                GenreSlugs.ACTION, GenreSlugs.ADVENTURE, GenreSlugs.ANIMATION, GenreSlugs.COMEDY,
                GenreSlugs.CRIME, GenreSlugs.DOCUMENTARY, GenreSlugs.DRAMA, GenreSlugs.FAMILY,
                GenreSlugs.FANTASY, GenreSlugs.HISTORY, GenreSlugs.HORROR, GenreSlugs.MUSIC,
                GenreSlugs.MYSTERY, GenreSlugs.ROMANCE, GenreSlugs.SCIENCE_FICTION,
                GenreSlugs.THRILLER, GenreSlugs.TV_MOVIE, GenreSlugs.WAR, GenreSlugs.WESTERN,
            ],
        )
        val genres: List<String>? = null,
    )

    /**
     * Picks a random movie from a Letterboxd user's watchlist, optionally filtered by genre.
     *
     * Use this when the user wants a movie suggestion from their own or another Letterboxd
     * user's watchlist. Returns the movie's title, release year, poster URL and a Letterboxd
     * link the user can open to read more or mark as watched.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun pickRandomMovie(
        appFunctionContext: AppFunctionContext,
        params: PickRandomMovieParams,
    ): MovieResult = pickRandomMovieInternal(params)

    internal suspend fun pickRandomMovieInternal(params: PickRandomMovieParams): MovieResult {
        if (params.userName.isBlank()) {
            throw AppFunctionInvalidArgumentException("userName must not be blank")
        }
        return repository
            .getRandomMovie(params.userName, parseGenres(params.genres))
            .toMovieResultOrThrow()
    }

    /**
     * Picks a random movie shared by several Letterboxd users.
     *
     * Use this when the user wants a movie that multiple Letterboxd users have on their
     * watchlists, either as the intersection (movies everyone has) or the union (combined
     * pool). Returns the same movie shape as [pickRandomMovie].
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun pickRandomMovieFromUsers(
        appFunctionContext: AppFunctionContext,
        params: PickRandomMovieFromUsersParams,
    ): MovieResult = pickRandomMovieFromUsersInternal(params)

    internal suspend fun pickRandomMovieFromUsersInternal(params: PickRandomMovieFromUsersParams): MovieResult {
        val cleanedNames =
            params.userNames
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        if (cleanedNames.isEmpty()) {
            throw AppFunctionInvalidArgumentException("userNames must contain at least one non-blank username")
        }
        return repository
            .getRandomMoviesFromSearchList(
                searchList = cleanedNames,
                filmSearchMode = parseMode(params.mode),
                selectedGenres = parseGenres(params.genres),
            ).toMovieResultOrThrow()
    }

    internal fun parseMode(raw: String?): FilmSearchMode =
        when (raw?.trim()?.uppercase()) {
            null, "", "INTERSECTION" -> FilmSearchMode.INTERSECTION

            "UNION" -> FilmSearchMode.UNION

            else -> throw AppFunctionInvalidArgumentException(
                "Unknown mode '$raw'. Use 'INTERSECTION' or 'UNION'.",
            )
        }

    internal fun parseGenres(slugs: List<String>?): Set<FilmGenre> =
        slugs
            .orEmpty()
            .mapNotNull { slug -> FilmGenre.entries.find { it.slug == slug } }
            .toSet()

    internal fun ResultData<Film, DataError.Remote>.toMovieResultOrThrow(): MovieResult =
        when (this) {
            is ResultData.Success -> {
                MovieResult(
                    title = data.name,
                    year = data.releaseYear,
                    letterboxdUrl = RandomBoxdEndpoints.filmSlugUrl(data.slug),
                    posterUrl = data.imageUrl,
                )
            }

            is ResultData.Error -> {
                throw error.toAppFunctionException()
            }
        }

    internal fun DataError.Remote.toAppFunctionException(): AppFunctionException =
        when (this) {
            DataError.Remote.NO_RESULTS -> {
                AppFunctionElementNotFoundException("No movies match the requested filters")
            }

            DataError.Remote.TOO_MANY_REQUESTS -> {
                AppFunctionLimitExceededException("Letterboxd rate limit reached")
            }

            DataError.Remote.REQUEST_TIMEOUT -> {
                AppFunctionAppUnknownException("Request to Letterboxd timed out")
            }

            DataError.Remote.NO_INTERNET -> {
                AppFunctionAppUnknownException("No internet connection available")
            }

            DataError.Remote.SERVER -> {
                AppFunctionAppUnknownException("Letterboxd server error")
            }

            DataError.Remote.SERIALIZATION -> {
                AppFunctionAppUnknownException("Failed to parse Letterboxd response")
            }

            DataError.Remote.UNKNOWN -> {
                AppFunctionAppUnknownException("Unknown error fetching random movie")
            }
        }
}
