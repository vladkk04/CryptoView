package com.example.cryptoview.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cryptoview.data.local.room.dao.CryptoDao
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.local.room.convertor.DataRoomConverters

@Database(
    entities = [Price::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataRoomConverters::class)
abstract class CryptosDatabase: RoomDatabase() {
    abstract fun dao(): CryptoDao

}