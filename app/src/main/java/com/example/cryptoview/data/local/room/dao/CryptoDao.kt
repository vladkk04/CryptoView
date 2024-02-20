package com.example.cryptoview.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.cryptoview.data.models.Price
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface CryptoDao {

    @Upsert
    suspend fun upsertAll(vararg cryptos: Price)

    @Query("UPDATE cryptos SET lastPrice =:price WHERE symbol =:symbol")
    suspend fun updatePrice(symbol: String, price: BigDecimal)

    @Query("UPDATE cryptos SET isFavorite =:isFavorite WHERE symbol =:symbol")
    suspend fun updateIsFavorite(symbol: String, isFavorite: Boolean)

    @Upsert
    suspend fun upsert(crypto: Price)

    @Delete
    suspend fun delete(crypto: Price)

    @Query("DELETE FROM cryptos")
    fun deleteAll()

    @Query("SELECT * FROM cryptos")
    fun getCryptos(): Flow<List<Price>>

    @Query("SELECT * FROM cryptos ORDER BY symbol ASC ")
    fun getCryptosOrderedByName(): Flow<List<Price>>

    @Query("SELECT * FROM cryptos ORDER BY symbol DESC")
    fun getCryptosOrderedByDescendingName(): Flow<List<Price>>

    @Query("SELECT * FROM cryptos ORDER BY lastPrice ASC")
    fun getCryptosOrderedByPrice(): Flow<List<Price>>

    @Query("SELECT * FROM cryptos ORDER BY lastPrice DESC")
    fun getCryptosOrderedByDescendingPrice(): Flow<List<Price>>

    @Query("SELECT * FROM cryptos WHERE isFavorite = 1 ORDER BY isFavorite")
    fun getCryptosOrderedByFavorite(): Flow<List<Price>>

    @Query("SELECT COUNT(*) FROM cryptos WHERE isFavorite = 1")
    fun getFavoritePriceCount(): Flow<Int>

}