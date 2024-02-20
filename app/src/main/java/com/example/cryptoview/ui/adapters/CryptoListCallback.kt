package com.example.cryptoview.ui.adapters

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.cryptoview.data.models.Price
import java.math.BigDecimal

class CryptoListCallback : DiffUtil.ItemCallback<Price>() {
    override fun areItemsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem.symbol == newItem.symbol && oldItem.lastPrice != newItem.lastPrice
    }

    override fun areContentsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Price, newItem: Price): Any? {
        return when {
            (oldItem.isFavorite != newItem.isFavorite) -> ArticleChangePayload.Favorite(newItem.isFavorite)
            (oldItem.lastPrice != newItem.lastPrice) -> ArticleChangePayload.Price(newItem.lastPrice)
            else -> null
        }
    }

    sealed interface ArticleChangePayload {
        data class Price(val price: BigDecimal) : ArticleChangePayload

        data class Favorite(val isFavorite: Boolean) : ArticleChangePayload
    }
}