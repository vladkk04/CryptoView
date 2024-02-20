package com.example.cryptoview.data.models

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.ui.states.HomeScreenUIState.*
import com.example.cryptoview.utils.TypeOfCurrency
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Collections
import java.util.Deque


@Entity(tableName = "cryptos")
data class Price(
    @PrimaryKey val symbol: String,
    @ColumnInfo var lastPrice: BigDecimal,
    @ColumnInfo var isFavorite: Boolean
)

private val priceRegex = Regex("(\\d+\\.*\\d*[1-9])")

fun List<Price>.filterCryptosByUSDT(): List<Price> = this.filter {
    it.symbol.endsWith("USDT")
}

fun List<Price>.filterCryptosByExistence(): List<Price> = this.filter {
    it.lastPrice > BigDecimal.ZERO
}

fun List<Price>.filterCryptoNormalizeName(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD): List<Price> = this.map { item ->
    when (typeOfCurrency) {
        TypeOfCurrency.USD -> { item.copy(symbol = item.symbol.removeSuffix("USDT")) }
        else -> { item.copy(symbol = item.symbol.removeSuffix(typeOfCurrency.name)) }
    }
}

fun List<Price>.filterCryptoNormalizePrices(): List<Price> = this.map { item ->
    val resultFromRegex = priceRegex.find(item.lastPrice.toString())?.value
    item.copy(lastPrice = resultFromRegex?.toBigDecimal() ?: item.lastPrice)
}

fun List<Price>.roundCryptoPrices(): List<Price> = this.map { item ->
    val splitNumber = item.lastPrice.toString().split('.')
    val countZerosInDecimal = splitNumber.component2().takeWhile{ it == '0' }.count()

    val resultAfterScale = if (splitNumber.component1().length > 1) {
        item.lastPrice.setScale(2, RoundingMode.DOWN)
    } else {
        item.lastPrice.setScale(countZerosInDecimal + 3, RoundingMode.DOWN)
    }

    item.copy(lastPrice = resultAfterScale)
}






