package com.nacchofer31.randomboxd.history.presentation.viewmodel

data class HistoryState(
    val showClearConfirmDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isFavoritesOnly: Boolean = false,
)
