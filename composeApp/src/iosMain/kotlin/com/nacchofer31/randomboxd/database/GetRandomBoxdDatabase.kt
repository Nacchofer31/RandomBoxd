package com.nacchofer31.randomboxd.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nacchofer31.randomboxd.core.data.DATABASE_NAME
import com.nacchofer31.randomboxd.core.data.MIGRATION_1_2
import com.nacchofer31.randomboxd.core.data.RandomBoxdDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getRandomBoxdDatabase(): RandomBoxdDatabase {
    val dbFile = "${documentDirectory()}/$DATABASE_NAME"
    return Room
        .databaseBuilder<RandomBoxdDatabase>(
            name = dbFile,
        ).setDriver(BundledSQLiteDriver())
        .addMigrations(MIGRATION_1_2)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(documentDirectory?.path)
}
