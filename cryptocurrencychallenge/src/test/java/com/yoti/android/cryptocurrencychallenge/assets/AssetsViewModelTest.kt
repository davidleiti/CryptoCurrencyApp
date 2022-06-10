package com.yoti.android.cryptocurrencychallenge.assets

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yoti.android.cryptocurrencychallenge.MainDispatcherRule
import com.yoti.android.cryptocurrencychallenge.TestException
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.assets.domain.GetAssetsUseCase
import com.yoti.android.cryptocurrencychallenge.assets.domain.LoadAssetsUseCase
import com.yoti.android.cryptocurrencychallenge.assets.presentation.AssetUiItem
import com.yoti.android.cryptocurrencychallenge.assets.presentation.AssetsViewModel
import com.yoti.android.cryptocurrencychallenge.presentation.navigation.AppDestination
import com.yoti.android.cryptocurrencychallenge.presentation.navigation.AppDestinationRouteProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var getAssetsUseCase: GetAssetsUseCase

    @MockK
    lateinit var loadAssetsUseCase: LoadAssetsUseCase

    @MockK
    lateinit var appDestinationRouteProvider: AppDestinationRouteProvider

    private lateinit var sut: AssetsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 1
    }

    @Test
    fun `performInitialLoading() loads the new assets and collects the local assets source`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val mockFlow = mockk<Flow<List<Asset>>>()
        every { getAssetsUseCase() } returns mockFlow
        coJustRun { mockFlow.collect(any()) }

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)

        // Then
        coVerify { loadAssetsUseCase() }
        coVerify { mockFlow.collect(any()) }
        assertEquals(AssetsViewModel.UiState.Loading, sut.uiState.value)
    }

    @Test
    fun `performInitialLoading() emits AssetsError if assets loading fails`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.failure(TestException())
        val mockFlow = mockk<Flow<List<Asset>>>()
        every { getAssetsUseCase() } returns mockFlow
        coJustRun { mockFlow.collect(any()) }

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)

        // Then
        assertEquals(AssetsViewModel.AssetsError(R.string.error_refreshing_assets), sut.errorState.value)
    }

    @Test
    fun `performInitialLoading() updates the uiState if assets are received from upstream flow`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val testFlow = flow { emit(listOf(Asset("testId"))) }
        every { getAssetsUseCase() } returns testFlow

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)

        // Then
        val expectedUiItems = listOf(AssetUiItem("testId", "", "", "0.0"))
        assertEquals(AssetsViewModel.UiState.Success(expectedUiItems), sut.uiState.value)
    }

    @Test
    fun `performInitialLoading() updates the uiState and loads the new assets if empty assets list is received from the upstream flow`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val testFlow: Flow<List<Asset>> = flow { emit(listOf()) }
        every { getAssetsUseCase() } returns testFlow

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)

        // Then
        assertEquals(AssetsViewModel.UiState.Empty, sut.uiState.value)
        coVerify(exactly = 2) { loadAssetsUseCase() }
    }

    @Test
    fun `onRefreshTriggered() reloads the assets`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val testFlow = flow { emit(listOf(Asset("testId"))) }
        every { getAssetsUseCase() } returns testFlow

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)
        sut.onRefreshTriggered()

        // Then
        val expectedUiItems = listOf(AssetUiItem("testId", "", "", "0.0"))
        assertEquals(AssetsViewModel.UiState.Success(expectedUiItems), sut.uiState.value)
        coVerify(exactly = 2) { loadAssetsUseCase() }
    }

    @Test
    fun `onRefreshTriggered() displays Error uiEvent and doesn't change the existing uiState`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val testFlow = flow { emit(listOf(Asset("testId"))) }
        every { getAssetsUseCase() } returns testFlow

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)
        coEvery { loadAssetsUseCase() } returns Result.failure(TestException())
        sut.onRefreshTriggered()

        // Then
        val expectedUiItems = listOf(AssetUiItem("testId", "", "", "0.0"))
        assertEquals(AssetsViewModel.UiState.Success(expectedUiItems), sut.uiState.value)
        assertEquals(AssetsViewModel.AssetsError(R.string.error_refreshing_assets), sut.errorState.value)
        coVerify(exactly = 2) { loadAssetsUseCase() }
    }

    @Test
    fun `onAssetListItemClicked() emits NavigateToMarket uiEvent with the same assetId`() = runTest {
        // Given
        coEvery { loadAssetsUseCase() } returns Result.success(Unit)
        val testFlow = flow { emit(listOf(Asset("testId"))) }
        every { getAssetsUseCase() } returns testFlow
        val testAssetUiItem = AssetUiItem("testId", "", "", "0.0")
        every { appDestinationRouteProvider.provideDestinationRoute(AppDestination.Market("testId")) } returns "testDestination"

        // When
        sut = AssetsViewModel(getAssetsUseCase, loadAssetsUseCase, appDestinationRouteProvider)
        sut.onAssetListItemClicked(testAssetUiItem)

        // Then
        assertEquals("testDestination", sut.navigationState.value)
    }
}