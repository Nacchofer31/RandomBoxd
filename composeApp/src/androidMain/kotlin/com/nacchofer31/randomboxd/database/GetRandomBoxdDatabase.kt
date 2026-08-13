package com.nacchofer31.randomboxd.database

import android.content.Context
import androidx.room.Room
import com.nacchofer31.randomboxd.core.data.DATABASE_NAME
import com.nacchofer31.randomboxd.core.data.MIGRATION_1_2
import com.nacchofer31.randomboxd.core.data.RandomBoxdDatabase

fun getRandomBoxdDatabase(context: Context): RandomBoxdDatabase {
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room
        .databaseBuilder<RandomBoxdDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        ).addMigrations(MIGRATION_1_2)
        .build()
}
