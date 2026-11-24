package com.nacchofer31.randomboxd.random_film.domain.model

data class UserList(
    val name: String,
    val slug: String,
    val filmCount: Int? = null,
    /**
     * Owner scrapped from the list card, only filled for the site wide popular listing: there the lists belong to
     * other users, so `owner/slug` is the identifier the film scraper understands.
     */
    val ownerUsername: String? = null,
)
