package com.yoti.android.cryptocurrencychallenge.assets

import com.yoti.android.cryptocurrencychallenge.TestException
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.assets.domain.AssetsRepository
import com.yoti.android.cryptocurrencychallenge.assets.domain.LoadAssetsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadAssetsUseCaseTest {

    @MockK
    lateinit var assetsRepository: AssetsRepository

    @InjectMockKs
    lateinit var sut: LoadAssetsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke() returns success and saves assets if api call is successful and response is not empty`() = runTest {
        // Given
        val testAssetsList = listOf(Asset(id = "testId"))
        coEvery { assetsRepository.fetchAssets() } returns Result.success(testAssetsList)
        coJustRun { assetsRepository.saveAssets(testAssetsList) }

        // When
        val result = sut.invoke()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { assetsRepository.saveAssets(testAssetsList) }
    }

    @Test
    fun `invoke() returns success and doesn't save assets if api call is successful but response is empty`() = runTest {
        // Given
        val testAssetsList = listOf<Asset>()
        coEvery { assetsRepository.fetchAssets() } returns Result.success(testAssetsList)

        // When
        val result = sut.invoke()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify(exactly = 0) { assetsRepository.saveAssets(testAssetsList) }
    }

    @Test
    fun `invoke() returns failure and doesn't save assets if api call fails`() = runTest {
        // Given
        val expectedException = TestException()
        coEvery { assetsRepository.fetchAssets() } returns Result.failure(expectedException)

        // When
        val result = sut.invoke()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify(exactly = 0) { assetsRepository.saveAssets(any()) }
    }
}