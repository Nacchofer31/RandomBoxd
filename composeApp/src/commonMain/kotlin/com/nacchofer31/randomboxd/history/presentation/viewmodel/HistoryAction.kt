package com.nacchofer31.randomboxd.history.presentation.viewmodel

sealed interface HistoryAction {
    data class ToggleFavorite(
        val pickId: Int,
    ) : HistoryAction

    data object ClearAll : HistoryAction

    data object ConfirmClearAll : HistoryAction

    data object DismissClearDialog : HistoryAction

    data object ToggleFavoritesOnly : HistoryAction
}
