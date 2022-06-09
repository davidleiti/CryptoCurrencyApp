package com.yoti.android.cryptocurrencychallenge.market

import com.yoti.android.cryptocurrencychallenge.market.data.MarketRepositoryImpl
import com.yoti.android.cryptocurrencychallenge.market.domain.MarketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface MarketModule {

    @Binds
    fun bindMarketRepository(marketRepositoryImpl: MarketRepositoryImpl): MarketRepository
}