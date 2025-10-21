package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.lifecycle.SavedStateHandle
import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipeEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onSave_withEmptyTitle_setsError() = runTest {
        val repository = FakeRecipeRepository()
        val viewModel = RecipeEditorViewModel(repository, SavedStateHandle())

        viewModel.onSave()

        assertEquals("Título é obrigatório", viewModel.state.value.titleError)
        assertTrue(repository.createdDraft == null && repository.updatedDraft == null)
    }

    @Test
    fun onSave_withValidData_emitsNavigateEffect() = runTest {
        val repository = FakeRecipeRepository()
        val viewModel = RecipeEditorViewModel(repository, SavedStateHandle())

        viewModel.onTitleChange("Bolo de Fubá")
        val ingredientId = viewModel.state.value.ingredientInputs.first().id
        viewModel.onIngredientChange(ingredientId) { it.copy(name = "Fubá") }
        val stepId = viewModel.state.value.stepInputs.first().id
        viewModel.onStepChange(stepId) { it.copy(description = "Misture tudo e asse.") }

        viewModel.onSave()

        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.NavigateToRecipeDetail)
        val navigateEffect = effect as RecipeEditorUiEffect.NavigateToRecipeDetail
        assertEquals(1L, navigateEffect.recipeId)
        assertEquals("Bolo de Fubá", repository.createdDraft?.title)
        assertTrue(repository.updatedDraft == null)
    }

    @Test
    fun init_withRecipeId_loadsExistingRecipeAndUpdates() = runTest {
        val repository = FakeRecipeRepository()
        val existingRecipe = Recipe(
            id = 10L,
            title = "Pão Caseiro",
            ingredients = listOf(
                RecipeIngredient(
                    name = "Farinha",
                    quantity = "500",
                    unit = "g",
                    note = "De trigo",
                    position = 0
                )
            ),
            steps = listOf(
                RecipeStep(
                    description = "Misture tudo e deixe crescer.",
                    position = 0
                )
            )
        )
        repository.seed(existingRecipe)

        val viewModel = RecipeEditorViewModel(
            recipeRepository = repository,
            savedStateHandle = SavedStateHandle(mapOf("recipeId" to existingRecipe.id))
        )

        advanceUntilIdle()

        assertEquals("Pão Caseiro", viewModel.state.value.title)
        viewModel.onTitleChange("Pão Caseiro Integral")
        viewModel.onSave()
        advanceUntilIdle()

        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.NavigateToRecipeDetail)
        val navigateEffect = effect as RecipeEditorUiEffect.NavigateToRecipeDetail
        assertEquals(existingRecipe.id, navigateEffect.recipeId)
        assertEquals("Pão Caseiro Integral", repository.updatedDraft?.title)
    }
}

private class FakeRecipeRepository : RecipeRepository {
    private val recipes = mutableMapOf<Long, Recipe>()
    private val recipesFlow = MutableStateFlow<List<Recipe>>(emptyList())
    private var nextId = 1L

    var createdDraft: RecipeDraft? = null
        private set
    var updatedDraft: RecipeDraft? = null
        private set

    override suspend fun createRecipe(draft: RecipeDraft): Long {
        val id = nextId++
        createdDraft = draft
        recipes[id] = draft.toRecipe(id)
        emitChanges()
        return id
    }

    override suspend fun updateRecipe(id: Long, draft: RecipeDraft) {
        updatedDraft = draft
        recipes[id] = draft.toRecipe(id)
        emitChanges()
    }

    override suspend fun getRecipe(id: Long): Recipe? = recipes[id]

    override fun observeRecipes(): Flow<List<Recipe>> = recipesFlow

    fun seed(recipe: Recipe) {
        recipes[recipe.id] = recipe
        emitChanges()
    }

    private fun emitChanges() {
        recipesFlow.value = recipes.values.sortedBy { it.id }
    }

    private fun RecipeDraft.toRecipe(id: Long): Recipe =
        Recipe(
            id = id,
            title = title,
            ingredients = ingredients,
            steps = steps
        )
}
