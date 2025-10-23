package com.eliascoelho911.cookzy.feature.recipelist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class RecipeListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsRecentsAndHeaderCount() {
        val state = RecipeListPreviewData.statePopulated()
        composeRule.setContent {
            RecipeListScreen(
                state = state,
                onCreateRecipe = {},
                onRecipeSelected = {},
                onToggleSearch = {},
                onSearchQueryChanged = {},
                onClearSearch = {},
                onToggleLayout = {},
                onRetry = {}
            )
        }

        composeRule.onNodeWithText("Recentes").assertIsDisplayed()
        composeRule.onNodeWithText("5 receitas").assertIsDisplayed()
        // Ensure out-of-scope carousel is not present in MVP
        composeRule.onNodeWithText("Livros de Receitas").assertDoesNotExist()
    }
}
