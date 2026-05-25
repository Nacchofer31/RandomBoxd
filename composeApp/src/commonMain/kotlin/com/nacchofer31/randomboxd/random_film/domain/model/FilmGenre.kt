package com.nacchofer31.randomboxd.random_film.domain.model

import org.jetbrains.compose.resources.StringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.genre_action
import randomboxd.composeapp.generated.resources.genre_adventure
import randomboxd.composeapp.generated.resources.genre_animation
import randomboxd.composeapp.generated.resources.genre_comedy
import randomboxd.composeapp.generated.resources.genre_crime
import randomboxd.composeapp.generated.resources.genre_documentary
import randomboxd.composeapp.generated.resources.genre_drama
import randomboxd.composeapp.generated.resources.genre_family
import randomboxd.composeapp.generated.resources.genre_fantasy
import randomboxd.composeapp.generated.resources.genre_history
import randomboxd.composeapp.generated.resources.genre_horror
import randomboxd.composeapp.generated.resources.genre_music
import randomboxd.composeapp.generated.resources.genre_mystery
import randomboxd.composeapp.generated.resources.genre_romance
import randomboxd.composeapp.generated.resources.genre_science_fiction
import randomboxd.composeapp.generated.resources.genre_thriller
import randomboxd.composeapp.generated.resources.genre_tv_movie
import randomboxd.composeapp.generated.resources.genre_war
import randomboxd.composeapp.generated.resources.genre_western

enum class FilmGenre(
    val slug: String,
    val labelRes: StringResource,
) {
    ACTION("action", Res.string.genre_action),
    ADVENTURE("adventure", Res.string.genre_adventure),
    ANIMATION("animation", Res.string.genre_animation),
    COMEDY("comedy", Res.string.genre_comedy),
    CRIME("crime", Res.string.genre_crime),
    DOCUMENTARY("documentary", Res.string.genre_documentary),
    DRAMA("drama", Res.string.genre_drama),
    FAMILY("family", Res.string.genre_family),
    FANTASY("fantasy", Res.string.genre_fantasy),
    HISTORY("history", Res.string.genre_history),
    HORROR("horror", Res.string.genre_horror),
    MUSIC("music", Res.string.genre_music),
    MYSTERY("mystery", Res.string.genre_mystery),
    ROMANCE("romance", Res.string.genre_romance),
    SCIENCE_FICTION("science-fiction", Res.string.genre_science_fiction),
    THRILLER("thriller", Res.string.genre_thriller),
    TV_MOVIE("tv-movie", Res.string.genre_tv_movie),
    WAR("war", Res.string.genre_war),
    WESTERN("western", Res.string.genre_western),
}
