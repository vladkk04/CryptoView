package com.example.cryptoview.utils

import android.content.Context
import android.widget.ImageView

fun getResourceId(context: Context, name: String, defType: String) = context.resources.getIdentifier(
    "ic_${name}",
    defType,
    context.packageName
)