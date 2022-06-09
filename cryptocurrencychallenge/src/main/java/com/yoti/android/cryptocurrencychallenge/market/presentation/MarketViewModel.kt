package com.yoti.android.cryptocurrencychallenge.market.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.market.domain.GetMarketForAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _uiState: MutableLiveData<UiState> = MutableLiveData()
    val uiState: LiveData<UiState> get() = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            loadMarket(assetId)
        }
    }

    fun onRefresh() {
        viewModelScope.launch { loadMarket(assetId) }
    }

    private suspend fun loadMarket(assetId: String) {
        getMarketsUseCase(assetId)
            .onFailure { _uiState.value = UiState.Error(R.string.error_message_long) }
            .onSuccess { market ->
                val uiModel = marketUiFormatter.formatMarket(market)
                _uiState.value = UiState.Success(uiModel)
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