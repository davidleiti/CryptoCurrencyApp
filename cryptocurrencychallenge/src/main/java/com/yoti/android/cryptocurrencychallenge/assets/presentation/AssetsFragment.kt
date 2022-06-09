package com.yoti.android.cryptocurrencychallenge.assets.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yoti.android.cryptocurrencychallenge.R
import com.yoti.android.cryptocurrencychallenge.databinding.FragmentAssetsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetsFragment : Fragment() {

    private var binding: FragmentAssetsBinding? = null
    private lateinit var assetsAdapter: AssetsAdapter
    private val viewModel: AssetsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAssetsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.setupAssetsList()
        viewModel.uiEvents.observe(viewLifecycleOwner, ::handleUiEvent)
        viewModel.uiState.observe(viewLifecycleOwner, ::updateUiState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun updateUiState(uiState: AssetsViewModel.UiState) {
        binding?.apply {
            progressBarAssets.isVisible = uiState is AssetsViewModel.UiState.Loading
            noAssetsView.isVisible = uiState is AssetsViewModel.UiState.Empty
            swipeToRefresh.isRefreshing = false

            if (uiState is AssetsViewModel.UiState.Success) {
                assetsAdapter.submitList(uiState.items)
                recyclerViewAssets.isVisible = true
            } else {
                recyclerViewAssets.isVisible = false
            }
        }
    }

    private fun handleUiEvent(uiEvent: AssetsViewModel.UiEvent) {
        when (uiEvent) {
            is AssetsViewModel.UiEvent.Error -> {
                binding?.root?.let { view ->
                    Snackbar.make(view, uiEvent.messageRes, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.ok_label) { }
                        .show()
                }
            }
            is AssetsViewModel.UiEvent.NavigateToMarket -> {
                findNavController().navigate(AssetsFragmentDirections.navigateToMarket(uiEvent.assetId))
            }
        }
    }

    private fun FragmentAssetsBinding.setupAssetsList() {
        assetsAdapter = AssetsAdapter { assetItem -> viewModel.onAssetListItemClicked(assetItem) }
        recyclerViewAssets.adapter = assetsAdapter
        recyclerViewAssets.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        swipeToRefresh.setOnRefreshListener { viewModel.onRefreshTriggered() }
    }
}