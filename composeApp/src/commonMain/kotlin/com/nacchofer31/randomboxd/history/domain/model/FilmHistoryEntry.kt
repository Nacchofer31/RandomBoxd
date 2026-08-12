package com.nacchofer31.randomboxd.history.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film_history_entry")
data class FilmHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "filmSlug") val filmSlug: String,
    @ColumnInfo(name = "filmName") val filmName: String,
    @ColumnInfo(name = "posterUrl") val posterUrl: String,
    @ColumnInfo(name = "releaseYear") val releaseYear: Int?,
    @ColumnInfo(name = "userNames") val userNames: String,
    @ColumnInfo(name = "searchMode") val searchMode: String,
    @ColumnInfo(name = "selectedGenres") val selectedGenres: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean = false,
)
