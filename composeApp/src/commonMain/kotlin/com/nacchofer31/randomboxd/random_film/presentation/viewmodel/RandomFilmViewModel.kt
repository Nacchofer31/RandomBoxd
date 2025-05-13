package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Suppress("OPT_IN_USAGE")
class RandomFilmViewModel(
    private val repository: RandomFilmRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {
    private val actions = MutableSharedFlow<RandomFilmAction>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val internalState = MutableStateFlow(RandomFilmState())
    internal val state =
        internalState
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                internalState.value,
            )

    init {
        actions
            .filterIsInstance<RandomFilmAction.OnSubmitButtonClick>()
            .flatMapLatest {
                flow {
                    val result = repository.getRandomMovie(internalState.value.userName)
                    emit(result)
                }.onStart {
                    internalState.update { it.copy(isLoading = true) }
                }.flowOn(dispatchers.io)
            }.onEach { result ->
                internalState.update { current ->
                    when (result) {
                        is ResultData.Success ->
                            current.copy(
                                isLoading = false,
                                resultFilm = result.data,
                            )
                        is ResultData.Error ->
                            current.copy(
                                isLoading = false,
                                resultFilm = null,
                            )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: RandomFilmAction) {
        when (action) {
            is RandomFilmAction.OnSubmitButtonClick -> actions.tryEmit(action)
            is RandomFilmAction.OnClearButtonClick -> clearSelectedFilm()
            is RandomFilmAction.OnUserNameChanged -> updateUserNameValue(action.username)
            else -> Unit
        }
    }

    private fun updateUserNameValue(newValue: String) =
        internalState.update {
            it.copy(userName = newValue)
        }

    private fun clearSelectedFilm() =
        internalState.update {
            it.copy(resultFilm = null)
        }
}
