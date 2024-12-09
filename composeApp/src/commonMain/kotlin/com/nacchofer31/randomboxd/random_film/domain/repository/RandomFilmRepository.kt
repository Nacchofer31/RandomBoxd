package com.nacchofer31.randomboxd.random_film.domain.repository

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.Result
import com.nacchofer31.randomboxd.random_film.domain.model.Film

interface RandomFilmRepository {
    suspend fun getRandomMovie(userName : String): Result<Film,DataError.Remote>
}