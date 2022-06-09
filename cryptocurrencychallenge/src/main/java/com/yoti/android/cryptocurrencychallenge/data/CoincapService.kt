package com.yoti.android.cryptocurrencychallenge.data

import com.yoti.android.cryptocurrencychallenge.assets.data.network.AssetsApiData
import com.yoti.android.cryptocurrencychallenge.market.data.MarketsApiData
import retrofit2.http.GET
import retrofit2.http.Query


interface CoincapService {

    @GET("/v2/assets")
    suspend fun getAssets(): AssetsApiData

    @GET("/v2/markets")
    suspend fun getMarkets(@Query(value = "assetId") assetId: String): MarketsApiData

    companion object {
        const val BASE_URL = "https://api.coincap.io/"
    }
}