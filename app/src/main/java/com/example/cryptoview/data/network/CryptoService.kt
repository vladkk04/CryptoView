package com.example.cryptoview.data.network

import com.example.cryptoview.data.models.Price
import com.example.cryptoview.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Headers

interface CryptoService {
    @Headers(Constants.HEADER_API_KEY_BINANCE)
    @GET(Constants.BASE_TICKER_DAILY_URL)
    suspend fun getCryptos(): List<Price>
}