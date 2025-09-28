package com.nacchofer31.randomboxd.core.data

object RandomBoxdEndpoints {
    fun getUserRandomFilm(userName: String): String = "/api?users=$userName"

    fun getUserNameWatchlist(userName: String): String = "https://letterboxd.com/$userName/watchlist"

    fun filmSlugUrl(slug: String): String = "https://letterboxd.com/film/$slug/"

    fun filmPosterUrl(
        segmentedId: String,
        filmId: String,
        slug: String,
    ) = "https://a.ltrbxd.com/resized/film-poster/$segmentedId$filmId-$slug-0-125-0-187-crop.jpg"
}
