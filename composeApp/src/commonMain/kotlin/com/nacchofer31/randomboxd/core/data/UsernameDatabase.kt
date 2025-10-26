package com.nacchofer31.randomboxd.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.model.UserNameDao

const val USERNAME_DATABASE_NAME = "usernames.db"

@Database(entities = [UserName::class], version = 1)
@ConstructedBy(UserNameDatabaseConstructor::class)
abstract class UsernameDatabase : RoomDatabase() {
    abstract fun userNameDao(): UserNameDao
}

@Suppress("KotlinNoActualForExpect")
expect object UserNameDatabaseConstructor : RoomDatabaseConstructor<UsernameDatabase> {
    override fun initialize(): UsernameDatabase
}
