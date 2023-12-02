package com.example.cryptoview.utils

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.cryptoview.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.showLoadingBar(isLoading: Boolean = false) {
    val progressBar = this.requireActivity().findViewById<ProgressBar>(R.id.loading_bar)!!

    if (isLoading) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.INVISIBLE
    }
}

fun Fragment.showSnackBar(
    message: String? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: ((View) -> Unit)? = null
) {
    if (message == null) return

    Snackbar.make(requireView(), message, duration).apply {
        setAction(actionText, action)
    }.show()
}




