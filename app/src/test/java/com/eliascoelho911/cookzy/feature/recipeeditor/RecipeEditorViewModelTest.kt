package com.eliascoelho911.cookzy.feature.recipeeditor

import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.testing.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    lateinit var repository: RecipeRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        coEvery { repository.createRecipe(any()) } returns 1L
    }

    @Test
    fun onSave_withEmptyTitle_setsError() = runTest {
        val viewModel = RecipeEditorViewModel(
            recipeIdArg = null,
            recipeRepository = repository
        )

        viewModel.onSave()

        assertEquals("Título é obrigatório", viewModel.state.value.titleError)
        coVerify(exactly = 0) { repository.createRecipe(any()) }
        coVerify(exactly = 0) { repository.updateRecipe(any(), any()) }
    }

    @Test
    fun onSave_withValidData_emitsNavigateEffect() = runTest {
        val viewModel = RecipeEditorViewModel(
            recipeIdArg = null,
            recipeRepository = repository
        )

        viewModel.onTitleChange("Bolo de Fubá")
        val ingredientId = viewModel.state.value.ingredientInputs.first().id
        viewModel.onIngredientChange(ingredientId) { it.copy(name = "Fubá") }
        val stepId = viewModel.state.value.stepInputs.first().id
        viewModel.onStepChange(stepId) { it.copy(description = "Misture tudo e asse.") }

        viewModel.onSave()
        advanceUntilIdle()

        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.NavigateToRecipeDetail)
        val navigateEffect = effect as RecipeEditorUiEffect.NavigateToRecipeDetail
        assertEquals(1L, navigateEffect.recipeId)
        coVerify(exactly = 1) { repository.createRecipe(any()) }
        coVerify(exactly = 0) { repository.updateRecipe(any(), any()) }
    }

    @Test
    fun init_withRecipeId_loadsExistingRecipeAndUpdates() = runTest {
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
        coEvery { repository.getRecipe(existingRecipe.id) } returns existingRecipe

        val viewModel = RecipeEditorViewModel(existingRecipe.id, repository)

        advanceUntilIdle()

        assertEquals("Pão Caseiro", viewModel.state.value.title)
        viewModel.onTitleChange("Pão Caseiro Integral")
        coEvery { repository.updateRecipe(existingRecipe.id, any()) } returns Unit

        viewModel.onSave()
        advanceUntilIdle()

        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.NavigateToRecipeDetail)
        val navigateEffect = effect as RecipeEditorUiEffect.NavigateToRecipeDetail
        assertEquals(existingRecipe.id, navigateEffect.recipeId)
        coVerify(exactly = 1) {
            repository.updateRecipe(existingRecipe.id, match { it.title == "Pão Caseiro Integral" })
        }
        coVerify(exactly = 0) { repository.createRecipe(any()) }
    }
}
