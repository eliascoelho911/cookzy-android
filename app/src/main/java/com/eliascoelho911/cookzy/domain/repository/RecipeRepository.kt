package com.eliascoelho911.cookzy.domain.repository

import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import kotlinx.coroutines.flow.Flow

data class RecipeFilters(
    val query: String = "",
    val cookbookIds: Set<Long> = emptySet()
)

data class RecipeFeed(
    val all: List<Recipe>,
    val filtered: List<Recipe>,
    val filters: RecipeFilters
)

interface RecipeRepository {
    suspend fun createRecipe(draft: RecipeDraft): Long
    suspend fun updateRecipe(id: Long, draft: RecipeDraft)
    suspend fun getRecipe(id: Long): Recipe?
    fun observeRecipes(): Flow<RecipeFeed>
    fun setSearchQuery(query: String)
    fun observeRecentRecipes(limit: Int = DEFAULT_RECENT_LIMIT): Flow<List<Recipe>>

    companion object {
        const val DEFAULT_RECENT_LIMIT: Int = 10
    }
}
