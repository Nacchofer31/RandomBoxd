package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

sealed interface RandomFilmAction {
    data class OnSubmitButtonClick(val userName: String): RandomFilmAction
}