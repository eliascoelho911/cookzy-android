package com.eliascoelho911.cookzy.domain.repository

import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun createRecipe(draft: RecipeDraft): Long
    suspend fun updateRecipe(id: Long, draft: RecipeDraft)
    suspend fun getRecipe(id: Long): Recipe?
    fun observeRecipes(): Flow<List<Recipe>>
}
