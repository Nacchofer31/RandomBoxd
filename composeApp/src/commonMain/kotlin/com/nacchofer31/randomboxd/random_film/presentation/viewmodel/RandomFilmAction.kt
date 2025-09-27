package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.UserName

sealed interface RandomFilmAction {
    data class OnFilmClicked(
        val film: Film,
    ) : RandomFilmAction

    data class OnUserNameChanged(
        val username: String,
    ) : RandomFilmAction

    data class OnUserNameAdded(
        val username: String,
    ) : RandomFilmAction

    data class OnAddOrRemoveUserNameSearchList(
        val userName: String,
    ) : RandomFilmAction

    data class OnRemoveUserName(
        val userName: UserName,
    ) : RandomFilmAction

    data class OnSubmitButtonClick(
        val singleSearch: Boolean = true,
    ) : RandomFilmAction

    data object OnClearButtonClick : RandomFilmAction

    data object OnFilmSearchModeToggle : RandomFilmAction
}
