package com.nacchofer31.randomboxd.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryDao
import com.nacchofer31.randomboxd.history.domain.model.FilmHistoryEntry
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.model.UserNameDao

// Physical file kept as "usernames.db" — renaming would orphan existing installs' data
const val DATABASE_NAME = "usernames.db"

@Database(
    entities = [
        UserName::class,
        FilmHistoryEntry::class,
    ],
    version = 2,
    exportSchema = true,
)
@ConstructedBy(RandomBoxdDatabaseConstructor::class)
abstract class RandomBoxdDatabase : RoomDatabase() {
    abstract fun userNameDao(): UserNameDao

    abstract fun filmHistoryDao(): FilmHistoryDao
}

@Suppress("KotlinNoActualForExpect")
expect object RandomBoxdDatabaseConstructor : RoomDatabaseConstructor<RandomBoxdDatabase> {
    override fun initialize(): RandomBoxdDatabase
}
