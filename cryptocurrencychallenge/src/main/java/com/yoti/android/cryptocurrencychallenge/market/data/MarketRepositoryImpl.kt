package com.yoti.android.cryptocurrencychallenge.market.data

import com.yoti.android.cryptocurrencychallenge.data.ApiCallExecutor
import com.yoti.android.cryptocurrencychallenge.data.CoincapService
import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import com.yoti.android.cryptocurrencychallenge.market.domain.MarketRepository
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val coincapService: CoincapService,
    private val apiCallExecutor: ApiCallExecutor,
    private val marketConverter: MarketConverter
) : MarketRepository {

    override suspend fun getMarketForAsset(assetId: String): Result<Market> {
        return apiCallExecutor.performCall(
            call = { coincapService.getMarkets(assetId) },
            validate = { marketApiData -> !marketApiData.marketData.isNullOrEmpty() }
        ).map { marketData ->
            marketConverter.toModel(marketData.marketData.orEmpty().first())
        }
    }
}