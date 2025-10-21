package com.eliascoelho911.cookzy.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.eliascoelho911.cookzy.data.local.database.CookzyDatabase
import com.eliascoelho911.cookzy.data.local.entity.IngredientEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeStepEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class RecipeDaoTest {

    private lateinit var database: CookzyDatabase
    private lateinit var recipeDao: RecipeDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, CookzyDatabase::class.java)
            .addMigrations(CookzyDatabase.MIGRATION_0_1)
            .allowMainThreadQueries()
            .build()
        recipeDao = database.recipeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertRecipeWithDetails_returnsPersistedData() = runTest {
        val recipeId = recipeDao.insertRecipe(
            RecipeEntity(title = "Bolo de Cenoura")
        )
        recipeDao.insertIngredients(
            listOf(
                IngredientEntity(
                    recipeId = recipeId,
                    position = 0,
                    name = "Cenouras",
                    quantity = "3",
                    unit = "unidades",
                    note = null
                )
            )
        )
        recipeDao.insertSteps(
            listOf(
                RecipeStepEntity(
                    recipeId = recipeId,
                    position = 0,
                    description = "Bater todos os ingredientes no liquidificador."
                )
            )
        )

        val recipeWithDetails = recipeDao.getRecipeWithDetails(recipeId)

        assertNotNull(recipeWithDetails)
        requireNotNull(recipeWithDetails)

        assertEquals("Bolo de Cenoura", recipeWithDetails.recipe.title)
        assertEquals(1, recipeWithDetails.ingredients.size)
        assertEquals("Cenouras", recipeWithDetails.ingredients.first().name)
        assertEquals(1, recipeWithDetails.steps.size)
        assertEquals(0, recipeWithDetails.steps.first().position)
    }

    @Test
    fun updateRecipe_overwritesPreviousDetails() = runTest {
        val recipeId = recipeDao.insertRecipe(
            RecipeEntity(title = "Bolo Simples")
        )
        recipeDao.insertIngredients(
            listOf(
                IngredientEntity(
                    recipeId = recipeId,
                    position = 0,
                    name = "Farinha",
                    quantity = "2",
                    unit = "xícaras",
                    note = null
                )
            )
        )
        recipeDao.insertSteps(
            listOf(
                RecipeStepEntity(
                    recipeId = recipeId,
                    position = 0,
                    description = "Misture tudo."
                )
            )
        )

        recipeDao.updateRecipe(
            RecipeEntity(
                id = recipeId,
                title = "Bolo de Chocolate"
            )
        )
        recipeDao.deleteIngredientsByRecipeId(recipeId)
        recipeDao.deleteStepsByRecipeId(recipeId)
        recipeDao.insertIngredients(
            listOf(
                IngredientEntity(
                    recipeId = recipeId,
                    position = 0,
                    name = "Chocolate em pó",
                    quantity = "4",
                    unit = "colheres",
                    note = "Use chocolate 70%"
                )
            )
        )
        recipeDao.insertSteps(
            listOf(
                RecipeStepEntity(
                    recipeId = recipeId,
                    position = 0,
                    description = "Misture e leve ao forno por 40 minutos."
                )
            )
        )

        val updatedRecipe = recipeDao.getRecipeWithDetails(recipeId)

        assertNotNull(updatedRecipe)
        requireNotNull(updatedRecipe)

        assertEquals("Bolo de Chocolate", updatedRecipe.recipe.title)
        assertEquals(1, updatedRecipe.ingredients.size)
        assertEquals("Chocolate em pó", updatedRecipe.ingredients.first().name)
        assertEquals("Use chocolate 70%", updatedRecipe.ingredients.first().note)
        assertEquals("Misture e leve ao forno por 40 minutos.", updatedRecipe.steps.first().description)
    }
}
