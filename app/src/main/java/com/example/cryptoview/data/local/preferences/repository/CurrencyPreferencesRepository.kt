package com.example.cryptoview.data.local.preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.cryptoview.data.local.preferences.CurrencyPreferencesService
import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.TypeOfCurrency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CurrencyPreferencesRepository @Inject constructor(
    private val currencyDataStorePreferences: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
): CurrencyPreferencesService {

    override suspend fun saveAllExchangeRatesToPreferences(rates: Map<String, Double>) {
        rates.forEach { (currency, rate) ->
            currencyDataStorePreferences.edit { preferences ->
                preferences[doublePreferencesKey(currency)] = rate
            }
        }
    }
    override fun getExchangeRateFromPreferences(currency: TypeOfCurrency) = currencyDataStorePreferences.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[doublePreferencesKey(currency.name)]
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("Preferences for ${currency.name} do not exist")
            }
        }.flowOn(dispatcher)

    override suspend fun getExchangeAllRatesFromPreferences(): Resource<HashMap<String, Double>> {
        TODO("Not yet implemented")
    }
}