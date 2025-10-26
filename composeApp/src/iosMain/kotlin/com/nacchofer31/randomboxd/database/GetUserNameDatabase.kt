package com.nacchofer31.randomboxd.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nacchofer31.randomboxd.core.data.USERNAME_DATABASE_NAME
import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getUserNameDatabase(): UsernameDatabase {
    val dbFile = "${documentDirectory()}/$USERNAME_DATABASE_NAME"
    return Room
        .databaseBuilder<UsernameDatabase>(
            name = dbFile,
        ).setDriver(BundledSQLiteDriver())
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
