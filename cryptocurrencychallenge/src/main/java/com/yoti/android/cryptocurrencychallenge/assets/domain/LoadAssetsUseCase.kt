package com.yoti.android.cryptocurrencychallenge.assets.domain

import java.lang.IllegalStateException
import javax.inject.Inject

class LoadAssetsUseCase @Inject constructor(
    private val assetsRepository: AssetsRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val fetchResult = assetsRepository.fetchAssets()
        if (fetchResult.isFailure) {
            return Result.failure(fetchResult.exceptionOrNull() ?: IllegalStateException("Unknown error"))
        }

        fetchResult.getOrNull()?.takeIf { it.isNotEmpty() }?.let { assets ->
            assetsRepository.saveAssets(assets)
        }
        return Result.success(Unit)
    }
}