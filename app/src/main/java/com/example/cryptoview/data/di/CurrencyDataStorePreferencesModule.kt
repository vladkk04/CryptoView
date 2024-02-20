package com.example.cryptoview.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.cryptoview.data.local.preferences.repository.CurrencyPreferencesRepository
import com.example.cryptoview.data.local.preferences.CurrencyPreferencesService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private val Context.currencyDataStore: DataStore<Preferences> by preferencesDataStore("currency")

@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyDataStorePreferencesModule {
    @Binds
    abstract fun bindCurrencyPreferencesService(
        currencyPreferencesRepository: CurrencyPreferencesRepository
    ): CurrencyPreferencesService

    companion object {
        @Singleton
        @Provides
        fun provideCurrencyDataStorePreferences(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = context.currencyDataStore
    }
}