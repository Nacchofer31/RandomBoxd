package com.nacchofer31.randomboxd.random_film.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.core.domain.onError
import com.nacchofer31.randomboxd.core.domain.onSuccess
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RandomFilmViewModel(private val repository: RandomFilmRepository): ViewModel() {

    private val _state = MutableStateFlow(RandomFilmState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )
    fun onAction(action: RandomFilmAction) {
        when(action){
            is RandomFilmAction.OnSubmitButtonClick ->{
                observeRandomFilm(action.userName)
            }
            else -> Unit
        }
    }

    private fun observeRandomFilm(userName: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        repository
            .getRandomMovie(userName)
            .onSuccess { film ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        resultFilm = film
                    )
                }
            }
            .onError {
                _state.update {
                    it.copy(
                        isLoading = false,
                        resultFilm = null
                    )
                }
            }
    }
}
