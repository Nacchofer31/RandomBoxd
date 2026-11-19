package com.nacchofer31.randomboxd.random_film.domain.model

data class UserListsPage(
    val lists: List<UserList>,
    val hasMore: Boolean,
)
