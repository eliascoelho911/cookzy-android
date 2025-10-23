package com.eliascoelho911.cookzy.domain.repository

import com.eliascoelho911.cookzy.domain.model.RecipeListLayout
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeRecipeListLayout(): Flow<RecipeListLayout>
    suspend fun setRecipeListLayout(layout: RecipeListLayout)
}

