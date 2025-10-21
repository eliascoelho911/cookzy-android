package com.eliascoelho911.cookzy.navigation

import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable

private const val DEEP_LINK_BASE = "cookzy://recipes"

@Serializable
data object RecipeListDestination

@Serializable
data class RecipeEditorDestination(
    val recipeId: Long? = null
)

@Serializable
data class RecipeDetailDestination(
    val recipeId: Long
)

val RecipeEditorDeepLinks = listOf(
    navDeepLink<RecipeEditorDestination>(basePath = "$DEEP_LINK_BASE/editor")
)

val RecipeDetailDeepLinks = listOf(
    navDeepLink<RecipeDetailDestination>(basePath = "$DEEP_LINK_BASE/detail")
)
