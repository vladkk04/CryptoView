package com.example.cryptoview

import android.app.Application
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp(Application::class)
class CryptoViewApplication: Application()