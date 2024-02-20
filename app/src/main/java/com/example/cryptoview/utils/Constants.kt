package com.example.cryptoview.utils

import android.content.Context
import android.content.pm.Capability
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.util.TimeUtils
import com.example.cryptoview.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Flow


object Constants {
    const val BASE_URL_BINANCE = "https://api.binance.com"
    const val BASE_TICKER_DAILY_URL = "/api/v3/ticker/24hr"
    const val HEADER_API_KEY_BINANCE = "X-MBX-APIKEY: ${BuildConfig.API_KEY_BINANCE}"

    private const val CURRENCY_API_KEY = BuildConfig.API_KEY_CURRENCY
    const val BASE_CURRENCY_URL = "https://openexchangerates.org/api/"
    const val BASE_LATEST_EXCHANGE_RATES_CURRENCY_URL = "latest.json?app_id=${CURRENCY_API_KEY}"

    object DB {
        const val nameCryptoDatabase = "CryptosDatabase"
    }
}

val generateUniqueID: (Int) -> Int = {it + 1}

object InternetConnectionState : ConnectivityManager.NetworkCallback() {
    private val _connectionStateFlow = MutableStateFlow<Pair<Boolean, String>?>(null)
    val connectionStateFlow: StateFlow<Pair<Boolean, String>?> = _connectionStateFlow.asStateFlow()

    val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    override fun onAvailable(network: Network) {
        _connectionStateFlow.value = true to "You are online."
        super.onAvailable(network)

    }
    override fun onLost(network: Network) {
        _connectionStateFlow.value = false to "You are offline."
        super.onLost(network)
    }

    override fun onUnavailable() {
        super.onUnavailable()
    }
}




