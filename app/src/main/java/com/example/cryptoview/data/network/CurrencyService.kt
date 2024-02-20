package com.example.cryptoview.data.network

import com.example.cryptoview.data.models.Currency
import com.example.cryptoview.utils.Constants
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface CurrencyService {
    @GET(Constants.BASE_LATEST_EXCHANGE_RATES_CURRENCY_URL)
    suspend fun getAllLatestRatesCurrencies(): Currency
}