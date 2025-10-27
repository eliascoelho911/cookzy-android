package com.eliascoelho911.cookzy.ui

import androidx.compose.runtime.Composable
import com.eliascoelho911.cookzy.ds.theme.AppTheme

@Composable
fun CookzyApp(
    finishApp: () -> Unit
) {
    AppTheme {
        CookzyNavHost(
            finishApp = finishApp
        )
    }
}
