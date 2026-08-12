package com.nacchofer31.randomboxd.core.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class MigrationTest {
    private val testHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            RandomBoxdDatabase::class.java,
            emptyList(),
            FrameworkSQLiteOpenHelperFactory(),
        )

    private val testUserName = "testuser"

    @Test
    fun `v1 to v2 preserves usernames and creates film_history_entry`() {
        // Create v1 database
        val v1Db = testHelper.createDatabase(DATABASE_NAME, 1)
        v1Db.execSQL("INSERT INTO UserName (username) VALUES ('$testUserName')")
        v1Db.close()

        // Migrate to v2
        val v2Db =
            Room
                .databaseBuilder(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    RandomBoxdDatabase::class.java,
                    DATABASE_NAME,
                ).addMigrations(MIGRATION_1_2)
                .build()
        v2Db.openHelper.writableDatabase

        // Verify UserName survived
        val cursor = v2Db.query("SELECT COUNT(*) FROM UserName WHERE username = '$testUserName'", emptyArray())
        cursor.moveToFirst()
        assertEquals(1, cursor.getInt(0))
        cursor.close()

        // Verify film_history_entry table exists
        val tableCursor =
            v2Db.query(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='film_history_entry'",
                emptyArray(),
            )
        assertEquals(1, tableCursor.count)
        tableCursor.close()

        v2Db.close()
    }
}
