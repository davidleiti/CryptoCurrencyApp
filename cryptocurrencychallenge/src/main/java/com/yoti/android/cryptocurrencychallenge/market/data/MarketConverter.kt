package com.yoti.android.cryptocurrencychallenge.market.data

import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import javax.inject.Inject

class MarketConverter @Inject constructor()  {

    fun toModel(marketData: MarketData): Market = Market(
        exchangeId = marketData.exchangeId.orEmpty(),
        rank = marketData.rank?.toIntOrNull() ?: 0,
        baseId = marketData.baseId.orEmpty(),
        baseSymbol = marketData.baseSymbol.orEmpty(),
        quoteId = marketData.quoteId.orEmpty(),
        quoteSymbol = marketData.quoteSymbol.orEmpty(),
        percentExchangeVolume = marketData.percentExchangeVolume?.toDoubleOrNull() ?: 0.0,
        priceQuote = marketData.priceQuote?.toDoubleOrNull() ?: 0.0,
        priceUsd = marketData.priceUsd?.toDoubleOrNull() ?: 0.0,
        tradesCount24Hr = marketData.tradesCount24Hr?.toDoubleOrNull() ?: 0.0,
        volumeUsd24Hr = marketData.volumeUsd24Hr?.toDoubleOrNull() ?: 0.0,
        updated = marketData.updated ?: 0
    )
}