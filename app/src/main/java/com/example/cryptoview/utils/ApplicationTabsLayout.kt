package com.example.cryptoview.utils

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.cryptoview.R
import com.google.android.material.tabs.TabLayout

fun <T> createTabsLayout(
    tabLayout: TabLayout,
    tabs: List<T>,
    @IdRes textView: Int,
    @LayoutRes customViewID: Int ?= null,
    onTabSelected: (T) -> Unit,
    onLongClickListener: ((T, Int) -> Unit)? = null
) {
    tabs.forEachIndexed { index, tabItem ->
        tabLayout.newTab().apply {
            if (customViewID != null){
                setCustomView(customViewID)
                customView?.findViewById<TextView>(textView)?.text = tabItem.toString()
            }
            text = tabItem.toString()
            tag = tabItem
            view.setOnLongClickListener {
                onLongClickListener?.invoke(tabItem, index)
                true
            }
        }.also { tabLayout.addTab(it) }
    }

    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) = onTabSelected(tab.tag as T)
        override fun onTabReselected(tab: TabLayout.Tab) = onTabSelected(tab)
        override fun onTabUnselected(tab: TabLayout.Tab) {}
    })
}