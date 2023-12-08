package com.example.cryptoview.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoview.data.local.repository.CryptosPreferencesRepository
import com.example.cryptoview.data.local.repository.CurrencyPreferencesRepository
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.data.models.sortBy
import com.example.cryptoview.data.network.repository.CurrencyRepository
import com.example.cryptoview.data.network.repository.PriceRepository
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.ui.states.HomeScreenUIState.SortState
import com.example.cryptoview.ui.states.HomeScreenUIState.SortBy
import com.example.cryptoview.ui.states.HomeScreenUIState.LoadingSource
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

    private fun updateLoadingSource(isLoadingSource: LoadingSource) =
        _uiState.update { it.copy(isLoadingSource = isLoadingSource) }

    private fun updateListCryptos(cryptos: List<Price>) =
        _uiState.update { it.copy(cryptos = cryptos) }

    private fun updateExchangeRate(exchangeRate: Double) =
        _uiState.update { it.copy(exchangeRate = exchangeRate) }

    private fun updateError(error: String?) =
        _uiState.update { it.copy(error = error) }

    private fun updateUISortState(
        isSortedByName: SortState,
        isSortedByPrice: SortState
    ) {
        _uiState.update {
            it.copy(
                isSortByName = isSortedByName,
                isSortByPrice = isSortedByPrice
            )
        }
    }

    private val getPricesByExchangeRate: (cryptos: List<Price>, exchangeRate: Double) -> List<Price> = { cryptos, exchangeRate ->
        cryptos.map { item ->
            item.copy(lastPrice = (item.lastPrice.toDouble() * (exchangeRate)).toString().toNormalPrice())
        }
    }

    init {
        loadDailyCryptoStats()
        loadAllLatestExchangeRates()
    }

    fun loadDailyCryptoStats(typeOfCurrency: TypeOfCurrency = TypeOfCurrency.USD) = viewModelScope.launch {
        updateLoadingSource(isLoadingSource = LoadingSource.DAILY_STATS)

        getResourceResult(
            data = priceRepository.getDailyCryptoStats(),
            success = {
                getAndUpdatePriceByExchangeRate(typeOfCurrency, it)
            },
            error = { message, _ ->
                updateError(error = message)
            }
        )

        updateLoadingSource(isLoadingSource = LoadingSource.NONE)
    }

    fun loadExchangeRateCurrency(typeOfCurrency: TypeOfCurrency) = viewModelScope.launch {
        updateLoadingSource(isLoadingSource = LoadingSource.EXCHANGE_RATE)

        getResourceResult(
            data = currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency),
            success = {
                updateExchangeRate(exchangeRate = it)
            }, error = { message, _ ->
                updateError(error = message)
            }
        )
    }

    fun getCryptoSortBy(sortBy: SortBy) {
        sortBy.sortState = SortState.values()[(sortBy.sortState.ordinal + 1) % SortState.values().size]
        resetOtherSortStatesToNone(except = sortBy)

        updateUISortState(
            isSortedByName = SortBy.NAME.sortState,
            isSortedByPrice = SortBy.PRICE.sortState
        )

        updateListCryptos(
            cryptos = _uiState.value.cryptos.sortBy(sortBy)
        )
    }

    private fun loadAllLatestExchangeRates() = viewModelScope.launch {
        updateLoadingSource(LoadingSource.EXCHANGE_RATE)
        getResourceResult(
            data = currencyRepository.getAllLatestRatesCurrencies(),
            success = {
                //currencyPreferencesRepository.saveAllExchangeRatesToPreferences(it.rates)
            },
            error = { message, _ ->
                updateError(message)
            }
        )
        updateLoadingSource(LoadingSource.NONE)
    }

    private fun getAndUpdatePriceByExchangeRate(
        typeOfCurrency: TypeOfCurrency,
        items: List<Price>
    ) = viewModelScope.launch {

        if (typeOfCurrency == TypeOfCurrency.USD)
            return@launch updateListCryptos(items)

        getResourceResult(
            data = currencyPreferencesRepository.getExchangeRateFromPreferences(typeOfCurrency),
            success = {
                updateListCryptos(cryptos = getPricesByExchangeRate(items, it))
            }, error = { message, _ ->
                updateError(error = message)
            }
        )
    }

    private fun resetOtherSortStatesToNone(except: SortBy) {
        SortBy.values().forEach { sortBy ->
            if (sortBy != except) {
                sortBy.sortState = SortState.NONE
            }
        }
    }
}