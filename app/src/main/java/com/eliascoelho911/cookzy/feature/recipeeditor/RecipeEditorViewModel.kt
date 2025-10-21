package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.launch
import java.util.UUID

private const val RECIPE_ID_ARG = "recipeId"

class RecipeEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : BaseViewModel<RecipeEditorUiState, RecipeEditorUiEffect>(
    initialState = RecipeEditorUiState(isLoading = savedStateHandle[RECIPE_ID_ARG] as? Boolean? != null)
) {

    private val recipeIdArg: Long? = savedStateHandle[RECIPE_ID_ARG]

    private var currentRecipeId: Long? = null

    init {
        if (recipeIdArg != null) {
            loadRecipe(recipeIdArg)
        }
    }

    fun onTitleChange(newTitle: String) {
        updateState {
            it.copy(
                title = newTitle,
                titleError = null
            )
        }
    }

    fun onIngredientChange(id: String, transform: (IngredientInput) -> IngredientInput) {
        updateState { current ->
            current.copy(
                ingredientInputs = current.ingredientInputs.map { input ->
                    if (input.id == id) transform(input) else input
                }
            )
        }
    }

    fun addIngredient() {
        updateState {
            it.copy(
                ingredientInputs = it.ingredientInputs + IngredientInput()
            )
        }
    }

    fun removeIngredient(id: String) {
        updateState { current ->
            val updated = current.ingredientInputs.filterNot { it.id == id }
            current.copy(
                ingredientInputs = updated.ifEmpty { listOf(IngredientInput()) }
            )
        }
    }

    fun onStepChange(id: String, transform: (StepInput) -> StepInput) {
        updateState { current ->
            current.copy(
                stepInputs = current.stepInputs.map { input ->
                    if (input.id == id) transform(input) else input
                }
            )
        }
    }

    fun addStep() {
        updateState {
            it.copy(
                stepInputs = it.stepInputs + StepInput()
            )
        }
    }

    fun removeStep(id: String) {
        updateState { current ->
            val updated = current.stepInputs.filterNot { it.id == id }
            current.copy(
                stepInputs = updated.ifEmpty { listOf(StepInput()) }
            )
        }
    }

    fun onCancel() {
        sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
    }

    fun onSave() {
        val sanitizedTitle = currentState.title.trim()

        if (sanitizedTitle.isEmpty()) {
            updateState { it.copy(titleError = "Título é obrigatório") }
            return
        }

        val draft = RecipeDraft(
            title = sanitizedTitle,
            ingredients = currentState.ingredientInputs
                .mapIndexed { index, input ->
                    RecipeIngredient(
                        name = input.name.trim(),
                        quantity = input.quantity.takeIf { it.isNotBlank() },
                        unit = input.unit.takeIf { it.isNotBlank() },
                        note = input.note.takeIf { it.isNotBlank() },
                        position = index
                    )
                }
                .filter { it.name.isNotEmpty() },
            steps = currentState.stepInputs
                .mapIndexed { index, input ->
                    RecipeStep(
                        description = input.description.trim(),
                        position = index
                    )
                }
                .filter { it.description.isNotEmpty() }
        )

        viewModelScope.launch {
            updateState { it.copy(isSaving = true) }
            try {
                val recipeId = currentRecipeId?.let { existingId ->
                    recipeRepository.updateRecipe(existingId, draft)
                    existingId
                } ?: recipeRepository.createRecipe(draft).also { createdId ->
                    currentRecipeId = createdId
                }

                sendEffect(RecipeEditorUiEffect.NavigateToRecipeDetail(recipeId))
            } catch (error: Throwable) {
                sendEffect(
                    RecipeEditorUiEffect.ShowError(
                        message = error.message ?: "Não foi possível salvar a receita."
                    )
                )
            } finally {
                updateState { it.copy(isSaving = false) }
            }
        }
    }

    private fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            runCatching { recipeRepository.getRecipe(recipeId) }
                .onSuccess { recipe ->
                    if (recipe == null) {
                        sendEffect(RecipeEditorUiEffect.ShowError("Receita não encontrada."))
                        sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
                        return@launch
                    }

                    currentRecipeId = recipe.id
                    updateState {
                        it.copy(
                            title = recipe.title,
                            titleError = null,
                            isLoading = false,
                            isEditing = true,
                            ingredientInputs = recipe.ingredients
                                .sortedBy { ingredient -> ingredient.position }
                                .map { ingredient ->
                                    IngredientInput(
                                        id = UUID.randomUUID().toString(),
                                        name = ingredient.name,
                                        quantity = ingredient.quantity.orEmpty(),
                                        unit = ingredient.unit.orEmpty(),
                                        note = ingredient.note.orEmpty()
                                    )
                                }
                                .ifEmpty { listOf(IngredientInput()) },
                            stepInputs = recipe.steps
                                .sortedBy { step -> step.position }
                                .map { step ->
                                    StepInput(
                                        id = UUID.randomUUID().toString(),
                                        description = step.description
                                    )
                                }
                                .ifEmpty { listOf(StepInput()) }
                        )
                    }
                }
                .onFailure { error ->
                    sendEffect(
                        RecipeEditorUiEffect.ShowError(
                            message = error.message ?: "Erro ao carregar a receita."
                        )
                    )
                    sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
                }
        }
    }
}

data class RecipeEditorUiState(
    val title: String = "",
    val titleError: String? = null,
    val ingredientInputs: List<IngredientInput> = listOf(IngredientInput()),
    val stepInputs: List<StepInput> = listOf(StepInput()),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false
)

data class IngredientInput(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val note: String = ""
)

data class StepInput(
    val id: String = UUID.randomUUID().toString(),
    val description: String = ""
)

sealed interface RecipeEditorUiEffect {
    data class NavigateToRecipeDetail(val recipeId: Long) : RecipeEditorUiEffect
    data object CloseWithoutSaving : RecipeEditorUiEffect
    data class ShowError(val message: String) : RecipeEditorUiEffect
}

object RecipeEditorArgs {
    const val ROUTE = "recipeEditor"
    const val RECIPE_ID = RECIPE_ID_ARG
}
