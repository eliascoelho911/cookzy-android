package com.eliascoelho911.cookzy.ui

import androidx.compose.runtime.Composable
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.ui.theme.AppTheme

@Composable
fun CookzyApp(
    repository: RecipeRepository,
    finishApp: () -> Unit
) {
    AppTheme {
        CookzyNavHost(
            repository = repository,
            finishApp = finishApp
        )
    }
}
