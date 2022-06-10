package com.yoti.android.cryptocurrencychallenge.market.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.presentation.common.FullScreenLoadingIndicator
import com.yoti.android.cryptocurrencychallenge.presentation.common.FullScreenError
import com.yoti.android.cryptocurrencychallenge.presentation.common.Header5
import com.yoti.android.cryptocurrencychallenge.presentation.common.Subtitle1

@Composable
fun MarketScreen(viewModel: MarketViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val onRefresh = { viewModel.onRefresh() }

    Scaffold(
        topBar = { MarketTopBar { navController.popBackStack() } }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        when (val state = uiState) {
            is MarketViewModel.UiState.Loading -> FullScreenLoadingIndicator(modifier)
            is MarketViewModel.UiState.Success -> MarketContent(
                modifier = modifier,
                uiModel = state.data,
                isRefreshing = isRefreshing,
                onSwipeToRefresh = onRefresh
            )
            is MarketViewModel.UiState.Error -> FullScreenError(
                modifier = modifier,
                errorMessage = stringResource(id = R.string.error_message_long),
                onRetry = onRefresh
            )
        }
    }
}

@Composable
fun MarketTopBar(onActionIconClicked: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.market_title)) },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        navigationIcon = {
            IconButton(onClick = onActionIconClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_label))
            }
        },
    )
}

@Composable
fun MarketContent(
    modifier: Modifier = Modifier,
    uiModel: MarketUiModel,
    isRefreshing: Boolean,
    onSwipeToRefresh: () -> Unit
) {
    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = isRefreshing), onRefresh = { onSwipeToRefresh() }) {
        Column(
            modifier
                .fillMaxSize()
                .padding(all = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Header5(text = stringResource(id = R.string.exchange_id_title))
            Subtitle1(modifier = Modifier.padding(bottom = 12.dp), text = uiModel.exchangeId)
            Header5(text = stringResource(id = R.string.rank_title))
            Subtitle1(modifier = Modifier.padding(bottom = 12.dp), text = uiModel.rank)
            Header5(text = stringResource(id = R.string.price_title))
            Subtitle1(modifier = Modifier.padding(bottom = 12.dp), text = uiModel.price)
            Header5(text = stringResource(id = R.string.date_title))
            Subtitle1(modifier = Modifier.padding(bottom = 12.dp), text = uiModel.date)
        }
    }
}


