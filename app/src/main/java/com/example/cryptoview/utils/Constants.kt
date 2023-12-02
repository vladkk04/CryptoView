package com.example.cryptoview.utils

import com.example.cryptoview.BuildConfig


object Constants {
    const val BASE_URL_BINANCE = "https://api.binance.com"
    const val BASE_TICKER_DAILY_URL = "/api/v3/ticker/24hr"
    const val HEADER_API_KEY_BINANCE = "X-MBX-APIKEY: ${BuildConfig.API_KEY_BINANCE}"

    private const val CURRENCY_API_KEY = BuildConfig.API_KEY_CURRENCY
    const val BASE_CURRENCY_URL = "https://openexchangerates.org/api/"
    const val BASE_LATEST_EXCHANGE_RATES_CURRENCY_URL = "latest.json?app_id=${CURRENCY_API_KEY}"
}

