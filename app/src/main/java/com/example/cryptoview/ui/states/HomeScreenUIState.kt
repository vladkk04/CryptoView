package com.example.cryptoview.ui.states

import com.example.cryptoview.data.models.Price

data class HomeScreenUIState(
    val isLoadingSource: LoadingSource = LoadingSource.NONE,
    val cryptos: List<Price> = emptyList(),
    val isSortByName: SortState = SortState.NONE,
    val isSortByPrice: SortState = SortState.NONE,
    val error: String ?= null,
) {

    enum class SortState {
        NONE,
        UP,
        DOWN
    }

    enum class SortBy(var sortState: SortState) {
        NAME(SortState.NONE),
        PRICE(SortState.NONE)
    }

    enum class LoadingSource {
        NONE,
        DAILY_STATS,
        EXCHANGE_RATE
    }
}