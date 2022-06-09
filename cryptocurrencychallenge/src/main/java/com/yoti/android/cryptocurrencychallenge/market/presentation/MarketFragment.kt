package com.yoti.android.cryptocurrencychallenge.market.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yoti.android.cryptocurrencychallenge.databinding.FragmentMarketBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MarketFragment : Fragment() {

    private var binding: FragmentMarketBinding? = null
    private val viewModel: MarketViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contentContainer?.setOnRefreshListener { viewModel.onRefresh() }
        viewModel.uiState.observe(viewLifecycleOwner, ::updateUiState)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun updateUiState(uiState: MarketViewModel.UiState) {
        binding?.apply {
            loadingIndicator.isVisible = uiState is MarketViewModel.UiState.Loading
            errorMessageText.isVisible = uiState is MarketViewModel.UiState.Error
            contentContainer.isRefreshing = false

            if (uiState is MarketViewModel.UiState.Success) {
                contentContainer.isVisible = true
                textViewExchangeId.text = uiState.data.exchangeId
                textViewRank.text = uiState.data.rank
                textViewPrice.text = uiState.data.price
                textViewDate.text = uiState.data.date
            } else {
                contentContainer.isVisible = false
            }
        }
    }
}