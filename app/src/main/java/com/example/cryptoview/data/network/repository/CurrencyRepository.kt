package com.example.cryptoview.data.network.repository

import com.example.cryptoview.data.models.Currency
import com.example.cryptoview.data.network.CurrencyService
import com.example.cryptoview.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyService: CurrencyService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getAllLatestRatesCurrencies(): Resource<Currency> {
        return try {
            val response = currencyService.getAllLatestRatesCurrencies()
            Resource.Success(response)
        } catch (e: IOException) {
            Resource.Error("No Internet Connection")
        } catch (e: HttpException) {
            Resource.Error("Http Error")
        } catch (e: Exception) {
            Resource.Error("Exception")
        }
    }
}