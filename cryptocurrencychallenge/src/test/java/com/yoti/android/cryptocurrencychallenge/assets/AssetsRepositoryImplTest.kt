package com.yoti.android.cryptocurrencychallenge.assets

import com.yoti.android.cryptocurrencychallenge.TestException
import com.yoti.android.cryptocurrencychallenge.assets.data.AssetsConverter
import com.yoti.android.cryptocurrencychallenge.assets.data.AssetsRepositoryImpl
import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetEntity
import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetsDao
import com.yoti.android.cryptocurrencychallenge.assets.data.network.AssetData
import com.yoti.android.cryptocurrencychallenge.assets.data.network.AssetsApiData
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.data.ApiCallExecutor
import com.yoti.android.cryptocurrencychallenge.data.CoincapService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsRepositoryImplTest {

    @MockK
    lateinit var assetsDao: AssetsDao

    @MockK
    lateinit var coincapService: CoincapService

    @MockK
    lateinit var apiCallExecutor: ApiCallExecutor

    @MockK
    lateinit var assetsConverter: AssetsConverter

    @InjectMockKs
    lateinit var sut: AssetsRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getAssets() emits and maps each change from the source`() = runTest {
        // Given
        val testEntity1 = AssetEntity("testId1")
        val testAsset1 = Asset("testId1")
        val testEntity2 = AssetEntity("testId2")
        val testAsset2 = Asset("testId2")
        val testAssetsFlow = flow {
            emit(listOf(testEntity1))
            emit(listOf(testEntity2))
        }
        every { assetsDao.getAll() } returns testAssetsFlow
        every { assetsConverter.toModel(testEntity1) } returns testAsset1
        every { assetsConverter.toModel(testEntity2) } returns testAsset2

        // When
        val assetEmissions = sut.getAssets().toList()

        // Then
        assertEquals(assetEmissions.size, 2)
        assertEquals(assetEmissions[0].first(), testAsset1)
        assertEquals(assetEmissions[1].first(), testAsset2)
    }

    @Test
    fun `fetchAssets() calls to perform api call and converts the results if successful`() = runTest {
        // Given
        val testAssetData = AssetData("", "", "", "", "", "", "", "", "", "", "", "")
        val testAssetApiData = AssetsApiData(listOf(testAssetData), 1000)
        val testAsset = Asset("testId")
        coEvery { apiCallExecutor.performCall<AssetsApiData>(any()) } returns Result.success(testAssetApiData)
        every { assetsConverter.toModel(testAssetData) } returns testAsset

        // When
        val assets = sut.fetchAssets()

        // Then
        assertTrue(assets.isSuccess)
        assertEquals(testAsset, assets.getOrNull()?.first())
    }

    @Test
    fun `fetchAssets() calls to perform api call and sends failure upstream if call failed`() = runTest {
        // Given
        val thrownException = TestException()
        coEvery { apiCallExecutor.performCall<AssetsApiData>(any()) } returns Result.failure(thrownException)

        // When
        val assets = sut.fetchAssets()

        // Then
        assertTrue(assets.isFailure)
        assertEquals(thrownException, assets.exceptionOrNull())
    }

    @Test
    fun `fetchAssets() calls to perform api call and sends failure if conversion raised errors`() = runTest {
        // Given
        val thrownException = TestException()
        val testAssetData = AssetData("", "", "", "", "", "", "", "", "", "", "", "")
        val testAssetApiData = AssetsApiData(listOf(testAssetData), 1000)
        coEvery { apiCallExecutor.performCall<AssetsApiData>(any()) } returns Result.success(testAssetApiData)
        every { assetsConverter.toModel(testAssetData) } throws thrownException

        // When
        val assets = sut.fetchAssets()

        // Then
        assertTrue(assets.isFailure)
        assertEquals(thrownException, assets.exceptionOrNull())
    }

    @Test
    fun `saveAssets() replaces the existing assets data`() = runTest {
        // Given
        val testAsset1 = Asset("testId1")
        val testAsset2 = Asset("testId2")
        val testEntity1 = AssetEntity("testId1")
        val testEntity2 = AssetEntity("testId2")
        val testAssets = listOf(testAsset1, testAsset2)
        val testEntities = listOf(testEntity1, testEntity2)
        every { assetsConverter.toEntity(testAsset1) } returns testEntity1
        every { assetsConverter.toEntity(testAsset2) } returns testEntity2
        coJustRun { assetsDao.replaceAll(testEntities) }

        // When
        sut.saveAssets(testAssets)

        // Then
        coVerify { assetsDao.replaceAll(testEntities) }
    }
}