package com.example.cryptoview.ui

import androidx.lifecycle.ViewModel
import com.example.cryptoview.utils.TypeOfCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel: ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    fun setSearchQuery(query: String?) {
        if (query.isNullOrEmpty())
            return

        _searchQuery.value = query
    }
}