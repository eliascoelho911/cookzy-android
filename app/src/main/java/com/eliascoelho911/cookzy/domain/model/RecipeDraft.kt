package com.eliascoelho911.cookzy.domain.model

data class RecipeDraft(
    val title: String,
    val ingredients: List<RecipeIngredient>,
    val steps: List<RecipeStep>
)
