package com.nacchofer31.randomboxd.core.data

object RandomBoxdEndpoints {
    fun getUserRandomFilm(userName: String): String = "/api?users=$userName"
}
