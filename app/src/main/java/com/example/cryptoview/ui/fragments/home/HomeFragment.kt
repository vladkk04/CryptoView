package com.example.cryptoview.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoview.R
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.databinding.FragmentHomeBinding
import com.example.cryptoview.ui.MainViewModel
import com.example.cryptoview.ui.adapters.CryptoListAdapter
import com.example.cryptoview.ui.adapters.CryptoListOnClickListeners
import com.example.cryptoview.ui.states.HomeScreenUIState
import com.example.cryptoview.utils.SortType
import com.example.cryptoview.utils.TypeOfCurrency
import com.example.cryptoview.utils.createTabsLayout
import com.example.cryptoview.utils.priceToDollar
import com.example.cryptoview.utils.showLoadingBar
import com.example.cryptoview.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val adapter: CryptoListAdapter by lazy { CryptoListAdapter() }

    private var positionOnLongClickListener = 0


    //private val connectivityManager = getSystemService(this.requireContext(), ConnectivityManager::class.java) as ConnectivityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager

        binding.recyclerView.itemAnimator = null

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        createTabsTypeOfCurrencies()

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                observeUIState()
            }
            launch {
                observeExchangeRate()
            }
            launch {
                observeSearchQuery()
            }
            launch {
                homeViewModel.countIsFavorite.collectLatest {
                    mainViewModel.setCountFavorite (it)
                }
            }
            launch {
                mainViewModel.isFilterByFavorite.collectLatest {
                    if(it) homeViewModel.getCryptosSortBy(SortType.FAVORITE)
                    else homeViewModel.getCryptosSortBy(SortType.NONE)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.loadCryptos()
                }
                launch {
                    homeViewModel.loadAllExchangeRatesFromRemote()
                }
            }
        }
    }
    private suspend fun observeSearchQuery() = mainViewModel.searchQuery.collectLatest { filterBySearch(it) }

    private suspend fun observeUIState() = homeViewModel.uiState.collectLatest { updateUI(it) }
    private suspend fun observeExchangeRate() {
        homeViewModel.exchangeRate.collectLatest {
            binding.currencyTabsLayout.getTabAt(positionOnLongClickListener)?.customView?.findViewById<TextView>(
                R.id.currency_rate_text
            )?.let { textView ->
                if (it == null) {
                    return@let
                } else if (textView.text.isEmpty()) {
                    textView.text = it.toString().priceToDollar()
                    textView.visibility = View.VISIBLE
                } else {
                    textView.text = null
                    textView.visibility = View.GONE
                }
            }
        }
    }

    private fun createTabsTypeOfCurrencies() {
        createTabsLayout(
            tabLayout = binding.currencyTabsLayout,
            tabs = TypeOfCurrency.values().toList(),
            textView = R.id.currency_type_text,
            customViewID = R.layout.currency_item_tabs,
            onTabSelected = { typeOfCurrency ->
                homeViewModel.loadCryptos(typeOfCurrency)
            },
            onLongClickListener = { typeOfCurrency, position ->
                homeViewModel.loadExchangeRateCurrency(typeOfCurrency)
                positionOnLongClickListener = position
            }
        )
    }

    private fun setupClickListeners() {
        binding.textSortByName.setOnClickListener {
            homeViewModel.getCryptosSortBy(SortType.NAME)
        }
        binding.textSortByPrice.setOnClickListener {
            homeViewModel.getCryptosSortBy(SortType.PRICE)
        }

        adapter.setOnClickListener(object : CryptoListOnClickListeners {
            override fun onClick(position: Int, model: Price) {
                model.isFavorite = !model.isFavorite
                homeViewModel.updateIsFavorite(model)
            }
        })
    }

    private fun updateUI(uiState: HomeScreenUIState) {
        showSnackBar(uiState.error)

        if (uiState.isLoading) {
            binding.recyclerView.visibility = View.GONE
            showLoadingBar(true)
        } else {
            adapter.differ.submitList(uiState.cryptos) {
                binding.recyclerView.visibility = View.VISIBLE
                showLoadingBar(false)
            }
        }

        updateSortStateViews(uiState)
    }

    private fun updateSortStateViews(uiState: HomeScreenUIState) {
        when (uiState.sortType) {
            SortType.NAME -> {
                binding.textSortByName.text = resources.getStringArray(R.array.text_filter_by_name_array)[uiState.sortOrder.ordinal]
                binding.textSortByPrice.text = resources.getStringArray(R.array.text_filter_by_price_array)[0]
            }
            SortType.PRICE -> {
                binding.textSortByPrice.text = resources.getStringArray(R.array.text_filter_by_price_array)[uiState.sortOrder.ordinal]
                binding.textSortByName.text = resources.getStringArray(R.array.text_filter_by_name_array)[0]
            }
            SortType.NONE -> {
                binding.textSortByPrice.text = resources.getStringArray(R.array.text_filter_by_price_array)[0]
                binding.textSortByName.text = resources.getStringArray(R.array.text_filter_by_name_array)[0]
            }
            SortType.FAVORITE -> {

            }
        }
    }

    private fun filterBySearch(query: String) = adapter.differ.submitList(homeViewModel.uiState.value.cryptos.filter {
        it.symbol.contains(query.uppercase())
    })

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}