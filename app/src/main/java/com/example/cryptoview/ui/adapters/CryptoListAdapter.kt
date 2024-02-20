package com.example.cryptoview.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoview.R
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.databinding.CryptoListItemBinding
import com.example.cryptoview.utils.getResourceId
import com.example.cryptoview.utils.toCryptoIconName
import java.math.BigDecimal


class CryptoListAdapter : RecyclerView.Adapter<CryptoListAdapter.CryptoViewHolder>() {
    private var onClickListeners: CryptoListOnClickListeners ?= null
    fun setOnClickListener(onClickListener: CryptoListOnClickListeners) {
        this.onClickListeners = onClickListener
    }

    val differ = AsyncListDiffer(this, CryptoListCallback())

    inner class CryptoViewHolder(private val binding: CryptoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(crypto: Price) {
            binding.cryptoNameTextview.text = crypto.symbol
            binding.cryptoPriceTextview.text = crypto.lastPrice.toString()
            binding.btnStar.isSelected = crypto.isFavorite

            binding.btnStar.setOnClickListener {
                binding.btnStar.isSelected = !binding.btnStar.isSelected
                onClickListeners?.onClick(adapterPosition, crypto)
            }

            val iconName = crypto.symbol.toCryptoIconName()
            val resId = getResourceId(binding.root.context, iconName, "drawable")
            if (resId != 0) {
                binding.iconCryptoImage.setImageResource(resId)
            } else {
                binding.iconCryptoImage.setImageResource(R.drawable.ic_btc)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindPrice(price: BigDecimal) {
            binding.cryptoPriceTextview.text = price.toString()
        }

        @SuppressLint("SetTextI18n")
        fun bindFavorite(isFavorite: Boolean) {
            binding.btnStar.isSelected = isFavorite
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        return CryptoViewHolder(
            CryptoListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(
        holder: CryptoViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (val payload = payloads.lastOrNull()) {
            is CryptoListCallback.ArticleChangePayload.Price -> {
                holder.bindPrice(payload.price)
            }

            is CryptoListCallback.ArticleChangePayload.Favorite -> {
                holder.bindFavorite(payload.isFavorite)
            }

            else -> onBindViewHolder(holder, position)
        }
    }
}
