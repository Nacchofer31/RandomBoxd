package com.nacchofer31.randomboxd.database

import android.content.Context
import androidx.room.Room
import com.nacchofer31.randomboxd.core.data.USERNAME_DATABASE_NAME
import com.nacchofer31.randomboxd.core.data.UsernameDatabase

fun getUserNameDatabase(context: Context): UsernameDatabase {
    val dbFile = context.getDatabasePath(USERNAME_DATABASE_NAME)
    return Room
        .databaseBuilder<UsernameDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        ).build()
}
