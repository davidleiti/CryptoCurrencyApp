package com.yoti.android.cryptocurrencychallenge.market.domain

interface MarketRepository {
    suspend fun getMarketForAsset(assetId: String): Result<Market>
}