package com.eliascoelho911.cookzy.data.repository

import com.eliascoelho911.cookzy.data.local.dao.RecipeDao
import com.eliascoelho911.cookzy.data.local.entity.IngredientEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeStepEntity
import com.eliascoelho911.cookzy.data.local.model.RecipeWithDetails
import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeFeed
import com.eliascoelho911.cookzy.domain.repository.RecipeFilters
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class OfflineRecipeRepository(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    private val filters = MutableStateFlow(RecipeFilters())

    override suspend fun createRecipe(draft: RecipeDraft): Long {
        val recipeId = recipeDao.insertRecipe(
            RecipeEntity(
                title = draft.title.trim(),
                updatedAt = System.currentTimeMillis()
            )
        )
        persistChildren(recipeId, draft)
        return recipeId
    }

    override suspend fun updateRecipe(id: Long, draft: RecipeDraft) {
        recipeDao.updateRecipe(
            RecipeEntity(
                id = id,
                title = draft.title.trim(),
                updatedAt = System.currentTimeMillis()
            )
        )
        recipeDao.deleteIngredientsByRecipeId(id)
        recipeDao.deleteStepsByRecipeId(id)
        persistChildren(id, draft)
    }

    override suspend fun getRecipe(id: Long): Recipe? =
        recipeDao.getRecipeWithDetails(id)?.toDomain()

    override fun observeRecipes(): Flow<RecipeFeed> =
        combine(
            recipeDao.observeRecipes().map { recipes ->
                recipes.map { it.toDomain() }
            },
            filters
        ) { recipes, currentFilters ->
            RecipeFeed(
                all = recipes,
                filtered = applyFilters(recipes, currentFilters),
                filters = currentFilters
            )
        }

    override fun setSearchQuery(query: String) {
        filters.update { current ->
            if (current.query == query) {
                current
            } else {
                current.copy(query = query)
            }
        }
    }

    override fun observeRecentRecipes(limit: Int): Flow<List<Recipe>> =
        recipeDao.observeRecentRecipes(limit).map { recipes ->
            recipes.map { it.toDomain() }
        }

    private suspend fun persistChildren(recipeId: Long, draft: RecipeDraft) {
        val sanitizedIngredients = draft.ingredients
            .sortedBy { it.position }
            .mapIndexed { index, ingredient ->
                IngredientEntity(
                    recipeId = recipeId,
                    position = index,
                    rawText = ingredient.rawText.trim()
                )
            }
            .filter { it.rawText.isNotEmpty() }

        if (sanitizedIngredients.isNotEmpty()) {
            recipeDao.insertIngredients(sanitizedIngredients)
        }

        val sanitizedSteps = draft.steps
            .sortedBy { it.position }
            .mapIndexed { index, step ->
                RecipeStepEntity(
                    recipeId = recipeId,
                    position = index,
                    description = step.description.trim()
                )
            }
            .filter { it.description.isNotEmpty() }

        if (sanitizedSteps.isNotEmpty()) {
            recipeDao.insertSteps(sanitizedSteps)
        }
    }

    private fun RecipeWithDetails.toDomain(): Recipe =
        Recipe(
            id = recipe.id,
            title = recipe.title,
            ingredients = ingredients
                .sortedBy { it.position }
                .map {
                    RecipeIngredient(
                        rawText = it.rawText,
                        position = it.position
                    )
                },
            steps = steps
                .sortedBy { it.position }
                .map {
                    RecipeStep(
                        description = it.description,
                        position = it.position
                    )
                }
        ,
            updatedAt = recipe.updatedAt
        )

    private fun applyFilters(
        recipes: List<Recipe>,
        filters: RecipeFilters
    ): List<Recipe> {
        val query = filters.query
        val trimmedQuery = query.trim()
        val filteredByQuery = if (trimmedQuery.isEmpty()) {
            recipes
        } else {
            val normalizedQuery = trimmedQuery.lowercase(Locale.getDefault())
            recipes.filter { recipe ->
                recipe.title.lowercase(Locale.getDefault()).contains(normalizedQuery)
            }
        }

        // Future filters (cookbooks, etc.) can be chained here.
        return filteredByQuery
    }
}
