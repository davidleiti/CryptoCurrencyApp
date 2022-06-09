package com.yoti.android.cryptocurrencychallenge.assets.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "rank") val rank: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "symbol") val symbol: String = "",
    @ColumnInfo(name = "price_usd") val priceUsd: Double = 0.0,
    @ColumnInfo(name = "change_percent_24hr") val changePercent24Hr: Double = 0.0,
    @ColumnInfo(name = "market_cap_usd") val marketCapUsd: Double = 0.0,
    @ColumnInfo(name = "max_supply") val maxSupply: Double = 0.0,
    @ColumnInfo(name = "supply") val supply: Double = 0.0,
    @ColumnInfo(name = "volume_usd_24hr") val volumeUsd24Hr: Double = 0.0,
    @ColumnInfo(name = "vwap_24hr") val vwap24Hr: Double = 0.0
)