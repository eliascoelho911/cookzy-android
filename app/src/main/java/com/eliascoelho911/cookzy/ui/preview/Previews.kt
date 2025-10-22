package com.eliascoelho911.cookzy.ui.preview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eliascoelho911.cookzy.ui.theme.AppTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreviews

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    AppTheme { content() }
}

