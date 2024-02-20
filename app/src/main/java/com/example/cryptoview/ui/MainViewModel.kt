package com.example.cryptoview.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.cryptoview.utils.TypeOfCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _countFavorite = MutableStateFlow(0)

    var isFilterByFavorite = MutableStateFlow(false)
        private set
    val searchQuery: StateFlow<String> get() = _searchQuery
    val countFavorite: StateFlow<Int> get() = _countFavorite

    fun setSearchQuery(query: String?) {
        if (query != null) {
            _searchQuery.value = query
        }
    }

    fun setIsFilterByFavorite() {
        isFilterByFavorite.update { !isFilterByFavorite.value }
    }
    fun setCountFavorite(quantity: Int) {
        _countFavorite.value = quantity
    }


}