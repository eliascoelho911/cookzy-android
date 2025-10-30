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
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineRecipeRepository(
    private val recipeDao: RecipeDao
) : RecipeRepository {

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

    override fun observeRecipes(): Flow<List<Recipe>> =
        recipeDao.observeRecipes().map { recipes ->
            recipes.map { it.toDomain() }
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
}
