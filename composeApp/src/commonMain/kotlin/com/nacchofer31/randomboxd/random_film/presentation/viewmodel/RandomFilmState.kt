package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize

data class RandomFilmState(
    val userName: String = "",
    val resultFilm: Film? = null,
    val resultError: DataError.Remote? = null,
    val isLoading: Boolean = false,
    val userNameSearchList: Set<String> = mutableSetOf(),
    val filmSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION,
    val selectedGenres: Set<FilmGenre> = emptySet(),
    val showGenreBottomSheet: Boolean = false,
    val showTournamentBottomSheet: Boolean = false,
    val tournamentSize: TournamentSize? = null,
    val numberOfResults: Int = 0,
    val userLists: List<UserList> = emptyList(),
    val userListsForUser: String = "",
    val isLoadingUserLists: Boolean = false,
    val userListsError: DataError.Remote? = null,
    val userListsDropdownVisible: Boolean = false,
    /** Last lists page loaded for [userListsForUser]; page 1 is the initial scrape. */
    val userListsPage: Int = 1,
    /** Whether Letterboxd reported another lists page after [userListsPage]. */
    val userListsHasMore: Boolean = false,
    val isLoadingMoreUserLists: Boolean = false,
    val userListsLoadMoreError: Boolean = false,
    /**
     * Text last written into the field by the ViewModel itself (chip tap or list pick). [onUserNameChangedForLists]
     * ignores it so the programmatic write, which loops back through the textfield, does not open the list picker.
     */
    val programmaticFillValue: String = "",
) {
    /** Text typed after the list user, used to filter the scrapped lists. */
    val userListsQuery: String
        get() = ParsedUserNameInput.from(userName).listQuery

    val filteredUserLists: List<UserList>
        get() =
            if (userListsQuery.isBlank()) {
                userLists
            } else {
                userLists.filter { it.name.contains(userListsQuery.trim(), ignoreCase = true) }
            }
}
