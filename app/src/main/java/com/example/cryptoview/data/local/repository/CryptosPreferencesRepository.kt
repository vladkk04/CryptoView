package com.example.cryptoview.data.local.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.cryptoview.data.di.CryptosDataStore
import com.example.cryptoview.data.local.CryptosPreferencesService
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.PrivateKey
import javax.inject.Inject

class CryptosPreferencesRepository @Inject constructor(
    @CryptosDataStore private val cryptosPreferencesDataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
): CryptosPreferencesService {

    override suspend fun saveAllCryptosToPreferences(price: List<Price>) {
        cryptosPreferencesDataStore.edit { preferences ->
            price.forEach { crypto ->
                preferences[stringPreferencesKey(crypto.symbol)] = crypto.lastPrice
            }
        }
    }
    override suspend fun getAllCryptosFromPreferences(): Resource<List<Price>> {
        return try {
            val prices = cryptosPreferencesDataStore.data.flowOn(dispatcher).map { preferences ->
                preferences.asMap().mapNotNull { entry ->
                    val symbol = entry.key.name
                    val lastPrice = entry.value.toString()
                    Price(symbol, lastPrice)
                }
            }.first()
            Resource.Success(emptyList())
        } catch (e: IOException) {
            Resource.Error(e.message ?: "IO error occurred", null)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred", null)
        }
    }

}