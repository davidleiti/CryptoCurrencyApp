package com.yoti.android.cryptocurrencychallenge.market.presentation

import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MarketUiFormatter @Inject constructor(
    private val dateFormatProvider: DateFormatProvider
) {
    fun formatMarket(market: Market): MarketUiModel {
        val dateFormat = dateFormatProvider.provideFormat(DEFAULT_DATE_FORMAT)
        return MarketUiModel(
            exchangeId = market.exchangeId,
            rank = market.rank.toString(),
            price = market.priceUsd.toString(),
            date = dateFormat.format(Date(market.updated))
        )
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "dd/MM/yyyy"
    }
}

class DateFormatProvider @Inject constructor() {
    fun provideFormat(formatPattern: String): DateFormat = SimpleDateFormat(formatPattern, Locale.getDefault())
}