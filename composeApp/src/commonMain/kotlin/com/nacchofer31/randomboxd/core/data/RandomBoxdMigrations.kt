package com.nacchofer31.randomboxd.core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS film_history_entry (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    filmSlug TEXT NOT NULL,
                    filmName TEXT NOT NULL,
                    posterUrl TEXT NOT NULL,
                    releaseYear INTEGER,
                    userNames TEXT NOT NULL,
                    searchMode TEXT NOT NULL,
                    selectedGenres TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    isFavorite INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }
