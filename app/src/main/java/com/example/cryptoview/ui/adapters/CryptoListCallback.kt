package com.example.cryptoview.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.cryptoview.data.models.Price

class CryptoListCallback : DiffUtil.ItemCallback<Price>() {
    override fun areItemsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Price, newItem: Price): Any? {
        return when {
            oldItem.lastPrice != newItem.lastPrice -> ArticleChangePayload.Price(newItem.lastPrice)
            else -> null
        }
    }

    sealed interface ArticleChangePayload {

        data class Price(val price: String) : ArticleChangePayload

        data class Bookmark(val bookmarked: Boolean) : ArticleChangePayload
    }
}