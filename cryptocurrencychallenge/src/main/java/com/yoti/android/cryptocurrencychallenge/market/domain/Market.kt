package com.yoti.android.cryptocurrencychallenge.market.domain

data class Market(
    val exchangeId: String = "",
    val baseId: String = "",
    val baseSymbol: String = "",
    val rank: Int = 0,
    val quoteId: String = "",
    val quoteSymbol: String = "",
    val percentExchangeVolume: Double = 0.0,
    val priceQuote: Double = 0.0,
    val priceUsd: Double = 0.0,
    val tradesCount24Hr: Double = 0.0,
    val volumeUsd24Hr: Double = 0.0,
    val updated: Long = 0
)