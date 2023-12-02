package com.example.cryptoview.data.local

import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.TypeOfCurrency

interface CurrencyPreferencesService {
    suspend fun saveAllExchangeRatesToPreferences(rates: Map<String, Double>)
    suspend fun getExchangeRateFromPreferences(currency: TypeOfCurrency): Resource<Double>
    suspend fun getExchangeAllRatesFromPreferences(): Resource<HashMap<String, Double>>
}