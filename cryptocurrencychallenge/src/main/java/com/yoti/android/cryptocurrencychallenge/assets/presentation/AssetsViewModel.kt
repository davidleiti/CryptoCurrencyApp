package com.yoti.android.cryptocurrencychallenge.assets.presentation

import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.assets.domain.GetAssetsUseCase
import com.yoti.android.cryptocurrencychallenge.assets.domain.LoadAssetsUseCase
import com.yoti.android.cryptocurrencychallenge.presentation.navigation.AppDestination
import com.yoti.android.cryptocurrencychallenge.presentation.navigation.AppDestinationRouteProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val loadAssetsUseCase: LoadAssetsUseCase,
    private val appDestinationRouteProvider: AppDestinationRouteProvider
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorState: MutableStateFlow<AssetsError?> = MutableStateFlow(null)
    val errorState: StateFlow<AssetsError?> = _errorState.asStateFlow()

    private val _navigationState: MutableStateFlow<String?> = MutableStateFlow(null)
    val navigationState = _navigationState.asStateFlow()

    init {
        performInitialLoading()
    }

    fun onRefreshTriggered() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadAssets()
        }
    }

    fun onErrorAcknowledged() {
        _errorState.value = null
    }

    fun onNavigationPerformed() {
        _navigationState.value = null
    }

    fun onAssetListItemClicked(assetUiItem: AssetUiItem) {
        viewModelScope.launch {
            val navigationDestination = AppDestination.Market(assetUiItem.assetId)
            _navigationState.value = appDestinationRouteProvider.provideDestinationRoute(navigationDestination)
        }
    }

    private fun performInitialLoading() {
        viewModelScope.launch {
            loadAssets()
            getAssetsUseCase().collect { assets -> handleNewAssets(assets) }
        }
    }

    @MainThread
    private suspend fun loadAssets() {
        loadAssetsUseCase()
            .onSuccess { _isRefreshing.value = false }
            .onFailure {
                _errorState.value = AssetsError(R.string.error_refreshing_assets)
                _isRefreshing.value = false
            }
    }

    @MainThread
    private suspend fun handleNewAssets(assets: List<Asset>) {
        if (assets.isEmpty()) {
            loadAssets()
            _uiState.value = UiState.Empty
        } else {
            val assetUiItems = assets.map { asset -> asset.toUiItem() }
            _uiState.value = UiState.Success(assetUiItems)
        }
    }

    private fun Asset.toUiItem(): AssetUiItem = AssetUiItem(
        assetId = id,
        symbol = symbol,
        name = name,
        price = priceUsd.toString()
    )

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Success(val items: List<AssetUiItem>) : UiState()
    }

    data class AssetsError(@StringRes val messageRes: Int)
}