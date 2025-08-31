package com.nacchofer31.randomboxd.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nacchofer31.randomboxd.core.data.USERNAME_DATABASE_NAME
import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import platform.Foundation.NSHomeDirectory

fun getUserNameDatabase(): UsernameDatabase {
    val dbFile = NSHomeDirectory() + "/$USERNAME_DATABASE_NAME"
    return Room
        .databaseBuilder<UsernameDatabase>(
            name = dbFile,
            factory = { UsernameDatabase::class.instantiateImpl() },
        ).setDriver(BundledSQLiteDriver())
        .build()
}
