package com.example.cryptoview.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.commit
import androidx.activity.viewModels
import com.example.cryptoview.R
import com.example.cryptoview.databinding.ActivityMainBinding
import com.example.cryptoview.ui.fragments.home.HomeFragment
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, HomeFragment())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        val menuItemSearch = menu?.findItem(R.id.search)
        val actionView = menuItemSearch?.actionView

        if (actionView is SearchView) {
            actionView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    mainViewModel.setSearchQuery(newText)
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }
}
