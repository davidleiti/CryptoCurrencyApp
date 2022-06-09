package com.yoti.android.cryptocurrencychallenge.assets.data

import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetEntity
import com.yoti.android.cryptocurrencychallenge.assets.data.network.AssetData
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import javax.inject.Inject

class AssetsConverter @Inject constructor() {

    fun toModel(data: AssetData): Asset = Asset(
        id = data.id.orEmpty(),
        name = data.name.orEmpty(),
        rank = data.rank?.toIntOrNull() ?: 0,
        priceUsd = data.priceUsd?.toDoubleOrNull() ?: 0.0,
        changePercent24Hr = data.changePercent24Hr?.toDoubleOrNull() ?: 0.0,
        marketCapUsd = data.marketCapUsd?.toDoubleOrNull() ?: 0.0,
        maxSupply = data.maxSupply?.toDoubleOrNull() ?: 0.0,
        supply = data.supply?.toDoubleOrNull() ?: 0.0,
        symbol = data.symbol.orEmpty(),
        volumeUsd24Hr = data.volumeUsd24Hr?.toDoubleOrNull() ?: 0.0,
        vwap24Hr = data.vwap24Hr?.toDoubleOrNull() ?: 0.0
    )

    fun toModel(entity: AssetEntity): Asset = Asset(
        id = entity.id,
        name = entity.name,
        priceUsd = entity.priceUsd,
        changePercent24Hr = entity.changePercent24Hr,
        marketCapUsd = entity.marketCapUsd,
        maxSupply = entity.maxSupply,
        rank = entity.rank,
        supply = entity.supply,
        symbol = entity.symbol,
        volumeUsd24Hr = entity.volumeUsd24Hr,
        vwap24Hr = entity.vwap24Hr
    )

    fun toEntity(model: Asset): AssetEntity = AssetEntity(
        id = model.id,
        name = model.name,
        priceUsd = model.priceUsd,
        changePercent24Hr = model.changePercent24Hr,
        marketCapUsd = model.marketCapUsd,
        maxSupply = model.maxSupply,
        rank = model.rank,
        supply = model.supply,
        symbol = model.symbol,
        volumeUsd24Hr = model.volumeUsd24Hr,
        vwap24Hr = model.vwap24Hr
    )
}