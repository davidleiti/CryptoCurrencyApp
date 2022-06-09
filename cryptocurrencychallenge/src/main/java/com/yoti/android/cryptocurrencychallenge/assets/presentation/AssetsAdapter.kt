package com.yoti.android.cryptocurrencychallenge.assets.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yoti.android.cryptocurrencychallenge.databinding.AssetItemBinding

class AssetsAdapter(private val onAssetClicked: (AssetUiItem) -> Unit) :
    ListAdapter<AssetUiItem, AssetsAdapter.AssetItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AssetItemViewHolder(AssetItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AssetItemViewHolder, position: Int) {
        getItem(position)?.let { assetItem -> holder.bind(assetItem) }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AssetUiItem>() {
        override fun areItemsTheSame(oldItem: AssetUiItem, newItem: AssetUiItem): Boolean = oldItem.assetId == newItem.assetId
        override fun areContentsTheSame(oldItem: AssetUiItem, newItem: AssetUiItem): Boolean = oldItem == newItem
    }

    inner class AssetItemViewHolder(private val binding: AssetItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { assetItem -> onAssetClicked(assetItem) }
            }
        }

        fun bind(asset: AssetUiItem) {
            binding.textViewAssetCode.text = asset.symbol
            binding.textViewAssetName.text = asset.name
            binding.textViewAssetPrice.text = asset.price
        }
    }
}

