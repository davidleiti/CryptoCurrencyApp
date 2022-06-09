package com.yoti.android.cryptocurrencychallenge.assets.domain

data class Asset(
    val id: String,
    val rank: Int = 0,
    val name: String = "",
    val symbol: String = "",
    val priceUsd: Double = 0.0,
    val changePercent24Hr: Double = 0.0,
    val marketCapUsd: Double = 0.0,
    val maxSupply: Double = 0.0,
    val supply: Double = 0.0,
    val volumeUsd24Hr: Double = 0.0,
    val vwap24Hr: Double = 0.0
)
