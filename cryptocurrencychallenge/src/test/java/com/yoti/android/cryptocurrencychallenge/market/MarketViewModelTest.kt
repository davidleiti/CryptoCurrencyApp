package com.yoti.android.cryptocurrencychallenge.market

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.yoti.android.cryptocurrencychallenge.MainDispatcherRule
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.TestException
import com.yoti.android.cryptocurrencychallenge.market.domain.GetMarketForAssetUseCase
import com.yoti.android.cryptocurrencychallenge.market.domain.Market
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketUiFormatter
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketUiModel
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    @MockK
    lateinit var getMarketsUseCase: GetMarketForAssetUseCase

    @MockK
    lateinit var marketUiFormatter: MarketUiFormatter

    lateinit var sut: MarketViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { savedStateHandle.get<String>("assetId") } returns "testId"
    }

    @Test
    fun `uiState value is Success if api call is successful on init`() = runTest {
        // Given
        val testMarket = Market()
        val testUiModel = MarketUiModel("testId", "testRank", "testPrice", "testDate")
        coEvery { getMarketsUseCase.invoke("testId") } returns Result.success(testMarket)
        every { marketUiFormatter.formatMarket(testMarket) } returns testUiModel

        // When
        sut = MarketViewModel(savedStateHandle, getMarketsUseCase, marketUiFormatter)

        // Then
        val expectedState = MarketViewModel.UiState.Success(testUiModel)
        assertEquals(expectedState, sut.uiState.value)
    }

    @Test
    fun `uiState value is Error if api call is not successful on init`() = runTest {
        // Given
        val failureCause = TestException()
        coEvery { getMarketsUseCase.invoke("testId") } returns Result.failure(failureCause)

        // When
        sut = MarketViewModel(savedStateHandle, getMarketsUseCase, marketUiFormatter)

        // Then
        val expectedState = MarketViewModel.UiState.Error(R.string.error_message_long)
        assertEquals(expectedState, sut.uiState.value)
    }

    @Test
    fun `uiState value is Success if refresh is successful`() = runTest {
        // Given
        val testMarket = Market()
        val testUiModel = MarketUiModel("testId", "testRank", "testPrice", "testDate")
        coEvery { getMarketsUseCase.invoke("testId") } returns Result.success(testMarket)
        every { marketUiFormatter.formatMarket(testMarket) } returns testUiModel

        // When
        sut = MarketViewModel(savedStateHandle, getMarketsUseCase, marketUiFormatter)
        sut.onRefresh()

        // Then
        val expectedState = MarketViewModel.UiState.Success(testUiModel)
        assertEquals(expectedState, sut.uiState.value)
    }

    @Test
    fun `uiState value is Error if refresh is not successful after successful loading`() = runTest {
        // Given
        val testMarket = Market()
        val testUiModel = MarketUiModel("testId", "testRank", "testPrice", "testDate")
        coEvery { getMarketsUseCase.invoke("testId") } returns Result.success(testMarket)
        every { marketUiFormatter.formatMarket(testMarket) } returns testUiModel

        // When
        sut = MarketViewModel(savedStateHandle, getMarketsUseCase, marketUiFormatter)
        val failureCause = TestException()
        coEvery { getMarketsUseCase.invoke("testId") } returns Result.failure(failureCause)
        sut.onRefresh()

        // Then
        val expectedState = MarketViewModel.UiState.Error(R.string.error_message_long)
        assertEquals(expectedState, sut.uiState.value)
    }
}