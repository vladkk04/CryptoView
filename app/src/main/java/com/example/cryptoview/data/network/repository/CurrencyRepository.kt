package com.example.cryptoview.data.network.repository

import androidx.datastore.preferences.core.emptyPreferences
import com.example.cryptoview.data.models.Currency
import com.example.cryptoview.data.network.CurrencyService
import com.example.cryptoview.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyService: CurrencyService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getAllLatestRatesCurrencies(): Flow<Resource<out Currency>> = flow {
        try {
            val result = currencyService.getAllLatestRatesCurrencies()
            emit(Resource.Success(result))
        } catch (exception: Exception) {
            emit(when (exception) {
                is IOException -> Resource.Error("No Internet Connection", null)
                is HttpException -> Resource.Error("Http Error", null)
                else -> Resource.Error("An unknown error occurred", null)
            })
        }
    }.flowOn(dispatcher)
}