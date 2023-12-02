package com.example.cryptoview.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoview.data.local.repository.CryptosPreferencesRepository
import com.example.cryptoview.data.local.repository.CurrencyPreferencesRepository
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.sortBy
import com.example.cryptoview.data.network.repository.CurrencyRepository
import com.example.cryptoview.data.network.repository.PriceRepository
import com.example.cryptoview.ui.states.HomeScreenSortState
import com.example.cryptoview.ui.states.HomeScreenSortState.SortState
import com.example.cryptoview.ui.states.HomeScreenSortState.SortBy
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.ui.states.HomeScreenUIState.LoadingSource
import com.example.cryptoview.utils.Resource
import com.example.cryptoview.utils.TypeOfCurrency
import com.example.cryptoview.utils.getResourceResult
import com.example.cryptoview.utils.toNormalPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val priceRepository: PriceRepository,
    private val currencyRepository: CurrencyRepository,
    private val currencyPreferencesRepository: CurrencyPreferencesRepository,
    private val cryptosPreferencesRepository: CryptosPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> get() = _uiState

    private val _uiSortState = MutableStateFlow(HomeScreenSortState())
    val uiSortState: StateFlow<HomeScreenSortState> get() = _uiSortState

    var tabOnLongListenerPosition = 0
        private set

    fun setTabOnLongListenerPosition(position: Int) {
        tabOnLongListenerPosition = position
    }

    init {
        getAllLatestExchangeRates()
        getDailyCryptoStats()
    }

    fun getDailyCryptoStats(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD) = viewModelScope.launch {
        updateUIState(isLoadingSource = LoadingSource.DAILY_STATS)

        cryptosPreferencesRepository.getAllCryptosFromPreferences()

        getResourceResult(
            data = priceRepository.getDailyCryptoStats(),
            success = {
                if (typeOfCurrency == TypeOfCurrency.USD) {
                    updateUIState(cryptos = it)
                } else {
                    getAndUpdatePriceByExchangeRate(typeOfCurrency, it)
                }
            },
            error = { message, _ ->
                updateUIState(error = message)
            }
        )
    }

    fun getExchangeRateCurrency(typeOfCurrency: TypeOfCurrency) = viewModelScope.launch {
        updateUIState(isLoadingSource = LoadingSource.EXCHANGE_RATE)

        getResourceResult(
            data = currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency),
            success = {
                updateUIState(exchangeRate = it)
            }, error = { message, _ ->
                updateUIState(error = message)
            }
        )
    }

    private fun getAllLatestExchangeRates() = viewModelScope.launch {
        updateUIState(isLoadingSource = LoadingSource.NONE)

        when (val result = currencyRepository.getAllLatestRatesCurrencies()) {
            is Resource.Success -> currencyPreferencesRepository.saveAllExchangeRatesToPreferences(result.data!!.rates)
            is Resource.Error -> updateUIState(error = result.message)
        }
    }

    private fun getAndUpdatePriceByExchangeRate(
        typeOfCurrency: TypeOfCurrency,
        items: List<Price>
    ) = viewModelScope.launch {
        getResourceResult(
            data = currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency),
            success = {
                updateUIState(cryptos = items.map { item ->
                    item.copy(
                        lastPrice = (item.lastPrice.toDouble() * (it)).toString().toNormalPrice()
                    )
                })
            }, error = { message, _ ->
                updateUIState(error = message)
            }
        )
    }

    fun getSortBy(sortBy: SortBy) {
        sortBy.sortState = SortState.values()[(sortBy.sortState.ordinal + 1) % SortState.values().size]
        resetOtherSortStatesToNone(except = sortBy)

        updateUISortState(
            isSortedByName = SortBy.NAME.sortState,
            isSortedByPrice = SortBy.PRICE.sortState
        )

        updateUIState(
            cryptos = _uiState.value.cryptos.sortBy(sortBy)
        )
    }

    private fun resetOtherSortStatesToNone(except: SortBy) {
        SortBy.values().forEach { sortBy ->
            if (sortBy != except) {
                sortBy.sortState = SortState.NONE
            }
        }
    }

    private fun updateUISortState(
        isSortedByName: SortState,
        isSortedByPrice: SortState
    ) {
        _uiSortState.update {
            it.copy(
                isSortByName = isSortedByName,
                isSortByPrice = isSortedByPrice
            )
        }
    }

    fun searchBy(query: String) {
        updateUIState(
            cryptos = uiState.value.cryptos.filter {
                it.symbol.contains(query)
            }
        )
    }

    private fun updateUIState(
        isLoadingSource: LoadingSource = LoadingSource.NONE,
        cryptos: List<Price>? = null,
        exchangeRate: Double? = null,
        error: String? = null
    ) {
        _uiState.update {
            it.copy(
                isLoadingSource = isLoadingSource,
                cryptos = cryptos ?: it.cryptos,
                exchangeRate = exchangeRate,
                error = error
            )
        }
    }
}