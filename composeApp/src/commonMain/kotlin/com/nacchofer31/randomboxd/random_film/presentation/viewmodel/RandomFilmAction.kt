package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film

sealed interface RandomFilmAction {
    data class OnFilmClicked(
        val film: Film,
    ) : RandomFilmAction

    data class OnUserNameChanged(
        val username: String,
    ) : RandomFilmAction

    data object OnSubmitButtonClick : RandomFilmAction

    data object OnClearButtonClick : RandomFilmAction
}
