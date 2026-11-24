package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize

/**
 * Cache key of the site wide popular lists listing. It cannot collide with a Letterboxd username (no `@` there),
 * so the picker can tell the shared listing apart from the lists of a specific user.
 */
internal const val POPULAR_LISTS_USER_KEY = "@popular"

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
        get() = if (isShowingPopularLists) "" else ParsedUserNameInput.from(userName).listQuery

    /** Whether the picker is currently showing the shared popular listing instead of a user's lists. */
    val isShowingPopularLists: Boolean
        get() = userListsForUser == POPULAR_LISTS_USER_KEY

    /**
     * Whether another page can be requested right now: the scraper reported more pages and no name filter is
     * active. Filtering is client side, so paginating while filtering would hammer Letterboxd for nothing.
     */
    val userListsCanLoadMore: Boolean
        get() = userListsHasMore && userListsQuery.isBlank()

    val filteredUserLists: List<UserList>
        get() =
            if (userListsQuery.isBlank()) {
                userLists
            } else {
                userLists.filter { it.name.contains(userListsQuery.trim(), ignoreCase = true) }
            }
}
