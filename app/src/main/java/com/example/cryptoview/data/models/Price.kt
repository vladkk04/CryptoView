package com.example.cryptoview.data.models

import com.example.cryptoview.utils.TypeOfCurrency
import java.math.BigDecimal

data class Price(
    val symbol: String,
    val lastPrice: String
)

private val priceRegex = Regex("(\\d+\\.\\d+[1-9](?![1-9]?!0))")

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

fun List<Price>.filterCryptosByUSDT(): List<Price> = this.filter {
    it.symbol.endsWith("USDT")
}

fun List<Price>.filterCryptosByExistence(): List<Price> = this.filter {
    it.lastPrice.toBigDecimal() > BigDecimal.ZERO
}

fun List<Price>.filterCryptoNormalizeName(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD): List<Price> = this.map { item ->
    when (typeOfCurrency) {
        TypeOfCurrency.USD -> { item.copy(symbol = item.symbol.removeSuffix("USDT")) }
        else -> { item.copy(symbol = item.symbol.removeSuffix(typeOfCurrency.name)) }
    }
}

fun List<Price>.filterCryptoNormalizePrices(): List<Price> = this.map { item ->
    val originalPrice = item.lastPrice.toBigDecimal()
    item.copy(lastPrice = priceRegex.find(originalPrice.toPlainString())?.value ?: String.format("%.2f", originalPrice))
}



