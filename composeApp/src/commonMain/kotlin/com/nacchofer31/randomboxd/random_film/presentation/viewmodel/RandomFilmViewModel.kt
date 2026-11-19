package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.core.domain.randomExcluding
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserList
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.ShareRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("OPT_IN_USAGE")
class RandomFilmViewModel(
    private val repository: RandomFilmRepository,
    private val userNameRepository: UserNameRepository,
    private val dispatchers: DispatcherProvider,
    private val inAppReviewRepository: InAppReviewRepository,
    private val historyRepository: FilmHistoryRepository,
    private val shareRepository: ShareRepository,
) : ViewModel() {
    companion object {
        const val USER_LISTS_DEBOUNCE_MS = 300L
    }

    private val actions = MutableSharedFlow<RandomFilmAction>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val events = MutableSharedFlow<RandomFilmEvent>(extraBufferCapacity = 8)

    internal val eventFlow: SharedFlow<RandomFilmEvent> = events

    private val internalState = MutableStateFlow(RandomFilmState())

    internal val state: StateFlow<RandomFilmState> =
        internalState
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                internalState.value,
            )

    internal val userNameList: StateFlow<List<UserName>> =
        userNameRepository
            .getAllUserNames()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList(),
            )

    internal var cachedResultFilms: Set<Film> = emptySet()

    private var userListsJob: Job? = null

    // Snapshot search context captured at submit time for history save
    private var savedSearchUserNames: Set<String> = emptySet()
    private var savedSearchMode: FilmSearchMode = FilmSearchMode.INTERSECTION
    private var savedSearchGenres: Set<FilmGenre> = emptySet()

    init {
        actions
            .filterIsInstance<RandomFilmAction.OnSubmitButtonClick>()
            .flatMapLatest {
                flow {
                    // Snapshot search context at submit time (before extraction)
                    if (it.singleSearch) {
                        savedSearchUserNames = setOf(internalState.value.userName.trim())
                    } else {
                        savedSearchUserNames = internalState.value.userNameSearchList
                    }
                    savedSearchMode = internalState.value.filmSearchMode
                    savedSearchGenres = internalState.value.selectedGenres
                    val result =
                        when {
                            !it.singleSearch -> {
                                repository.getRandomMoviesFromSearchList(
                                    internalState.value.userNameSearchList,
                                    internalState.value.filmSearchMode,
                                    internalState.value.selectedGenres,
                                )
                            }

                            else -> {
                                val userName = internalState.value.userName.trim()
                                withContext(dispatchers.io) {
                                    userNameRepository.addUserName(userName)
                                }
                                repository.getRandomMovies(userName, internalState.value.selectedGenres)
                            }
                        }
                    emit(result)
                }.onStart {
                    internalState.update { state ->
                        state.copy(isLoading = true, resultError = null, resultFilm = null, numberOfResults = 0)
                    }
                }.flowOn(dispatchers.main)
            }.onEach { result ->
                internalState.update { current ->
                    when (result) {
                        is ResultData.Success -> {
                            viewModelScope.launch {
                                inAppReviewRepository.requestInAppReview()
                            }
                            cachedResultFilms = result.data
                            var filmResult = repository.extractResultMovie(result.data.randomExcluding(null) { it.name })
                            val extractedFilm = if (filmResult is ResultData.Success) filmResult.data else null
                            // Save history fire-and-forget — must never break pick UX
                            if (extractedFilm != null) {
                                viewModelScope.launch(dispatchers.io) {
                                    runCatching {
                                        historyRepository.save(
                                            film = extractedFilm,
                                            userNames = savedSearchUserNames,
                                            searchMode = savedSearchMode,
                                            selectedGenres = savedSearchGenres,
                                        )
                                    }
                                }
                            }
                            return@update when (filmResult) {
                                is ResultData.Success -> {
                                    current.copy(
                                        isLoading = false,
                                        resultFilm = filmResult.data,
                                        resultError = null,
                                        numberOfResults = cachedResultFilms.size,
                                    )
                                }

                                is ResultData.Error -> {
                                    current.copy(
                                        isLoading = false,
                                        resultFilm = null,
                                        resultError = filmResult.error,
                                        numberOfResults = 0,
                                    )
                                }
                            }
                        }

                        is ResultData.Error -> {
                            current.copy(
                                isLoading = false,
                                resultFilm = null,
                                resultError = result.error,
                                numberOfResults = 0,
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: RandomFilmAction) {
        when (action) {
            is RandomFilmAction.OnSubmitButtonClick -> {
                actions.tryEmit(action)
            }

            is RandomFilmAction.OnClearButtonClick -> {
                clearSelectedFilm()
            }

            is RandomFilmAction.OnInfoButtonClick -> {
                resetValues()
            }

            is RandomFilmAction.OnUserNameChanged -> {
                updateUserNameValue(action.username)
                onUserNameChangedForLists(action.username)
            }

            is RandomFilmAction.OnUserNameChipClicked -> {
                fillUserNameProgrammatically(action.username)
            }

            is RandomFilmAction.OnUserListSelected -> {
                onUserListSelected(action.list)
            }

            is RandomFilmAction.OnUserListsEndReached -> {
                onUserListsEndReached()
            }

            is RandomFilmAction.OnRemoveUserName -> {
                viewModelScope.launch {
                    if (internalState.value.userNameSearchList.contains(action.userName.username)) {
                        addOrRemoveUserNameToList(action.userName.username)
                    }
                    withContext(dispatchers.io) {
                        userNameRepository.deleteUserName(action.userName)
                    }
                }
            }

            is RandomFilmAction.OnUserNameAdded -> {
                viewModelScope.launch(dispatchers.io) { userNameRepository.addUserName(action.username.trim()) }
            }

            is RandomFilmAction.OnAddOrRemoveUserNameSearchList -> {
                addOrRemoveUserNameToList(action.userName.trim())
            }

            is RandomFilmAction.OnFilmSearchModeToggle -> {
                onFilmSearchModeToggle()
            }

            is RandomFilmAction.OnGenreToggled -> {
                toggleGenre(action.genre)
            }

            is RandomFilmAction.OnGenreAnySelected -> {
                internalState.update { it.copy(selectedGenres = emptySet()) }
            }

            is RandomFilmAction.OnGenreBottomSheetOpen -> {
                internalState.update { it.copy(showGenreBottomSheet = true) }
            }

            is RandomFilmAction.OnGenreBottomSheetDismiss -> {
                internalState.update { it.copy(showGenreBottomSheet = false) }
            }

            is RandomFilmAction.OnGenreSelectionApplied -> {
                internalState.update {
                    it.copy(
                        selectedGenres = action.genres,
                        showGenreBottomSheet = false,
                    )
                }
            }

            is RandomFilmAction.OnTournamentBottomSheetOpen -> {
                internalState.update { it.copy(showTournamentBottomSheet = true) }
            }

            is RandomFilmAction.OnTournamentBottomSheetDismiss -> {
                internalState.update { it.copy(showTournamentBottomSheet = false) }
            }

            is RandomFilmAction.OnTournamentStart -> {
                startTournament(action.size)
            }

            is RandomFilmAction.OnRerollClicked -> {
                rerollMovie()
            }

            else -> {
                Unit
            }
        }
    }

    private fun updateUserNameValue(newValue: String) =
        internalState.update {
            it.copy(userName = newValue, resultError = null)
        }

    private fun onUserNameChangedForLists(username: String) {
        val listUser = ParsedUserNameInput.from(username).listUser

        if (listUser == null) {
            internalState.update { it.copy(userListsDropdownVisible = false) }
            return
        }

        // Guard against the loop back caused by textfield writes originated in the ViewModel (chip tap, list pick).
        if (username == internalState.value.programmaticFillValue) {
            internalState.update { it.copy(userListsDropdownVisible = false) }
            return
        }

        if (listUser == internalState.value.userListsForUser) {
            internalState.update {
                it.copy(
                    userListsDropdownVisible = true,
                    isLoadingUserLists = false,
                    userListsError = null,
                    isLoadingMoreUserLists = false,
                    userListsLoadMoreError = false,
                )
            }
            return
        }

        userListsJob?.cancel()
        internalState.update {
            it.copy(
                userListsDropdownVisible = true,
                isLoadingUserLists = true,
                userLists = emptyList(),
                userListsError = null,
                userListsPage = 1,
                userListsHasMore = false,
                isLoadingMoreUserLists = false,
                userListsLoadMoreError = false,
            )
        }
        userListsJob =
            viewModelScope.launch(dispatchers.io) {
                delay(USER_LISTS_DEBOUNCE_MS)
                val result = repository.getUserLists(listUser, page = 1)
                // Drop results from a superseded lookup (cancelled or a different user was typed meanwhile).
                if (!isActive || ParsedUserNameInput.from(internalState.value.userName).listUser != listUser) {
                    return@launch
                }

                internalState.update { current ->
                    when (result) {
                        is ResultData.Success -> {
                            current.copy(
                                userLists = result.data.lists,
                                userListsForUser = listUser,
                                isLoadingUserLists = false,
                                userListsError = null,
                                userListsPage = 1,
                                userListsHasMore = result.data.hasMore,
                            )
                        }

                        is ResultData.Error -> {
                            current.copy(
                                userLists = emptyList(),
                                isLoadingUserLists = false,
                                userListsError = result.error,
                                userListsPage = 1,
                                userListsHasMore = false,
                            )
                        }
                    }
                }
            }
    }

    private fun onUserListsEndReached() {
        val state = internalState.value
        if (!state.userListsDropdownVisible ||
            !state.userListsHasMore ||
            state.isLoadingUserLists ||
            state.isLoadingMoreUserLists
        ) {
            return
        }

        // Same parser as the textfield, so a chip tap or a different user typed mid-flight invalidates the load.
        val listUser = ParsedUserNameInput.from(state.userName).listUser ?: return
        if (listUser != state.userListsForUser) return

        val nextPage = state.userListsPage + 1
        internalState.update {
            it.copy(isLoadingMoreUserLists = true, userListsLoadMoreError = false)
        }
        viewModelScope.launch(dispatchers.io) {
            val result = repository.getUserLists(listUser, nextPage)
            // Drop results of a page that no longer belongs to the current picker session (and release the
            // footer spinner so the next picker session starts clean).
            if (!isActive || ParsedUserNameInput.from(internalState.value.userName).listUser != listUser) {
                internalState.update { it.copy(isLoadingMoreUserLists = false) }
                return@launch
            }

            internalState.update { current ->
                when (result) {
                    is ResultData.Success -> {
                        current.copy(
                            userLists = (current.userLists + result.data.lists).distinctBy { it.slug },
                            userListsPage = nextPage,
                            // An empty page is treated as the end even if the paginator lied.
                            userListsHasMore = result.data.hasMore && result.data.lists.isNotEmpty(),
                            isLoadingMoreUserLists = false,
                            userListsLoadMoreError = false,
                        )
                    }

                    is ResultData.Error -> {
                        // Keep the already loaded lists and `hasMore` so the footer can be tapped to retry.
                        current.copy(
                            isLoadingMoreUserLists = false,
                            userListsLoadMoreError = true,
                        )
                    }
                }
            }
        }
    }

    private fun onUserListSelected(list: UserList) {
        val listUser = ParsedUserNameInput.from(internalState.value.userName).listUser ?: return

        val value = "$listUser/${list.slug}"
        fillUserNameProgrammatically(value)
        // Persisted like any manually typed username (the chip shows up), but the union/intersection search
        // list stays manual: including it is a long-press action, not a side effect of picking a list.
        viewModelScope.launch(dispatchers.io) { userNameRepository.addUserName(value) }
    }

    /**
     * Fills the textfield from the ViewModel (chip tap or list pick) and suppresses the list picker until the
     * text changes again, so the programmatic write looping back through the textfield stays silent.
     */
    private fun fillUserNameProgrammatically(userName: String) {
        updateUserNameValue(userName)
        internalState.update {
            it.copy(
                userListsDropdownVisible = false,
                programmaticFillValue = userName,
            )
        }
    }

    private fun clearSelectedFilm() =
        internalState.update {
            it.copy(resultError = null)
        }

    private fun resetValues() =
        internalState.update {
            it.copy(resultError = null, resultFilm = null)
        }

    private fun onFilmSearchModeToggle() =
        internalState.update {
            it.copy(filmSearchMode = if (it.filmSearchMode == FilmSearchMode.INTERSECTION) FilmSearchMode.UNION else FilmSearchMode.INTERSECTION, resultError = null)
        }

    private fun toggleGenre(genre: FilmGenre) =
        internalState.update { current ->
            val updated =
                if (current.selectedGenres.contains(genre)) {
                    current.selectedGenres - genre
                } else {
                    current.selectedGenres + genre
                }
            if (updated.size == FilmGenre.entries.size) {
                current.copy(selectedGenres = emptySet())
            } else {
                current.copy(selectedGenres = updated)
            }
        }

    private fun addOrRemoveUserNameToList(userName: String) =
        internalState.update { current ->
            if (current.userNameSearchList.contains(userName)) {
                current.copy(userNameSearchList = current.userNameSearchList - userName, userName = "", resultError = null)
            } else {
                current.copy(userNameSearchList = current.userNameSearchList + userName, userName = "", resultError = null)
            }
        }

    private fun startTournament(size: TournamentSize) {
        if (cachedResultFilms.size < size.filmCount) return

        val films = cachedResultFilms.shuffled().take(size.filmCount)
        internalState.update {
            it.copy(
                showTournamentBottomSheet = false,
                tournamentSize = size,
            )
        }
        events.tryEmit(
            RandomFilmEvent.TournamentStarted(
                size = size,
                films = films,
                userNames = savedSearchUserNames,
                searchMode = savedSearchMode,
                genres = savedSearchGenres,
            ),
        )
    }

    private fun rerollMovie() =
        viewModelScope.launch {
            internalState.update {
                it.copy(isLoading = true)
            }
            val rerolledFilm = cachedResultFilms.randomExcluding(internalState.value.resultFilm) { it.name }
            val filmResult =
                withContext(dispatchers.io) {
                    repository.extractResultMovie(rerolledFilm)
                }
            // Save history fire-and-forget on reroll — same snapshot from submit
            if (filmResult is ResultData.Success) {
                viewModelScope.launch(dispatchers.io) {
                    runCatching {
                        historyRepository.save(
                            film = filmResult.data,
                            userNames = savedSearchUserNames,
                            searchMode = savedSearchMode,
                            selectedGenres = savedSearchGenres,
                        )
                    }
                }
            }
            internalState.update {
                when (filmResult) {
                    is ResultData.Success -> it.copy(isLoading = false, resultFilm = filmResult.data, resultError = null, numberOfResults = cachedResultFilms.size)
                    is ResultData.Error -> it.copy(isLoading = false, resultFilm = null, resultError = filmResult.error, numberOfResults = 0)
                }
            }
        }

    fun shareImage(
        image: ImageBitmap,
        fileName: String,
    ) {
        viewModelScope.launch {
            shareRepository.shareImage(image, fileName)
        }
    }
}
