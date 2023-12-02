package com.example.cryptoview.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoview.R
import com.example.cryptoview.data.models.Price
import com.example.cryptoview.databinding.CryptoListItemBinding
import com.example.cryptoview.utils.getResourceId
import com.example.cryptoview.utils.toCryptoIconName


class CryptoListAdapter: RecyclerView.Adapter<CryptoListAdapter.CryptoViewHolder>() {
    val differ = AsyncListDiffer(this, CryptoListCallback())

    class CryptoViewHolder(private val binding: CryptoListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): CryptoViewHolder {
                val binding = CryptoListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CryptoViewHolder(binding)
            }
        }

        fun bind(crypto: Price) {
            binding.cryptoNameTextview.text = crypto.symbol
            binding.cryptoPriceTextview.text = crypto.lastPrice

            val iconName = crypto.symbol.toCryptoIconName()
            val resId = getResourceId(binding.root.context, iconName, "drawable")
            if (resId != 0) {
                binding.iconCryptoImage.setImageResource(resId)
            } else {
                binding.iconCryptoImage.setImageResource(R.drawable.ic_btc)
            }
        }

        fun bindPrice(price: String) {
            binding.cryptoPriceTextview.text = price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        return CryptoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
      //  Log.d("d", holder.adapterPosition.toString())
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int, payloads: MutableList<Any>) {
        when (val payload = payloads.lastOrNull()) {
            is CryptoListCallback.ArticleChangePayload.Price -> holder.bindPrice(payload.price)
            else -> onBindViewHolder(holder, position)
        }
    }
}
