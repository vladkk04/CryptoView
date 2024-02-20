package com.example.cryptoview.data.local.preferences

import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.TypeOfCurrency
import kotlinx.coroutines.flow.Flow

interface CurrencyPreferencesService {
    suspend fun saveAllExchangeRatesToPreferences(rates: Map<String, Double>)
    fun getExchangeRateFromPreferences(currency: TypeOfCurrency): Flow<Resource<Double>>
    suspend fun getExchangeAllRatesFromPreferences(): Resource<HashMap<String, Double>>
}