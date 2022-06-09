package com.yoti.android.cryptocurrencychallenge.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Header5(modifier: Modifier = Modifier, text: String) {
    Text(modifier = modifier, text = text, style = MaterialTheme.typography.h5)
}

@Composable
fun Subtitle1(modifier: Modifier = Modifier, text: String) {
    Text(modifier = modifier, text = text, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun Caption(modifier: Modifier = Modifier, text: String) {
    Text(modifier = modifier, text = text, style = MaterialTheme.typography.caption)
}
