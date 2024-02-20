package com.example.cryptoview.ui.adapters

import com.example.cryptoview.data.models.Price

interface CryptoListOnClickListeners {
    fun onClick (position: Int, model: Price)
}