package com.yoti.android.cryptocurrencychallenge.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.yoti.android.cryptocurrencychallenge.assets.presentation.AssetsScreen
import com.yoti.android.cryptocurrencychallenge.market.presentation.MarketScreen

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun CryptoApp() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "assets"
    ) {
        composable(route = "assets") {
            AssetsScreen(
                viewModel = hiltViewModel(),
                onNavigationTriggered = { destination -> navController.navigate(destination) }
            )
        }
        composable(route = "market/{assetId}") {
            MarketScreen(viewModel = hiltViewModel(), navController = navController)
        }
    }
}