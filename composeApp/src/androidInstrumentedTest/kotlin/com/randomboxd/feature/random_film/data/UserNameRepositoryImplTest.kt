package com.randomboxd.feature.random_film.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import com.nacchofer31.randomboxd.random_film.data.repository_impl.UserNameRepositoryImpl
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class UserNameRepositoryImplTest {
    private lateinit var database: UsernameDatabase
    private lateinit var repository: UserNameRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database =
            Room
                .inMemoryDatabaseBuilder(context, UsernameDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        repository = UserNameRepositoryImpl(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllUserNames_returns_empty_list_initially() =
        runBlocking {
            val userNames = repository.getAllUserNames().first()
            assertEquals(emptyList(), userNames)
        }

    @Test
    fun addUserName_adds_new_username() =
        runBlocking {
            repository.addUserName("newuser")

            val userNames = repository.getAllUserNames().first()

            assertEquals(1, userNames.size)
            assertEquals("newuser", userNames.first().username)
        }

    @Test
    fun addUserName_does_not_add_duplicate_username() =
        runBlocking {
            repository.addUserName("existinguser")
            repository.addUserName("existinguser")

            val userNames = repository.getAllUserNames().first()

            assertEquals(1, userNames.size)
        }

    @Test
    fun addUserName_adds_multiple_different_usernames() =
        runBlocking {
            repository.addUserName("user1")
            repository.addUserName("user2")
            repository.addUserName("user3")

            val userNames = repository.getAllUserNames().first()

            assertEquals(3, userNames.size)
        }

    @Test
    fun deleteUserName_removes_existing_username() =
        runBlocking {
            repository.addUserName("userToDelete")
            val userNameToDelete =
                database.userNameDao().getUserNameByValue("userToDelete")
                    ?: UserName(username = "userToDelete")

            repository.deleteUserName(userNameToDelete)

            val userNames = repository.getAllUserNames().first()
            assertTrue(userNames.isEmpty())
        }

    @Test
    fun addUserName_does_not_add_same_user_after_delete_duplicate_check() =
        runBlocking {
            repository.addUserName("user1")
            repository.addUserName("user2")
            repository.addUserName("user1")

            val userNames = repository.getAllUserNames().first()

            assertEquals(2, userNames.size)
        }
}
