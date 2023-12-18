package com.example.cryptoview.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoview.R
import com.example.cryptoview.databinding.FragmentHomeBinding
import com.example.cryptoview.ui.MainViewModel
import com.example.cryptoview.ui.adapters.CryptoListAdapter
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.ui.states.HomeScreenUIState.LoadingSource
import com.example.cryptoview.ui.states.HomeScreenUIState.SortState
import com.example.cryptoview.utils.TypeOfCurrency
import com.example.cryptoview.utils.createTabsLayout
import com.example.cryptoview.utils.priceToDollar
import com.example.cryptoview.utils.showLoadingBar
import com.example.cryptoview.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val adapter: CryptoListAdapter by lazy { CryptoListAdapter() }

    private var currencyLongClickListener = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        createUI()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    collectUIState()
                }

                launch {
                    homeViewModel.exchangeRate.collect {
                        if (it == null)
                            return@collect

                        binding.currencyTabsLayout.getTabAt(currencyLongClickListener)?.customView?.findViewById<TextView>(
                            R.id.currency_rate_text
                        )?.let { textView ->
                            if (textView.text.isEmpty()) {
                                textView.text = it.toString().priceToDollar()
                                textView.visibility = View.VISIBLE
                            } else {
                                textView.text = null
                                textView.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    mainViewModel.searchQuery.collectLatest {}
                }
            }
        }
    }

    private fun createUI() {
        createTabsTypeOfCurrencies()
    }

    private fun createTabsTypeOfCurrencies() {
        createTabsLayout(
            tabLayout = binding.currencyTabsLayout,
            tabs = TypeOfCurrency.values().toList(),
            textView = R.id.currency_type_text,
            customViewID = R.layout.currency_item_tabs,
            onTabSelected = { typeOfCurrency ->
                homeViewModel.loadDailyCryptoStats(typeOfCurrency)
            },
            onLongClickListener = { typeOfCurrency, position ->
                currencyLongClickListener = position
                homeViewModel.loadExchangeRateCurrency(typeOfCurrency)
            }
        )
    }

    private fun setupClickListeners() {
        binding.textSortByName.setOnClickListener {
            homeViewModel.getCryptoSortBy(HomeScreenUIState.SortBy.NAME)
        }
        binding.textSortByPrice.setOnClickListener {
            homeViewModel.getCryptoSortBy(HomeScreenUIState.SortBy.PRICE)
        }
    }

    private fun updateUI(uiState: HomeScreenUIState) {
        showSnackBar(uiState.error, actionText = "Try Again", action = {homeViewModel.loadDailyCryptoStats()})

        when (uiState.isLoadingSource) {
            LoadingSource.DAILY_STATS -> {
                showLoadingBar(true)
                binding.recyclerView.visibility = View.INVISIBLE
            }
            LoadingSource.EXCHANGE_RATE -> {
                showLoadingBar(true)

            }
            else -> {
                adapter.differ.submitList(uiState.cryptos) {
                    binding.recyclerView.visibility = View.VISIBLE
                }
                showLoadingBar(false)
            }
        }
    }

    private fun updateSortStateViews(uiState: HomeScreenUIState) {
        when (uiState.isSortByName) {
            SortState.NONE -> binding.textSortByName.setText(R.string.text_filter_by_name_relevant)
            SortState.UP -> binding.textSortByName.setText(R.string.text_filter_by_name_A)
            SortState.DOWN -> binding.textSortByName.setText(R.string.text_filter_by_name_Z)
        }
        when (uiState.isSortByPrice) {
            SortState.NONE -> binding.textSortByPrice.setText(R.string.text_filter_by_price_relevant)
            SortState.UP -> binding.textSortByPrice.setText(R.string.text_filter_by_price_low)
            SortState.DOWN -> binding.textSortByPrice.setText(R.string.text_filter_by_price_high)
        }
    }

    private suspend fun collectUIState() {
        homeViewModel.uiState.collectLatest {
            updateUI(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}