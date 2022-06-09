package com.yoti.android.cryptocurrencychallenge.market

import com.yoti.android.cryptocurrencychallenge.TestException
import com.yoti.android.cryptocurrencychallenge.data.ApiCallExecutor
import com.yoti.android.cryptocurrencychallenge.data.CoincapService
import com.yoti.android.cryptocurrencychallenge.data.NetworkException
import com.yoti.android.cryptocurrencychallenge.market.data.MarketConverter
import com.yoti.android.cryptocurrencychallenge.market.data.MarketData
import com.yoti.android.cryptocurrencychallenge.market.data.MarketRepositoryImpl
import com.yoti.android.cryptocurrencychallenge.market.data.MarketsApiData
import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketRepositoryImplTest {

    @MockK
    lateinit var coincapService: CoincapService

    @MockK
    lateinit var apiCallExecutor: ApiCallExecutor

    @MockK
    lateinit var marketConverter: MarketConverter

    @InjectMockKs
    lateinit var sut: MarketRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getMarketForAsset() returns Result success() when response is valid`() = runTest {
        // Given
        val testMarketData = MarketData(
            baseId = "testId",
            baseSymbol = "testSymbol",
            exchangeId = "testId",
            percentExchangeVolume = "testVolume",
            priceQuote = "10.0",
            priceUsd = "10.0",
            quoteId = "testId",
            quoteSymbol = "testSymbol",
            rank = "1",
            tradesCount24Hr = "10.0",
            updated = 1000,
            volumeUsd24Hr = "10.0"
        )

        val testMarket = Market(
            exchangeId = "testId",
            baseId = "testId",
            baseSymbol = "testSymbol",
            rank = 1,
            quoteId = "testId",
            quoteSymbol = "testSymbol",
            percentExchangeVolume = 10.0,
            priceQuote = 10.0,
            priceUsd = 10.0,
            tradesCount24Hr = 10.0,
            volumeUsd24Hr = 10.0,
            updated = 1000
        )

        val testMarketsApiData = MarketsApiData(marketData = listOf(testMarketData), 1000)
        coEvery { coincapService.getMarkets("testAssetId") } returns testMarketsApiData
        coEvery { apiCallExecutor.performCall<MarketsApiData>(any(), any()) } returns Result.success(testMarketsApiData)
        every { marketConverter.toModel(testMarketData) } returns testMarket

        // When
        val result = sut.getMarketForAsset("testAssetId")

        // Then
        Assert.assertTrue(result.isSuccess)
        val resultData = result.getOrElse { Assert.fail("Result didn't contain market data.") }
        Assert.assertEquals("Result data didn't match expected market.", resultData, testMarket)
    }

    @Test
    fun `getMarketForAsset() returns Result failure() when response is empty`() = runTest {
        // Given
        val testMarketsApiData = MarketsApiData(marketData = listOf(), 1000)
        val expectedException = NetworkException.InvalidResponseException()
        coEvery { coincapService.getMarkets("testAssetId") } returns testMarketsApiData
        coEvery { apiCallExecutor.performCall<MarketsApiData>(any(), any()) } returns Result.failure(expectedException)

        // When
        val result = sut.getMarketForAsset("testAssetId")

        // Then
        Assert.assertTrue(result.isFailure)
        Assert.assertEquals("Expected exception was not returned in the failure result!", expectedException, result.exceptionOrNull())
    }

    @Test
    fun `getMarketForAsset() returns Result failure() when API call fails`() = runTest {
        // Given
        val expectedException = TestException()
        coEvery { coincapService.getMarkets("testAssetId") } throws expectedException
        coEvery { apiCallExecutor.performCall<MarketsApiData>(any(), any()) } returns Result.failure(expectedException)

        // When
        val result = sut.getMarketForAsset("testAssetId")

        // Then
        Assert.assertTrue(result.isFailure)
        val returnedException = result.exceptionOrNull()
        Assert.assertTrue(returnedException is TestException)
        Assert.assertEquals("Expected exception was not returned in the failure result!", expectedException, returnedException)
    }
}