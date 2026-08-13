package com.nacchofer31.randomboxd.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.history.domain.model.FilmPick
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(
    private val repository: FilmHistoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState(isLoading = true))
    val state: StateFlow<HistoryState> = _state.asStateFlow()
    lateinit var historyPicks: StateFlow<List<FilmPick>>
    lateinit var visiblePicks: StateFlow<List<FilmPick>>

    init {
        collectUserPicks()
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.ToggleFavorite -> toggleFavorite(action.pickId)
            is HistoryAction.ClearAll -> _state.update { it.copy(showClearConfirmDialog = true) }
            is HistoryAction.ConfirmClearAll -> confirmClearAll()
            is HistoryAction.DismissClearDialog -> _state.update { it.copy(showClearConfirmDialog = false) }
            is HistoryAction.ToggleFavoritesOnly -> toggleFavoritesOnly()
        }
    }

    private fun toggleFavorite(pickId: Int) {
        val pick = historyPicks.value.find { it.id == pickId } ?: return
        viewModelScope.launch {
            repository.updateFavorite(pickId, !pick.isFavorite)
        }
    }

    private fun toggleFavoritesOnly() {
        _state.update { it.copy(isFavoritesOnly = !it.isFavoritesOnly) }
    }

    private fun confirmClearAll() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteAll()
            }
            _state.update { it.copy(showClearConfirmDialog = false) }
        }

    private fun collectUserPicks() =
        viewModelScope.launch {
            val allPicks =
                repository
                    .getAllPicks()
                    .onEach { _state.update { it.copy(isLoading = false) } }
                    .stateIn(
                        viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000L),
                        initialValue = emptyList(),
                    )
            historyPicks = allPicks
            visiblePicks =
                combine(_state.map { it.isFavoritesOnly }, allPicks) { favoritesOnly, picks ->
                    if (favoritesOnly) picks.filter { it.isFavorite } else picks
                }.stateIn(
                    viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = emptyList(),
                )
        }
}
