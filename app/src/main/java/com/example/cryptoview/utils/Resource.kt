package com.example.cryptoview.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Resource<T>(val data: T? = null, val message: String?= null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}

fun <T> getResourceResult(
    data: Resource<out T>,
    success: ((T) ->  Unit)? = null,
    error: ((String?, (T?)) -> Unit)? = null
) {
    when (data) {
        is Resource.Success -> {
            success?.invoke(data.data!!)
        }
        is Resource.Error -> {
            error?.invoke(data.message, data.data)
        }
    }
}