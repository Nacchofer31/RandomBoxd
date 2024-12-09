package com.nacchofer31.randomboxd.app

import kotlinx.serialization.Serializable

sealed interface RandomBoxdRoute {

    @Serializable
    data object Home: RandomBoxdRoute

    @Serializable
    data object RandomFilm: RandomBoxdRoute

}