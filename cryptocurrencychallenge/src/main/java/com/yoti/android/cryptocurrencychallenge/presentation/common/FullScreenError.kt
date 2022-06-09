package com.yoti.android.cryptocurrencychallenge.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoti.android.cryptocurrencychallenge.R

@Composable
fun FullScreenError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    errorIcon: ImageVector? = null,
    errorContentDescription: String? = null,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorIcon?.let { iconVector ->
            Icon(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(36.dp),
                imageVector = iconVector,
                contentDescription = errorContentDescription.orEmpty()
            )
        }
        Text(
            modifier = Modifier
                .wrapContentSize(align = Alignment.Center)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            text = errorMessage,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text(
                text = stringResource(id = R.string.retry_label),
                style = MaterialTheme.typography.button,
                fontSize = 16.sp
            )
        }
    }
}