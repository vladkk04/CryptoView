package com.example.cryptoview.data.models

import com.example.cryptoview.ui.states.HomeScreenSortState.SortBy
import com.example.cryptoview.ui.states.HomeScreenSortState.SortState
import com.example.cryptoview.utils.TypeOfCurrency
import java.math.BigDecimal

data class Price(
    val symbol: String,
    val lastPrice: String
)

fun List<Price>.sortBy(sortBy: SortBy): List<Price> = when (sortBy) {
    SortBy.NAME -> {
        when (sortBy.sortState) {
            SortState.UP -> this.sortedBy { it.symbol }
            SortState.DOWN -> this.sortedByDescending { it.symbol }
            SortState.NONE -> this
        }
    }

    SortBy.PRICE -> {
        when (sortBy.sortState) {
            SortState.UP -> this.sortedBy { it.lastPrice.toDouble() }
            SortState.DOWN -> this.sortedByDescending { it.lastPrice.toDouble() }
            SortState.NONE -> this
        }
    }
}

fun List<Price>.filterCryptosToUSDT(): List<Price> = this.filter {
    it.symbol.contains("USDT")
}

fun List<Price>.filterToExistCryptos(): List<Price> = this.filter {
    it.lastPrice.toBigDecimal() > BigDecimal.ZERO
}

fun List<Price>.filterNormalizeCryptoName(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD): List<Price> = this.map { item ->
    when (typeOfCurrency) {
        TypeOfCurrency.USD -> { item.copy(symbol = item.symbol.removeSuffix("USDT")) }
        else -> { item.copy(symbol = item.symbol.removeSuffix(typeOfCurrency.name)) }
    }
}

fun List<Price>.filterNormalizePrices(): List<Price> = this.map { item ->
    val regex = Regex("(\\d+\\.\\d+[1-9](?![1-9]?!0))")
    val originalPrice = item.lastPrice.toBigDecimal()

    item.copy(lastPrice = regex.find(originalPrice.toPlainString())?.value ?: String.format("%.2f", originalPrice))
}



