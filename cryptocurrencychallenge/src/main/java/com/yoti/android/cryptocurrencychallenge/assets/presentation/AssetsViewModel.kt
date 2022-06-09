package com.yoti.android.cryptocurrencychallenge.assets.presentation

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.assets.domain.Asset
import com.yoti.android.cryptocurrencychallenge.assets.domain.GetAssetsUseCase
import com.yoti.android.cryptocurrencychallenge.assets.domain.LoadAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val loadAssetsUseCase: LoadAssetsUseCase
) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> = MutableLiveData()
    val uiState: LiveData<UiState> get() = _uiState

    private val _uiEvents: LiveEvent<UiEvent> = LiveEvent()
    val uiEvents: LiveData<UiEvent> get() = _uiEvents

    init {
        performInitialLoading()
    }

    fun onRefreshTriggered() {
        Log.d(TAG, "onRefreshTriggered()")
        viewModelScope.launch { loadAssets() }
    }

    fun onAssetListItemClicked(assetUiItem: AssetUiItem) {
        Log.d(TAG, "onAssetListItemClicked(): $assetUiItem")
        viewModelScope.launch {
            _uiEvents.value = UiEvent.NavigateToMarket(assetUiItem.assetId)
        }
    }

    private fun performInitialLoading() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            loadAssets()
            getAssetsUseCase().collect { assets -> handleNewAssets(assets) }
        }
    }

    @MainThread
    private suspend fun loadAssets() {
        loadAssetsUseCase()
            .onFailure { _uiEvents.value = UiEvent.Error(R.string.error_refreshing_assets) }
    }

    @MainThread
    private suspend fun handleNewAssets(assets: List<Asset>) {
        if (assets.isEmpty()) {
            Log.d(TAG, "onAssetsReceived(): Received no local assets, fetching from api...")
            loadAssets()
            _uiState.value = UiState.Empty
        } else {
            Log.d(TAG, "onAssetsReceived(): Received local assets $assets")
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

    sealed class UiEvent {
        data class Error(@StringRes val messageRes: Int) : UiEvent()
        data class NavigateToMarket(val assetId: String) : UiEvent()
    }

    companion object {
        private const val TAG = "AssetsViewModel"
    }
}