package com.eliascoelho911.cookzy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eliascoelho911.cookzy.ds.theme.AppTheme

@Composable
fun CookzyApp(
    finishApp: () -> Unit
) {
    AppTheme {
        CookzyNavHost(
            finishApp = finishApp,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
    }
}
