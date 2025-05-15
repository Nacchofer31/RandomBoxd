package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import kotlinx.coroutines.flow.Flow

class UserNameRepositoryImpl(
    private val usernameDatabase: UsernameDatabase,
) : UserNameRepository {
    override fun getAllUserNames(): Flow<List<UserName>> =
        usernameDatabase.userNameDao().getAllUsernames()

    override suspend fun deleteUserName(userName: UserName) = usernameDatabase.userNameDao().deleteUserName(userName)

    override suspend fun addUserName(userName: String) {
        val existing = usernameDatabase.userNameDao().getUserNameByValue(userName)
        if (existing == null) {
            usernameDatabase.userNameDao().upsert(UserName(username = userName))
        }
    }
}
