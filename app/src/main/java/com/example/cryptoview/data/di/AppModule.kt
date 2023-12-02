package com.example.cryptoview.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.cryptoview.data.network.CurrencyService
import com.example.cryptoview.data.network.CryptoService
import com.example.cryptoview.data.network.repository.CurrencyRepository
import com.example.cryptoview.data.network.repository.PriceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun providePriceRepository(
        cryptoService: CryptoService,
        dispatcher: CoroutineDispatcher
    ): PriceRepository = PriceRepository(cryptoService, dispatcher)

    @Singleton
    @Provides
    fun provideCurrencyRepository(
        currencyService: CurrencyService,
        dispatcher: CoroutineDispatcher
    ): CurrencyRepository = CurrencyRepository(currencyService, dispatcher)

}