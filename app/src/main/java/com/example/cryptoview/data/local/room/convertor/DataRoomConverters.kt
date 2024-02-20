package com.example.cryptoview.data.local.room.convertor

import androidx.room.TypeConverter
import java.math.BigDecimal

class DataRoomConverters {
    @TypeConverter
    fun fromBigDecimal(value: Double): BigDecimal {
        return value.toBigDecimal()
    }

    @TypeConverter
    fun toDouble(value: BigDecimal): Double {
        return value.toDouble()
    }
}