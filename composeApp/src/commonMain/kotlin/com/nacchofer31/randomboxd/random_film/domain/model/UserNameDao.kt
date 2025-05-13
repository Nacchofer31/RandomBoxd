package com.nacchofer31.randomboxd.random_film.domain.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserNameDao {
    @Upsert
    suspend fun upsert(username: UserName)

    @Delete
    suspend fun deleteUserName(username: UserName)

    @Query("SELECT * FROM username")
    fun getAllUsernames(): Flow<List<UserName>>

    @Query("SELECT * FROM username WHERE username = :username LIMIT 1")
    suspend fun getUserNameByValue(username: String): UserName?
}
