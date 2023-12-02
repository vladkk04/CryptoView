package com.example.cryptoview.data.network.repository

import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.filterNormalizeCryptoName
import com.example.cryptoview.data.models.filterNormalizePrices
import com.example.cryptoview.data.models.filterCryptosToUSDT
import com.example.cryptoview.data.models.filterToExistCryptos
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
                .filterCryptosToUSDT()
                .filterToExistCryptos()
                .filterNormalizePrices()
                .filterNormalizeCryptoName()

            Resource.Success(filteredResponse)
        } catch (e: IOException) {
            Resource.Error("No Internet Connection")
        } catch (e: HttpException) {
            Resource.Error("Http Error")
        } catch (e: Exception) {
            Resource.Error("Exception")
        }
    }

    /*suspend fun getPrice(symbol: String) = cryptoService.getCryptoList(symbol)

    suspend fun getPrice(symbols: List<String>) = cryptoService.getCryptoList(symbols)*/
}