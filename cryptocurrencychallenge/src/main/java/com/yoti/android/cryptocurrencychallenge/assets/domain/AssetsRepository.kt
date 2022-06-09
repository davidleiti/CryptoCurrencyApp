package com.yoti.android.cryptocurrencychallenge.assets.domain

import kotlinx.coroutines.flow.Flow

interface AssetsRepository {
    fun getAssets(): Flow<List<Asset>>
    suspend fun fetchAssets(): Result<List<Asset>>
    suspend fun saveAssets(assets: List<Asset>)
}