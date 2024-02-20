package com.example.cryptoview.data.di

import android.content.Context
import androidx.room.Room
import com.example.cryptoview.data.local.room.CryptosDatabase
import com.example.cryptoview.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideCryptosDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, CryptosDatabase::class.java, Constants.DB.nameCryptoDatabase
    ).build()

    @Provides
    fun provideDao(db: CryptosDatabase) = db.dao()

}