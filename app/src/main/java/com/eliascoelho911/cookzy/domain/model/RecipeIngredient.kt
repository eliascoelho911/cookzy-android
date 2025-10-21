package com.eliascoelho911.cookzy.domain.model

data class RecipeIngredient(
    val name: String,
    val quantity: String?,
    val unit: String?,
    val note: String?,
    val position: Int
)
