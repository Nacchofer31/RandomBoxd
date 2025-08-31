package com.nacchofer31.randomboxd.random_film.domain.repository

import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import kotlinx.coroutines.flow.Flow

interface UserNameRepository {
    fun getAllUserNames(): Flow<List<UserName>>

    suspend fun deleteUserName(userName: UserName)

    suspend fun addUserName(userName: String)
}
