package com.yoti.android.cryptocurrencychallenge.market.data

import com.google.gson.annotations.SerializedName

data class MarketsApiData(
    @SerializedName("data")
    val marketData: List<MarketData>?,
    @SerializedName("timestamp")
    val timestamp: Long?
)