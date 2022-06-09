package com.yoti.android.cryptocurrencychallenge.assets

import com.yoti.android.cryptocurrencychallenge.assets.data.AssetsRepositoryImpl
import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetsDao
import com.yoti.android.cryptocurrencychallenge.assets.domain.AssetsRepository
import com.yoti.android.cryptocurrencychallenge.data.CryptoDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AssetsModule {

    @Binds
    abstract fun bindAssetsRepository(assetsRepositoryImpl: AssetsRepositoryImpl): AssetsRepository

    companion object {
        @Provides
        fun provideAssetsDao(cryptoDatabase: CryptoDatabase): AssetsDao = cryptoDatabase.assetsDao()
    }
}