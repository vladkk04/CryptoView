package com.example.cryptoview.data.local

import com.example.cryptoview.data.models.Price
import com.example.cryptoview.utils.Resource

interface CryptosPreferencesService {
    suspend fun saveAllCryptosToPreferences(price: List<Price>)
    suspend fun getAllCryptosFromPreferences(): Resource<List<Price>>
}