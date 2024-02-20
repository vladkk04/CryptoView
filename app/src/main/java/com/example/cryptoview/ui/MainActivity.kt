package com.example.cryptoview.ui

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.commit
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cryptoview.R
import com.example.cryptoview.databinding.ActivityMainBinding
import com.example.cryptoview.ui.fragments.home.HomeFragment
import com.example.cryptoview.utils.InternetConnectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        //connectivityManager.requestNetwork(InternetConnectionState.networkRequest, InternetConnectionState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, HomeFragment())
                //setReorderingAllowed(true)
                //addToBackStack(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        val menuItemSearch = menu?.findItem(R.id.search)
        val menuItemFavorite = menu?.findItem(R.id.favorite)

        val actionSearchView = menuItemSearch?.actionView
        val actionFavoriteView = menuItemFavorite?.actionView

        val favoriteCountView = actionFavoriteView?.findViewById<TextView>(R.id.number_favorite)

        if (actionSearchView is SearchView) {
            actionSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

                override fun onQueryTextSubmit(query: String?): Boolean {
                    actionSearchView.clearFocus()
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    mainViewModel.setSearchQuery(newText)
                    return true
                }
            })
        }

        actionFavoriteView?.setOnClickListener {
            actionFavoriteView.isSelected = !actionFavoriteView.isSelected
            mainViewModel.setIsFilterByFavorite()
        }

        lifecycleScope.launch {
            mainViewModel.countFavorite.collectLatest {
                favoriteCountView?.text = it.toString()
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(InternetConnectionState)
    }
}
