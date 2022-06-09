package com.yoti.android.cryptocurrencychallenge.presentation.navigation

import javax.inject.Inject

class AppDestinationRouteProvider @Inject constructor() {

    fun provideDestinationRoute(appDestination: AppDestination): String {
        return when (appDestination) {
            AppDestination.Assets -> "assets"
            is AppDestination.Market -> "market/${appDestination.assetId}"
        }
    }
}