package com.example.cryptoview.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.cryptoview.data.local.CryptosPreferencesService
import com.example.cryptoview.data.local.repository.CryptosPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private val Context.cryptosDataStore: DataStore<Preferences> by preferencesDataStore("cryptos")

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CryptosDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptosDataStorePreferencesModule{

    @Binds
    abstract fun bindCryptoPreferencesService(
        cryptosPreferencesRepository: CryptosPreferencesRepository
    ): CryptosPreferencesService

    companion object {
        @Singleton
        @Provides
        @CryptosDataStore
        fun provideCryptosDataStorePreferences(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = context.cryptosDataStore
    }

}