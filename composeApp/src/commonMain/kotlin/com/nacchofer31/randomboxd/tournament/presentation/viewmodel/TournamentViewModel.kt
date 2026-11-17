package com.nacchofer31.randomboxd.tournament.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.core.domain.ResultData
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import com.nacchofer31.randomboxd.random_film.domain.model.FilmGenre
import com.nacchofer31.randomboxd.random_film.domain.model.FilmSearchMode
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.tournament.domain.model.Tournament
import com.nacchofer31.randomboxd.tournament.domain.model.TournamentSize
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

@Suppress("OPT_IN_USAGE")
class TournamentViewModel(
    private val size: TournamentSize,
    private val films: List<Film>,
    private val repository: RandomFilmRepository,
    private val dispatchers: DispatcherProvider,
    private val historyRepository: FilmHistoryRepository,
    private val userNames: Set<String>,
    private val searchMode: FilmSearchMode,
    private val genres: Set<FilmGenre>,
) : ViewModel() {
    private val internalState = MutableStateFlow(TournamentState())

    internal val state: StateFlow<TournamentState> =
        internalState
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                internalState.value,
            )

    private var loadedFilms: List<Film> = emptyList()

    init {
        if (films.size != size.filmCount || films.distinctBy { it.slug }.size != films.size) {
            internalState.value =
                TournamentState(
                    phase = TournamentPhase.ERROR,
                    error = DataError.Remote.SERIALIZATION,
                )
        } else {
            loadPosters()
        }
    }

    fun onAction(action: TournamentAction) {
        when (action) {
            is TournamentAction.OnFilmSelected -> selectFilm(action.matchIndex, action.film)
            TournamentAction.OnNextRoundClicked -> advanceRound()
            TournamentAction.OnNewTournamentClicked -> startNewTournament()
            TournamentAction.OnRetryClicked -> loadPosters()
        }
    }

    private fun loadPosters() {
        viewModelScope.launch(dispatchers.main) {
            internalState.value =
                TournamentState(
                    phase = TournamentPhase.LOADING,
                    loadingTotal = films.size,
                )

            val semaphore = Semaphore(MAX_PARALLEL_POSTER_EXTRACTIONS)
            val extractedFilms =
                withContext(dispatchers.io) {
                    films
                        .map { film ->
                            async {
                                semaphore.withPermit {
                                    val extractedFilm =
                                        when (val result = repository.extractResultMovie(film)) {
                                            is ResultData.Success -> result.data
                                            is ResultData.Error -> film
                                        }
                                    internalState.update { current ->
                                        current.copy(loadingProgress = current.loadingProgress + 1)
                                    }
                                    extractedFilm
                                }
                            }
                        }.awaitAll()
                }

            loadedFilms = extractedFilms
            val bracket = Tournament.create(size, extractedFilms.shuffled())
            internalState.update { current ->
                if (bracket == null) {
                    current.copy(
                        phase = TournamentPhase.ERROR,
                        tournament = null,
                        error = DataError.Remote.SERIALIZATION,
                    )
                } else {
                    current.copy(
                        phase = TournamentPhase.MATCH,
                        tournament = bracket,
                        error = null,
                    )
                }
            }
        }
    }

    private fun selectFilm(
        matchIndex: Int,
        film: Film,
    ) {
        val current = internalState.value
        val tournament = current.tournament
        if (current.phase != TournamentPhase.MATCH || tournament == null) return

        val updatedTournament =
            runCatching { tournament.selectWinner(matchIndex, film) }
                .getOrNull() ?: return
        val updatedPhase =
            when {
                updatedTournament.isFinished -> {
                    val champion = updatedTournament.champion
                    if (champion != null) {
                        viewModelScope.launch(dispatchers.io) {
                            runCatching {
                                historyRepository.save(
                                    film = champion,
                                    userNames = userNames,
                                    searchMode = searchMode,
                                    selectedGenres = genres,
                                )
                            }
                        }
                    }
                    TournamentPhase.CHAMPION
                }

                updatedTournament.isCurrentRoundComplete -> {
                    TournamentPhase.ROUND_SUMMARY
                }

                else -> {
                    TournamentPhase.MATCH
                }
            }
        internalState.update {
            it.copy(
                phase = updatedPhase,
                tournament = updatedTournament,
            )
        }
    }

    private fun advanceRound() {
        val current = internalState.value
        val tournament = current.tournament
        if (
            current.phase != TournamentPhase.ROUND_SUMMARY ||
            tournament == null ||
            !tournament.isCurrentRoundComplete ||
            tournament.isFinished
        ) {
            return
        }

        val updatedTournament = runCatching { tournament.advanceRound() }.getOrNull() ?: return
        internalState.update {
            it.copy(
                phase = TournamentPhase.MATCH,
                tournament = updatedTournament,
            )
        }
    }

    private fun startNewTournament() {
        val bracket = Tournament.create(size, loadedFilms.shuffled()) ?: return
        internalState.update {
            it.copy(
                phase = TournamentPhase.MATCH,
                tournament = bracket,
                error = null,
            )
        }
    }

    private companion object {
        const val MAX_PARALLEL_POSTER_EXTRACTIONS = 8
    }
}
