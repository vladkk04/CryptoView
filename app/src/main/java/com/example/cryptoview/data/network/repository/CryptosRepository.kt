package com.example.cryptoview.data.network.repository

import com.example.cryptoview.data.local.room.dao.CryptoDao
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.filterCryptoNormalizeName
import com.example.cryptoview.data.models.filterCryptoNormalizePrices
import com.example.cryptoview.data.models.filterCryptosByUSDT
import com.example.cryptoview.data.models.filterCryptosByExistence
import com.example.cryptoview.data.models.roundCryptoPrices
import com.example.cryptoview.data.network.CryptoService
import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.SortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject

class CryptosRepository @Inject constructor(
    private val remoteDataSourceService: CryptoService,
    private val localDataSourceService: CryptoDao,
    private val dispatcher: CoroutineDispatcher
) {
    fun getCryptosFromDatabase(): Flow<List<Price>> = localDataSourceService.getCryptos().flowOn(dispatcher)
    suspend fun getRemoteCryptos(): Resource<List<Price>> = withContext(dispatcher) {
        return@withContext try {
            val response = remoteDataSourceService.getCryptos()

            val filteredResponse = response
                .filterCryptosByUSDT()
                .filterCryptosByExistence()
                .filterCryptoNormalizePrices()
                .filterCryptoNormalizeName()

            Resource.Success(filteredResponse)
        } catch (e: IOException) {
            Resource.Error("No Internet Connection", )
        } catch (e: HttpException) {
            Resource.Error("Http Error")
        } catch (e: Exception) {
            Resource.Error("Exception")
        }
    }
    suspend fun updatePrice(symbol: String, price: BigDecimal) = withContext(dispatcher) { localDataSourceService.updatePrice(symbol, price) }
    suspend fun updateIsFavorite(symbol: String, isFavorite: Boolean) = withContext(dispatcher) { localDataSourceService.updateIsFavorite(symbol, isFavorite) }
    suspend fun saveOrUpdateCryptosToDatabase(cryptos: List<Price>) = withContext(dispatcher) { localDataSourceService.upsertAll(*cryptos.toTypedArray()) }
    suspend fun saveOrUpdateCryptoToDatabase(crypto: Price): Resource<Unit> {
        return try {
            localDataSourceService.upsert(crypto)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Crypto not saved in DB: ${e.message}")
        }
    }
    suspend fun deleteCryptoFromDatabase(crypto: Price): Resource<Unit> {
        return try {
            localDataSourceService.delete(crypto)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error deleting crypto: ${e.message}")
        }
    }
    suspend fun getCryptosFromDatabaseByExchangeRate(exchangeRate: Double): List<Price> {
        return getCryptosFromDatabase().map { listOfPrices ->
            listOfPrices.map { price ->
                price.copy(lastPrice = price.lastPrice * exchangeRate.toBigDecimal())
            }
        }.first().filterCryptoNormalizePrices().roundCryptoPrices()
    }
    fun getCountFavoritesCryptos(): Flow<Int> = localDataSourceService.getFavoritePriceCount().flowOn(dispatcher)
    fun getCryptosOrderedByName(list: MutableStateFlow<List<Price>>, order: SortOrder): Flow<List<Price>>{
        return when (order) {
            SortOrder.ASC -> list.map { cryptos ->
                cryptos.sortedBy { it.symbol }
            }
            SortOrder.DESC -> list.map { cryptos ->
                cryptos.sortedByDescending { it.symbol }
            }
            else -> list
        }
    }
    fun getCryptosOrderedByPrice(list: MutableStateFlow<List<Price>>, order: SortOrder): Flow<List<Price>>{
        return when (order) {
            SortOrder.ASC -> list.map { cryptos ->
                cryptos.sortedBy { it.lastPrice }
            }
            SortOrder.DESC -> list.map { cryptos ->
                cryptos.sortedByDescending { it.lastPrice }
            }
            else -> list
        }
    }
    fun getCryptosOrderedByFavorite(): Flow<List<Price>> = localDataSourceService.getCryptosOrderedByFavorite().flowOn(dispatcher)


}