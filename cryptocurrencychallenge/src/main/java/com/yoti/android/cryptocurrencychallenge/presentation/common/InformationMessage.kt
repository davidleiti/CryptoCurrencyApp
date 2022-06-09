package com.yoti.android.cryptocurrencychallenge.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.yoti.android.cryptocurrencychallenge.R

@Composable
fun InformationMessage(scaffoldState: ScaffoldState, @StringRes messageRes: Int, onMessageDisplayed: (() -> Unit)? = null) {
    val errorMessage = stringResource(id = messageRes)
    val actionMessage = stringResource(id = R.string.ok_label)
    LaunchedEffect(scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(
            message = errorMessage,
            actionLabel = actionMessage
        )
        onMessageDisplayed?.invoke()
    }
}