package com.yoti.android.cryptocurrencychallenge.market.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.market.domain.GetMarketForAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMarketsUseCase: GetMarketForAssetUseCase,
    private val marketUiFormatter: MarketUiFormatter
) : ViewModel() {

    private val assetId: String = savedStateHandle[KEY_NAV_ARG_ASSET_ID]
        ?: throw IllegalStateException("No asset ID has been passed for retrieving the market")

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch { loadMarket(assetId) }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadMarket(assetId)
        }
    }

    private suspend fun loadMarket(assetId: String) {
        getMarketsUseCase(assetId)
            .onFailure {
                _uiState.value = UiState.Error(R.string.error_message_long)
                _isRefreshing.value = false
            }
            .onSuccess { market ->
                val uiModel = marketUiFormatter.formatMarket(market)
                _uiState.value = UiState.Success(uiModel)
                _isRefreshing.value = false
            }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: MarketUiModel) : UiState()
        data class Error(@StringRes val messageRes: Int) : UiState()
    }

    companion object {
        private const val KEY_NAV_ARG_ASSET_ID = "assetId"
    }
}