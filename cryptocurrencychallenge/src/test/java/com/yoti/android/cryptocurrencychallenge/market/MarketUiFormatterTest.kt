package com.yoti.android.cryptocurrencychallenge.market

import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import com.yoti.android.cryptocurrencychallenge.market.presentation.DateFormatProvider
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketUiFormatter
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketUiModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.util.Date

class MarketUiFormatterTest {

    @MockK
    lateinit var  dateFormatProvider: DateFormatProvider

    @InjectMockKs
    lateinit var sut: MarketUiFormatter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `formatMarket() formats the given market`() {
        // Given
        val testMarket = Market(
            exchangeId = "testId",
            rank = 3,
            priceUsd = 123.31,
            updated = 1000
        )

        val mockDateFormat = mockk<DateFormat>()
        every { dateFormatProvider.provideFormat("dd/MM/yyyy") } returns mockDateFormat
        every { mockDateFormat.format(Date(1000)) } returns "11/11/2011"

        // When
        val uiModel = sut.formatMarket(testMarket)

        // Then
        val expectedUiModel = MarketUiModel(
            exchangeId = "testId",
            rank = "3",
            price = "123.31",
            date = "11/11/2011"
        )
        Assert.assertEquals(expectedUiModel, uiModel)
    }
}