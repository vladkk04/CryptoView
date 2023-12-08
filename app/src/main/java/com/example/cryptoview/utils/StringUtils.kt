package com.example.cryptoview.utils

fun String.toCryptoIconName(): String = this.lowercase()

fun String.toNormalPrice(): String = String.format("%.2f", this.toDouble())

fun String.priceToDollar(): String = this.toNormalPrice() + " ≈ 1$"
