package com.nacchofer31.randomboxd.history.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmHistoryDao {
    @Insert
    suspend fun insert(entry: FilmHistoryEntry)

    @Query("SELECT * FROM film_history_entry ORDER BY timestamp DESC")
    fun getAllPicks(): Flow<List<FilmHistoryEntry>>

    @Query("UPDATE film_history_entry SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean,
    )

    @Query("DELETE FROM film_history_entry")
    suspend fun deleteAll()
}
