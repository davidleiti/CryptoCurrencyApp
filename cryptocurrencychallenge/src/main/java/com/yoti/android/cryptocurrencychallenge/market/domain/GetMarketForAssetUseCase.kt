package com.yoti.android.cryptocurrencychallenge.market.domain

import javax.inject.Inject

class GetMarketForAssetUseCase @Inject constructor(private val marketRepository: MarketRepository) {
    suspend operator fun invoke(assetId: String): Result<Market> = marketRepository.getMarketForAsset(assetId)
}