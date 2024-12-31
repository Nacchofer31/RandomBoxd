package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film

sealed interface RandomFilmAction {
    data class OnSubmitButtonClick(val userName: String): RandomFilmAction
    data class OnFilmClicked(val film: Film): RandomFilmAction
    data object OnClearButtonClick: RandomFilmAction
}