package com.yoti.android.cryptocurrencychallenge.assets.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.presentation.common.Caption
import com.yoti.android.cryptocurrencychallenge.presentation.common.FullScreenError
import com.yoti.android.cryptocurrencychallenge.presentation.common.FullScreenLoadingIndicator
import com.yoti.android.cryptocurrencychallenge.presentation.common.Subtitle1
import com.yoti.android.cryptocurrencychallenge.presentation.common.InformationMessage

@ExperimentalMaterialApi
@Composable
fun AssetsScreen(
    viewModel: AssetsViewModel,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavigationTriggered: (destination: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val isRefreshingState by viewModel.isRefreshing.collectAsState()
    val navigationDestinationState by viewModel.navigationState.collectAsState()

    navigationDestinationState?.let { destination ->
        onNavigationTriggered(destination)
        viewModel.onNavigationPerformed()
    }

    errorState?.messageRes?.let { errorMessageRes ->
        InformationMessage(
            scaffoldState = scaffoldState,
            messageRes = errorMessageRes,
            onMessageDisplayed = { viewModel.onErrorAcknowledged() }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                title = { Text(text = stringResource(id = R.string.assets_title)) },
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is AssetsViewModel.UiState.Success -> AssetsContent(
                modifier = Modifier.padding(paddingValues),
                assetItems = state.items,
                isRefreshing = isRefreshingState,
                onSwipeToRefresh = { viewModel.onRefreshTriggered() },
                onAssetItemClicked = { assetItem -> viewModel.onAssetListItemClicked(assetItem) }
            )
            is AssetsViewModel.UiState.Empty -> FullScreenError(
                errorMessage = stringResource(id = R.string.no_assets_message),
                errorIcon = Icons.Default.Clear,
                onRetry = { viewModel.onRefreshTriggered() }
            )
            is AssetsViewModel.UiState.Loading -> FullScreenLoadingIndicator()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AssetsContent(
    modifier: Modifier = Modifier,
    assetItems: List<AssetUiItem>,
    isRefreshing: Boolean,
    onSwipeToRefresh: () -> Unit,
    onAssetItemClicked: (AssetUiItem) -> Unit
) {
    SwipeRefresh(modifier = modifier, state = rememberSwipeRefreshState(isRefreshing), onRefresh = onSwipeToRefresh) {
        LazyColumn {
            items(assetItems.size) { index ->
                AssetListItem(
                    assetUiItem = assetItems[index],
                    onItemClicked = { assetItem -> onAssetItemClicked(assetItem) }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AssetListItem(assetUiItem: AssetUiItem, onItemClicked: (AssetUiItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(all = 8.dp),
        elevation = 4.dp,
        onClick = { onItemClicked(assetUiItem) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Subtitle1(text = assetUiItem.name)
            Row(Modifier.padding(top = 12.dp)) {
                Caption(text = assetUiItem.symbol)
                Caption(modifier = Modifier.padding(start = 16.dp), text = assetUiItem.price)
            }
        }
    }
}