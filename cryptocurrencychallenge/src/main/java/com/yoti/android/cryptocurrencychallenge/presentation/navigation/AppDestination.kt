package com.yoti.android.cryptocurrencychallenge.presentation.navigation

sealed class AppDestination {
    object Assets: AppDestination()
    data class Market(val assetId: String): AppDestination()
}