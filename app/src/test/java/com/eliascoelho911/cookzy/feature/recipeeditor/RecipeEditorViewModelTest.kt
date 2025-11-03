package com.eliascoelho911.cookzy.feature.recipeeditor

import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.testing.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

        val ingredientId = viewModel.state.value.ingredientInputs.first().id
        val stepId = viewModel.state.value.stepInputs.first().id

        viewModel.onSave()

        assertEquals(R.string.editor_title_error_required, viewModel.state.value.titleError)
        assertEquals(setOf(ingredientId), viewModel.state.value.ingredientErrors)
        assertEquals(setOf(stepId), viewModel.state.value.stepErrors)
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
        viewModel.onIngredientChange(ingredientId) { it.copy(rawText = "Fubá") }
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
                    rawText = "500 g de farinha de trigo",
                    position = 0
                )
            ),
            steps = listOf(
                RecipeStep(
                    description = "Misture tudo e deixe crescer.",
                    position = 0
                )
            ),
            updatedAt = 0L
        )
        coEvery { repository.getRecipe(existingRecipe.id) } returns existingRecipe

        val viewModel = RecipeEditorViewModel(existingRecipe.id, repository)

        advanceUntilIdle()

        assertEquals("Pão Caseiro", viewModel.state.value.title)
        assertEquals("500 g de farinha de trigo", viewModel.state.value.ingredientInputs.first().rawText)
        assertEquals("Misture tudo e deixe crescer.", viewModel.state.value.stepInputs.first().description)
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

    @Test
    fun onTitleChange_updatesTitleAndClearsError() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)

        viewModel.onSave()
        viewModel.onTitleChange("Feijoada")

        val state = viewModel.state.value

        assertEquals("Feijoada", state.title)
        assertEquals(null, state.titleError)
        assertFalse(state.focusTitle)
    }

    @Test
    fun onIngredientChange_withNonBlankInputClearsError() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val ingredientId = viewModel.state.value.ingredientInputs.first().id

        viewModel.onSave()
        viewModel.onIngredientChange(ingredientId) { it.copy(rawText = "  Farinha  ") }

        val state = viewModel.state.value

        assertEquals("  Farinha  ", state.ingredientInputs.first().rawText)
        assertFalse(state.ingredientErrors.contains(ingredientId))
    }

    @Test
    fun addIngredient_whenLastBlank_doesNotAddNewItem() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)

        viewModel.addIngredient()

        assertEquals(1, viewModel.state.value.ingredientInputs.size)
    }

    @Test
    fun addIngredient_whenLastFilled_addsNewIngredientAndFocus() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val firstIngredientId = viewModel.state.value.ingredientInputs.first().id

        viewModel.onIngredientChange(firstIngredientId) { it.copy(rawText = "Ovo") }
        viewModel.addIngredient()

        val state = viewModel.state.value

        assertEquals(2, state.ingredientInputs.size)
        val newIngredient = state.ingredientInputs.last()
        assertEquals(newIngredient.id, state.focusedIngredientId)
        assertEquals("", newIngredient.rawText)
        assertTrue(state.ingredientErrors.isEmpty())
    }

    @Test
    fun addIngredient_whenValidationAlreadyTriggered_marksNewIngredientAsError() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val initialId = viewModel.state.value.ingredientInputs.first().id

        viewModel.onSave()
        viewModel.onTitleChange("Torta")
        viewModel.onIngredientChange(initialId) { it.copy(rawText = "Massa") }
        viewModel.addIngredient()

        val state = viewModel.state.value
        val newIngredientId = state.ingredientInputs.last().id

        assertTrue(state.validationTriggered)
        assertTrue(state.ingredientErrors.contains(newIngredientId))
    }

    @Test
    fun removeIngredient_whenOnlyOne_showsWarningEffect() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val onlyIngredientId = viewModel.state.value.ingredientInputs.first().id

        viewModel.removeIngredient(onlyIngredientId)
        advanceUntilIdle()
        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.ShowError)
        effect as RecipeEditorUiEffect.ShowError
        assertEquals(R.string.editor_remove_last_ingredient_warning, effect.messageRes)
        assertEquals(1, viewModel.state.value.ingredientInputs.size)
    }

    @Test
    fun removeIngredient_whenMultiple_removesAndFocusesPrevious() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val firstId = viewModel.state.value.ingredientInputs.first().id
        viewModel.onIngredientChange(firstId) { it.copy(rawText = "Farinha") }
        viewModel.addIngredient()
        val secondId = viewModel.state.value.ingredientInputs.last().id
        viewModel.onIngredientChange(secondId) { it.copy(rawText = "Ovo") }

        viewModel.removeIngredient(secondId)

        val state = viewModel.state.value

        assertEquals(listOf(firstId), state.ingredientInputs.map { it.id })
        assertEquals(firstId, state.focusedIngredientId)
        assertFalse(state.ingredientErrors.contains(secondId))
    }

    @Test
    fun moveIngredient_reordersList() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val firstId = viewModel.state.value.ingredientInputs.first().id
        viewModel.onIngredientChange(firstId) { it.copy(rawText = "Farinha") }
        viewModel.addIngredient()
        val secondId = viewModel.state.value.ingredientInputs.last().id
        viewModel.onIngredientChange(secondId) { it.copy(rawText = "Ovo") }

        viewModel.moveIngredient(0, 1)

        val state = viewModel.state.value

        assertEquals(listOf(secondId, firstId), state.ingredientInputs.map { it.id })
    }

    @Test
    fun addStep_whenLastFilled_addsNewStepAndFocus() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val firstStepId = viewModel.state.value.stepInputs.first().id

        viewModel.onStepChange(firstStepId) { it.copy(description = "Misture") }
        viewModel.addStep()

        val state = viewModel.state.value

        assertEquals(2, state.stepInputs.size)
        val newStep = state.stepInputs.last()
        assertEquals(newStep.id, state.focusedStepId)
        assertEquals("", newStep.description)
    }

    @Test
    fun addStep_whenLastBlank_doesNotAddNewItem() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)

        viewModel.addStep()

        assertEquals(1, viewModel.state.value.stepInputs.size)
    }

    @Test
    fun removeStep_whenOnlyOne_showsWarningEffect() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val onlyStepId = viewModel.state.value.stepInputs.first().id

        viewModel.removeStep(onlyStepId)
        advanceUntilIdle()
        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.ShowError)
        effect as RecipeEditorUiEffect.ShowError
        assertEquals(R.string.editor_remove_last_step_warning, effect.messageRes)
        assertEquals(1, viewModel.state.value.stepInputs.size)
    }

    @Test
    fun moveStep_reordersList() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val firstId = viewModel.state.value.stepInputs.first().id
        viewModel.onStepChange(firstId) { it.copy(description = "Misture") }
        viewModel.addStep()
        val secondId = viewModel.state.value.stepInputs.last().id
        viewModel.onStepChange(secondId) { it.copy(description = "Asse") }

        viewModel.moveStep(0, 1)

        val state = viewModel.state.value

        assertEquals(listOf(secondId, firstId), state.stepInputs.map { it.id })
    }

    @Test
    fun onSave_whenRepositoryThrows_showsErrorAndResetsSaving() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val ingredientId = viewModel.state.value.ingredientInputs.first().id
        val stepId = viewModel.state.value.stepInputs.first().id

        viewModel.onTitleChange("Bolo")
        viewModel.onIngredientChange(ingredientId) { it.copy(rawText = "Farinha") }
        viewModel.onStepChange(stepId) { it.copy(description = "Misture") }
        coEvery { repository.createRecipe(any()) } throws IllegalStateException("fail")

        viewModel.onSave()
        advanceUntilIdle()
        val effect = viewModel.effects.first()

        assertTrue(effect is RecipeEditorUiEffect.ShowError)
        effect as RecipeEditorUiEffect.ShowError
        assertEquals(R.string.error_recipe_save, effect.messageRes)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun loadRecipe_whenRecipeNotFound_showsErrorAndCloses() = runTest {
        coEvery { repository.getRecipe(5L) } returns null

        val viewModel = RecipeEditorViewModel(5L, repository)

        advanceUntilIdle()
        val firstEffect = viewModel.effects.first()
        val secondEffect = viewModel.effects.first()

        assertTrue(firstEffect is RecipeEditorUiEffect.ShowError)
        firstEffect as RecipeEditorUiEffect.ShowError
        assertEquals(R.string.error_recipe_not_found, firstEffect.messageRes)
        assertTrue(secondEffect is RecipeEditorUiEffect.CloseWithoutSaving)
    }

    @Test
    fun consumeFocusFunctions_clearPendingFocusIds() = runTest {
        val viewModel = RecipeEditorViewModel(null, repository)
        val ingredientId = viewModel.state.value.ingredientInputs.first().id
        val stepId = viewModel.state.value.stepInputs.first().id

        viewModel.onSave()
        runCurrent()
        viewModel.consumeTitleFocus()
        viewModel.consumeIngredientFocus()
        viewModel.consumeStepFocus()

        val state = viewModel.state.value

        assertFalse(state.focusTitle)
        assertEquals(null, state.focusedIngredientId)
        assertEquals(null, state.focusedStepId)
    }
}
