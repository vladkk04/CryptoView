package com.example.cryptoview.data.di

import com.example.cryptoview.data.local.room.dao.CryptoDao
import com.example.cryptoview.data.network.CurrencyService
import com.example.cryptoview.data.network.CryptoService
import com.example.cryptoview.data.network.repository.CurrencyRepository
import com.example.cryptoview.data.network.repository.CryptosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideCryptosRepository(
        remoteDataSourceService: CryptoService,
        localDataSourceService: CryptoDao,
        dispatcher: CoroutineDispatcher
    ): CryptosRepository = CryptosRepository(remoteDataSourceService, localDataSourceService, dispatcher)

    @Singleton
    @Provides
    fun provideCurrencyRepository(
        currencyService: CurrencyService,
        dispatcher: CoroutineDispatcher
    ): CurrencyRepository = CurrencyRepository(currencyService, dispatcher)
}