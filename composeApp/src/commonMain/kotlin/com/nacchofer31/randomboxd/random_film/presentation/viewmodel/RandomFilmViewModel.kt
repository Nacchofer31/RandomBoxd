package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.model.UserName
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlinx.coroutines.launch

@Suppress("OPT_IN_USAGE")
class RandomFilmViewModel(
    private val repository: RandomFilmRepository,
    private val userNameRepository: UserNameRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {
    private val actions = MutableSharedFlow<RandomFilmAction>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

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

    init {
        actions
            .filterIsInstance<RandomFilmAction.OnSubmitButtonClick>()
            .flatMapLatest {
                flow {
                    val result =
                        when {
                            !it.singleSearch -> {
                                repository.getRandomMoviesFromSearchList(
                                    internalState.value.userNameSearchList,
                                    internalState.value.filmSearchMode,
                                )
                            }

                            else -> {
                                val userName = internalState.value.userName.trim()
                                userNameRepository.addUserName(userName)
                                repository.getRandomMovie(userName)
                            }
                        }
                    emit(result)
                }.onStart {
                    internalState.update { state ->
                        state.copy(isLoading = true, resultError = null, resultFilm = null)
                    }
                }.flowOn(dispatchers.main)
            }.onEach { result ->
                internalState.update { current ->
                    when (result) {
                        is ResultData.Success -> {
                            current.copy(
                                isLoading = false,
                                resultFilm = result.data,
                                resultError = null,
                            )
                        }

                        is ResultData.Error -> {
                            current.copy(
                                isLoading = false,
                                resultFilm = null,
                                resultError = result.error,
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
            }

            is RandomFilmAction.OnRemoveUserName -> {
                viewModelScope.launch {
                    if (internalState.value.userNameSearchList.contains(action.userName.username)) {
                        addOrRemoveUserNameToList(action.userName.username)
                    }
                    userNameRepository.deleteUserName(action.userName)
                }
            }

            is RandomFilmAction.OnUserNameAdded -> {
                viewModelScope.launch { userNameRepository.addUserName(action.username.trim()) }
            }

            is RandomFilmAction.OnAddOrRemoveUserNameSearchList -> {
                addOrRemoveUserNameToList(action.userName.trim())
            }

            is RandomFilmAction.OnFilmSearchModeToggle -> {
                onFilmSearchModeToggle()
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

    private fun addOrRemoveUserNameToList(userName: String) =
        internalState.update { current ->
            if (current.userNameSearchList.contains(userName)) {
                current.copy(userNameSearchList = current.userNameSearchList - userName, userName = "", resultError = null)
            } else {
                current.copy(userNameSearchList = current.userNameSearchList + userName, userName = "", resultError = null)
            }
        }
}
