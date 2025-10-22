package com.eliascoelho911.cookzy.feature.recipelist

/**
 * Preview data factories for Recipe List feature.
 */
object RecipeListPreviewData {
    fun sampleItems(): List<RecipeSummaryUi> = listOf(
        RecipeSummaryUi(id = 1L, title = "Pão Caseiro"),
        RecipeSummaryUi(id = 2L, title = "Bolo de Fubá"),
        RecipeSummaryUi(id = 3L, title = "Lasanha de Legumes"),
        RecipeSummaryUi(id = 4L, title = "Panqueca Integral"),
        RecipeSummaryUi(id = 5L, title = "Sopa de Abóbora")
    )

    fun stateEmpty(): RecipeListUiState = RecipeListUiState(
        recipes = emptyList(),
        isEmpty = true
    )

    fun statePopulated(): RecipeListUiState = RecipeListUiState(
        recipes = sampleItems(),
        isEmpty = false
    )
}

