package com.eliascoelho911.cookzy.feature.recipedetail

import com.eliascoelho911.cookzy.R
import java.math.BigDecimal

/**
 * Preview data factories for Recipe Detail feature.
 */
object RecipeDetailPreviewData {
    fun stateLoading(): RecipeDetailUiState = RecipeDetailUiState(
        isLoading = true
    )

    fun stateError(): RecipeDetailUiState = RecipeDetailUiState(
        isLoading = false,
        errorMessageRes = R.string.error_recipe_not_found
    )

    fun stateSuccess(): RecipeDetailUiState = RecipeDetailUiState(
        recipeId = 10L,
        title = "Pão Caseiro",
        ingredients = listOf(
            IngredientDetailUi(
                rawText = "500 g de farinha de trigo",
                derivedQuantity = BigDecimal("500"),
                // Destaque do número "500"
                highlightRange = 0..2
            ),
            IngredientDetailUi(
                rawText = "10 g de fermento biológico seco",
                derivedQuantity = BigDecimal("10"),
                highlightRange = 0..1
            ),
            IngredientDetailUi(
                rawText = "300 ml de água morna",
                derivedQuantity = BigDecimal("300"),
                highlightRange = 0..2
            ),
            IngredientDetailUi(
                rawText = "1 colher (chá) de sal",
                derivedQuantity = BigDecimal.ONE,
                // Destaque do "1"
                highlightRange = 0..0
            )
        ),
        steps = listOf(
            StepDetailUi(position = 1, description = "Misture os secos."),
            StepDetailUi(position = 2, description = "Adicione a água e sove por 10 minutos."),
            StepDetailUi(position = 3, description = "Deixe crescer até dobrar de volume."),
            StepDetailUi(position = 4, description = "Asse em forno pré‑aquecido a 200°C por 35–40 min.")
        ),
        isLoading = false
    )
}

