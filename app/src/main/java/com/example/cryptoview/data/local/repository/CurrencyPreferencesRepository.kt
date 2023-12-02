package com.example.cryptoview.data.local.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.cryptoview.data.di.CurrencyDataStore
import com.example.cryptoview.data.local.CurrencyPreferencesService
import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.TypeOfCurrency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CurrencyPreferencesRepository @Inject constructor(
    @CurrencyDataStore private val currencyDataStorePreferences: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
): CurrencyPreferencesService {

    override suspend fun saveAllExchangeRatesToPreferences(rates: Map<String, Double>) {
        rates.forEach { (currency, rate) ->
            currencyDataStorePreferences.edit { preferences ->
                preferences[doublePreferencesKey(currency)] = rate
            }
        }
    }

    override suspend fun getExchangeRateFromPreferences(currency: TypeOfCurrency): Resource<Double> {
        return try {
            currencyDataStorePreferences.data.flowOn(dispatcher)
                .map { preferences ->
                    val result = preferences[doublePreferencesKey(currency.name)]
                    if (result != null) {
                        Resource.Success(result)
                    } else {
                        Resource.Error("Preferences for ${currency.name} do not exist")
                    }
                }.first()
        } catch (e: IOException) {
            Resource.Error(e.message ?: "IO error occurred", null)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred", null)
        }
    }



    override suspend fun getExchangeAllRatesFromPreferences(): Resource<HashMap<String, Double>> {
        TODO("Not yet implemented")
    }


}