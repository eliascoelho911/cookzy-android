package com.eliascoelho911.cookzy.feature.recipeeditor

/**
 * Preview data factories for Recipe Editor feature.
 */
object RecipeEditorPreviewData {
    fun stateNew(): RecipeEditorUiState = RecipeEditorUiState(
        title = "",
        ingredientInputs = listOf(
            IngredientInput(id = "ing-1", rawText = "500 g de farinha de trigo"),
            IngredientInput(id = "ing-2", rawText = "300 ml de água morna")
        ),
        stepInputs = listOf(
            StepInput(id = "step-1", description = "Misture os secos."),
            StepInput(id = "step-2", description = "Adicione água e sove por 10 minutos.")
        ),
        isLoading = false,
        isSaving = false,
        isEditing = false
    )

    fun stateEditing(): RecipeEditorUiState = RecipeEditorUiState(
        title = "Pão Caseiro",
        ingredientInputs = listOf(
            IngredientInput(id = "ing-1", rawText = "500 g de farinha de trigo"),
            IngredientInput(id = "ing-2", rawText = "10 g de fermento biológico seco"),
            IngredientInput(id = "ing-3", rawText = "300 ml de água morna")
        ),
        stepInputs = listOf(
            StepInput(id = "step-1", description = "Misture os secos."),
            StepInput(id = "step-2", description = "Adicione água e sove por 10 minutos."),
            StepInput(id = "step-3", description = "Deixe crescer até dobrar de volume.")
        ),
        isLoading = false,
        isSaving = false,
        isEditing = true
    )

    fun stateSaving(): RecipeEditorUiState = stateEditing().copy(isSaving = true)
}

