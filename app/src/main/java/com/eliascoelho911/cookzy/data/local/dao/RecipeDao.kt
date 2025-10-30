package com.eliascoelho911.cookzy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eliascoelho911.cookzy.data.local.entity.IngredientEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeStepEntity
import com.eliascoelho911.cookzy.data.local.model.RecipeWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Insert
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Insert
    suspend fun insertSteps(steps: List<RecipeStepEntity>)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Long)

    @Query("DELETE FROM recipe_steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsByRecipeId(recipeId: Long)

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeWithDetails(recipeId: Long): RecipeWithDetails?

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY updatedAt DESC, title COLLATE NOCASE")
    fun observeRecipes(): Flow<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY updatedAt DESC, title COLLATE NOCASE LIMIT :limit")
    fun observeRecentRecipes(limit: Int): Flow<List<RecipeWithDetails>>
}
