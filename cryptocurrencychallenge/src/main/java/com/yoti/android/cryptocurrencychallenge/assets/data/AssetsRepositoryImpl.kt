package com.yoti.android.cryptocurrencychallenge.assets.data

import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetsDao
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.assets.domain.AssetsRepository
import com.yoti.android.cryptocurrencychallenge.data.ApiCallExecutor
import com.yoti.android.cryptocurrencychallenge.data.CoincapService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class AssetsRepositoryImpl @Inject constructor(
    private val assetsDao: AssetsDao,
    private val coincapService: CoincapService,
    private val apiCallExecutor: ApiCallExecutor,
    private val assetsConverter: AssetsConverter
) : AssetsRepository {

    override fun getAssets(): Flow<List<Asset>> =
        assetsDao.getAll().map { entities ->
            entities.map { entity -> assetsConverter.toModel(entity) }
        }

    override suspend fun fetchAssets(): Result<List<Asset>> {
        return apiCallExecutor.performCall(
            call = { coincapService.getAssets() }
        ).mapCatching { apiData ->
            apiData.assetData.orEmpty().map { assetData -> assetsConverter.toModel(assetData) }
        }
    }

    override suspend fun saveAssets(assets: List<Asset>) {
        val entities = assets.map { model -> assetsConverter.toEntity(model) }
        assetsDao.replaceAll(entities)
    }
}