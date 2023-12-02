package com.example.cryptoview.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.cryptoview.data.network.CurrencyService
import com.example.cryptoview.data.network.CryptoService
import com.example.cryptoview.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideCryptoService(): CryptoService =
        provideRetrofit(Constants.BASE_URL_BINANCE).create(CryptoService::class.java)

    @Singleton
    @Provides
    fun provideCurrencyService(): CurrencyService =
        provideRetrofit(Constants.BASE_CURRENCY_URL).create(CurrencyService::class.java)
}