package com.yoti.android.cryptocurrencychallenge.assets.domain

import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import javax.inject.Inject

class GetAssetsUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository
) {
    operator fun invoke(): Flow<List<Asset>> = assetsRepository.getAssets()
}