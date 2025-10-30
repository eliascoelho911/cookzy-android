package com.eliascoelho911.cookzy.domain.model

data class Recipe(
    val id: Long,
    val title: String,
    val ingredients: List<RecipeIngredient>,
    val steps: List<RecipeStep>,
    val updatedAt: Long
)
