package com.example.cryptoview.ui.states

import com.example.cryptoview.data.models.Price
import com.example.cryptoview.utils.SortOrder
import com.example.cryptoview.utils.SortType

data class HomeScreenUIState(
    val sortOrder: SortOrder = SortOrder.NONE,
    val sortType: SortType = SortType.NONE,
    val cryptos: List<Price> = emptyList(),
    val isLoading: Boolean = true,
    val error: String ?= null,
)