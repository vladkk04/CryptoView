package com.example.cryptoview.data.network.repository

import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.filterCryptoNormalizeName
import com.example.cryptoview.data.models.filterCryptoNormalizePrices
import com.example.cryptoview.data.models.filterCryptosByUSDT
import com.example.cryptoview.data.models.filterCryptosByExistence
import com.example.cryptoview.data.network.CryptoService
import com.example.cryptoview.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PriceRepository @Inject constructor (
    private val cryptoService: CryptoService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getDailyCryptoStats(): Resource<List<Price>> = withContext(dispatcher) {
        try {
            val response = cryptoService.getDailyCryptoStats()
            val filteredResponse = response
                .filterCryptosByUSDT()
                .filterCryptosByExistence()
                .filterCryptoNormalizePrices()
                .filterCryptoNormalizeName()

            Resource.Success(filteredResponse)
        } catch (e: IOException) {
            Resource.Error("No Internet Connection")
        } catch (e: HttpException) {
            Resource.Error("Http Error")
        } catch (e: Exception) {
            Resource.Error("Exception")
        }
    }
}