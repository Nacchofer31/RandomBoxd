package com.nacchofer31.randomboxd.utils.fakes

import androidx.compose.ui.graphics.ImageBitmap
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryDao
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.ShareRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRandomFilmRepository : RandomFilmRepository {
    var getRandomMoviesResult: ResultData<Set<Film>, DataError.Remote> = ResultData.Success(emptySet())
    var getRandomMoviesFromSearchListResult: ResultData<Set<Film>, DataError.Remote> = ResultData.Success(emptySet())
    var extractResultMovieResult: ResultData<Film, DataError.Remote>? = null

    override suspend fun getRandomMovies(
        userName: String,
        selectedGenres: Set<FilmGenre>,
    ): ResultData<Set<Film>, DataError.Remote> = getRandomMoviesResult

    override suspend fun getRandomMoviesFromSearchList(
        searchList: Set<String>,
        filmSearchMode: FilmSearchMode,
        selectedGenres: Set<FilmGenre>,
    ): ResultData<Set<Film>, DataError.Remote> = getRandomMoviesFromSearchListResult

    override suspend fun extractResultMovie(
        film: Film,
    ): ResultData<Film, DataError.Remote> = extractResultMovieResult ?: ResultData.Success(film)
}

class FakeUserNameRepository : UserNameRepository {
    private val stored = mutableListOf<UserName>()

    override fun getAllUserNames(): Flow<List<UserName>> = flowOf(stored.toList())

    override suspend fun deleteUserName(userName: UserName) {
        stored.remove(userName)
    }

    override suspend fun addUserName(userName: String) {
        stored += UserName(id = stored.size + 1, username = userName)
    }
}

class FakeInAppReviewRepository : InAppReviewRepository {
    var requestCount = 0

    override suspend fun requestInAppReview() {
        requestCount++
    }
}

class FakeShareRepository : ShareRepository {
    override suspend fun shareImage(
        image: ImageBitmap,
        fileName: String,
    ) {}
}

class FakeFilmHistoryRepository : FilmHistoryRepository {
    var picks: List<FilmPick> = emptyList()
    var saveCount = 0
    val updateFavoriteCalls = mutableListOf<Pair<Int, Boolean>>()
    var deleteAllCount = 0

    override suspend fun save(
        film: Film,
        userNames: Set<String>,
        searchMode: FilmSearchMode,
        selectedGenres: Set<FilmGenre>,
    ) {
        saveCount++
    }

    override fun getAllPicks(): Flow<List<FilmPick>> = flowOf(picks)

    override suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean,
    ) {
        updateFavoriteCalls += id to isFavorite
    }

    override suspend fun deleteAll() {
        deleteAllCount++
    }
}

class FakeFilmHistoryDao : FilmHistoryDao {
    val insertedEntries = mutableListOf<FilmHistoryEntry>()
    var picks: List<FilmHistoryEntry> = emptyList()
    val updateFavoriteCalls = mutableListOf<Pair<Int, Boolean>>()
    var deleteAllCount = 0

    override suspend fun insert(entry: FilmHistoryEntry) {
        insertedEntries += entry
    }

    override fun getAllPicks(): Flow<List<FilmHistoryEntry>> = flowOf(picks)

    override suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean,
    ) {
        updateFavoriteCalls += id to isFavorite
    }

    override suspend fun deleteAll() {
        deleteAllCount++
    }
}
