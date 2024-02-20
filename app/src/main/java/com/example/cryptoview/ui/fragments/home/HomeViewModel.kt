package com.example.cryptoview.ui.fragments.home

import android.database.DatabaseErrorHandler
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoview.data.local.preferences.repository.CurrencyPreferencesRepository
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.filterCryptoNormalizePrices
import com.example.cryptoview.data.models.roundCryptoPrices
import com.example.cryptoview.data.network.repository.CryptosRepository
import com.example.cryptoview.data.network.repository.CurrencyRepository
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.SortOrder
import com.example.cryptoview.utils.SortType
import com.example.cryptoview.utils.TypeOfCurrency
import com.example.cryptoview.utils.getResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cryptosRepository: CryptosRepository,
    private val currencyRepository: CurrencyRepository,
    private val currencyPreferencesRepository: CurrencyPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUIState())
    private val cryptos = MutableStateFlow<List<Price>>(emptyList())

    private val _exchangeRate = MutableSharedFlow<Double?>()
    val exchangeRate = _exchangeRate.asSharedFlow()

    val countIsFavorite = cryptosRepository.getCountFavoritesCryptos()

    private val _sortType = MutableStateFlow(SortType.NONE)
    private val _sortOrder = MutableStateFlow(SortOrder.NONE)

    private val _sortBy = combine(_sortType, _sortOrder) { sortType, sortOrder ->
        Pair(sortType, sortOrder)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _cryptos = _sortBy.flatMapLatest { (sortType, sortOrder) ->
        when (sortType) {
            SortType.NONE -> cryptos
            SortType.NAME -> cryptosRepository.getCryptosOrderedByName(cryptos, sortOrder)
            SortType.PRICE -> cryptosRepository.getCryptosOrderedByPrice(cryptos, sortOrder)
            SortType.FAVORITE -> cryptosRepository.getCryptosOrderedByFavorite()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    val uiState = combine(_uiState, _cryptos) { uiState, cryptos ->
        uiState.copy(
            cryptos = cryptos,
            sortOrder = _sortOrder.value,
            sortType = _sortType.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeScreenUIState())

    fun loadCryptos(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD) = viewModelScope.launch {
        if (typeOfCurrency == TypeOfCurrency.USD) {
            loadCryptosFromRemote()
        } else {
            loadCryptosFromDatabase(typeOfCurrency)
        }
    }

    private suspend fun loadCryptosFromRemote() {
        _uiState.update {
            it.copy(
                error = null,
                isLoading = true
            )
        }
        when (val result = cryptosRepository.getRemoteCryptos()) {
            is Resource.Success -> {
                try {
                    val cryptosCount = cryptosRepository.getCryptosFromDatabase().first().count()

                    if (cryptosCount > 0) {
                        result.data!!.map {
                            cryptosRepository.updatePrice(it.symbol, it.lastPrice)
                        }
                    } else {
                        throw IndexOutOfBoundsException()
                    }

                } catch (e: IndexOutOfBoundsException) {
                    cryptosRepository.saveOrUpdateCryptosToDatabase(result.data!!)
                    Log.d("error", e.message.toString())
                }

                cryptos.value = cryptosRepository.getCryptosFromDatabase().first()

                _uiState.update {
                    it.copy(
                        error = null,
                        isLoading = false
                    )
                }
            }
            is Resource.Error -> {
                _uiState.update {
                    it.copy(
                        error = "You have no actuality database!",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getCryptosSortBy(sortType: SortType) = viewModelScope.launch {
        val nextSortOrder = SortOrder.values()[(_sortOrder.value.ordinal + 1) % SortOrder.entries.size]

        _sortType.value = sortType
        _sortOrder.value = nextSortOrder
    }

    private suspend fun loadCryptosFromDatabase(typeOfCurrency: TypeOfCurrency) {
        currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency).collectLatest { resource ->
            when (resource) {
                is Resource.Success -> {
                    cryptos.value = cryptosRepository.getCryptosFromDatabaseByExchangeRate(resource.data!!)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            error = "You don't have local database with exchange rate",
                        )
                    }
                }
            }
        }
    }

    suspend fun loadAllExchangeRatesFromRemote() {
        currencyRepository.getAllLatestRatesCurrencies().collectLatest { result ->
            getResourceResult(
                data = result,
                success = {
                    viewModelScope.launch {
                        currencyPreferencesRepository.saveAllExchangeRatesToPreferences(it.rates)
                    }
                },
                error = { msg, _ ->
                    _uiState.update {
                        it.copy(
                            error = "You can't upload exchange rate from ethernet",
                        )
                    }
                }

            )
        }
    }

    fun updateIsFavorite(crypto: Price) = viewModelScope.launch {
        cryptosRepository.updateIsFavorite(crypto.symbol, crypto.isFavorite)
        cryptos.value = cryptosRepository.getCryptosFromDatabase().first()
    }

    fun loadExchangeRateCurrency(typeOfCurrency: TypeOfCurrency) = viewModelScope.launch {
        currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency).collectLatest { resource ->
            when (resource) {
                is Resource.Success -> {
                    _exchangeRate.emit(resource.data!!)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            error = "You can't upload exchange rate from preferences",
                        )
                    }
                }
            }
        }
    }
}

