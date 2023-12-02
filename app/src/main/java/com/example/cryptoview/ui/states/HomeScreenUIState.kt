package com.example.cryptoview.ui.states

import com.example.cryptoview.data.models.Price

data class HomeScreenUIState(
    val isLoadingSource: LoadingSource = LoadingSource.NONE,
    val cryptos: List<Price> = emptyList(),
    val exchangeRate: Double ?= null,
    val error: String ?= null,
) {
    enum class LoadingSource {
        NONE,
        DAILY_STATS,
        EXCHANGE_RATE
    }
}