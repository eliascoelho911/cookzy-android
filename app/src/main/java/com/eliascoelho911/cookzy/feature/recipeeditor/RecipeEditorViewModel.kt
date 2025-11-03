package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.launch
import java.util.UUID

class RecipeEditorViewModel(
    private val recipeIdArg: Long?,
    private val recipeRepository: RecipeRepository,
) : BaseViewModel<RecipeEditorUiState, RecipeEditorUiEffect>(
    initialState = RecipeEditorUiState(
        isLoading = recipeIdArg != null,
    ),
) {

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
                titleError = null,
                focusTitle = false,
            )
        }
    }

    fun onIngredientChange(
        id: String,
        transform: (IngredientInput) -> IngredientInput,
    ) {
        updateState { current ->
            var transformed: IngredientInput? = null
            val ingredients = current.ingredientInputs.map { input ->
                if (input.id == id) {
                    transform(input).also { transformed = it }
                } else {
                    input
                }
            }
            val trimmed = transformed?.rawText?.trim().orEmpty()
            val updatedErrors = current.ingredientErrors.toMutableSet().apply {
                if (trimmed.isNotEmpty()) {
                    remove(id)
                } else if (current.validationTriggered) {
                    add(id)
                }
            }

            current.copy(
                ingredientInputs = ingredients,
                ingredientErrors = updatedErrors,
            )
        }
    }

    fun addIngredient() {
        updateState { current ->
            val canAdd = current.ingredientInputs.lastOrNull()
                ?.rawText
                ?.isNotBlank() ?: true
            if (!canAdd) {
                return@updateState current
            }

            val newIngredient = IngredientInput()
            val updatedErrors = current.ingredientErrors.toMutableSet().apply {
                if (current.validationTriggered) {
                    add(newIngredient.id)
                }
            }

            current.copy(
                ingredientInputs = current.ingredientInputs + newIngredient,
                ingredientErrors = updatedErrors,
                focusedIngredientId = newIngredient.id,
            )
        }
    }

    fun removeIngredient(id: String) {
        val current = currentState
        if (current.ingredientInputs.size <= 1) {
            sendEffect(RecipeEditorUiEffect.ShowError(R.string.editor_remove_last_ingredient_warning))
            return
        }

        updateState { state ->
            val index = state.ingredientInputs.indexOfFirst { it.id == id }
            if (index == -1) {
                return@updateState state
            }

            val updatedList = state.ingredientInputs.toMutableList().apply {
                removeAt(index)
            }
            val focusIndex = (index - 1).coerceAtLeast(0).coerceAtMost(updatedList.lastIndex)
            val newFocusId = updatedList.getOrNull(focusIndex)?.id

            state.copy(
                ingredientInputs = updatedList,
                ingredientErrors = state.ingredientErrors - id,
                focusedIngredientId = newFocusId,
            )
        }
    }

    fun moveIngredient(from: Int, to: Int) {
        if (from == to) return

        updateState { current ->
            if (from !in current.ingredientInputs.indices || to !in current.ingredientInputs.indices) {
                return@updateState current
            }

            val updated = current.ingredientInputs.toMutableList().apply {
                val item = removeAt(from)
                add(to, item)
            }

            current.copy(ingredientInputs = updated)
        }
    }

    fun onStepChange(
        id: String,
        transform: (StepInput) -> StepInput,
    ) {
        updateState { current ->
            var transformed: StepInput? = null
            val steps = current.stepInputs.map { input ->
                if (input.id == id) {
                    transform(input).also { transformed = it }
                } else {
                    input
                }
            }
            val trimmed = transformed?.description?.trim().orEmpty()
            val updatedErrors = current.stepErrors.toMutableSet().apply {
                if (trimmed.isNotEmpty()) {
                    remove(id)
                } else if (current.validationTriggered) {
                    add(id)
                }
            }

            current.copy(
                stepInputs = steps,
                stepErrors = updatedErrors,
            )
        }
    }

    fun addStep() {
        updateState { current ->
            val canAdd = current.stepInputs.lastOrNull()
                ?.description
                ?.isNotBlank() ?: true
            if (!canAdd) {
                return@updateState current
            }

            val newStep = StepInput()
            val updatedErrors = current.stepErrors.toMutableSet().apply {
                if (current.validationTriggered) {
                    add(newStep.id)
                }
            }

            current.copy(
                stepInputs = current.stepInputs + newStep,
                stepErrors = updatedErrors,
                focusedStepId = newStep.id,
            )
        }
    }

    fun removeStep(id: String) {
        val current = currentState
        if (current.stepInputs.size <= 1) {
            sendEffect(RecipeEditorUiEffect.ShowError(R.string.editor_remove_last_step_warning))
            return
        }

        updateState { state ->
            val index = state.stepInputs.indexOfFirst { it.id == id }
            if (index == -1) {
                return@updateState state
            }

            val updatedList = state.stepInputs.toMutableList().apply {
                removeAt(index)
            }
            val focusIndex = (index - 1).coerceAtLeast(0).coerceAtMost(updatedList.lastIndex)
            val newFocusId = updatedList.getOrNull(focusIndex)?.id

            state.copy(
                stepInputs = updatedList,
                stepErrors = state.stepErrors - id,
                focusedStepId = newFocusId,
            )
        }
    }

    fun moveStep(from: Int, to: Int) {
        if (from == to) return

        updateState { current ->
            if (from !in current.stepInputs.indices || to !in current.stepInputs.indices) {
                return@updateState current
            }

            val updated = current.stepInputs.toMutableList().apply {
                val item = removeAt(from)
                add(to, item)
            }

            current.copy(stepInputs = updated)
        }
    }

    fun onCancel() {
        sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
    }

    fun onSave() {
        val draft = validateInputs() ?: return

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
            } catch (_: Throwable) {
                sendEffect(RecipeEditorUiEffect.ShowError(R.string.error_recipe_save))
            } finally {
                updateState { it.copy(isSaving = false) }
            }
        }
    }

    fun consumeIngredientFocus() {
        updateState { it.copy(focusedIngredientId = null) }
    }

    fun consumeStepFocus() {
        updateState { it.copy(focusedStepId = null) }
    }

    fun consumeTitleFocus() {
        updateState { it.copy(focusTitle = false) }
    }

    private fun validateInputs(): RecipeDraft? {
        val current = currentState
        val sanitizedTitle = current.title.trim()
        val ingredientErrorIds = current.ingredientInputs
            .filter { it.rawText.trim().isEmpty() }
            .map { it.id }
        val stepErrorIds = current.stepInputs
            .filter { it.description.trim().isEmpty() }
            .map { it.id }

        val hasValidTitle = sanitizedTitle.isNotEmpty()
        val hasIngredients = current.ingredientInputs.isNotEmpty()
        val hasSteps = current.stepInputs.isNotEmpty()
        val canSave = hasValidTitle && hasIngredients && hasSteps &&
            ingredientErrorIds.isEmpty() && stepErrorIds.isEmpty()

        updateState {
            it.copy(
                titleError = if (hasValidTitle) null else R.string.editor_title_error_required,
                ingredientErrors = ingredientErrorIds.toSet(),
                stepErrors = stepErrorIds.toSet(),
                validationTriggered = true,
                focusTitle = !hasValidTitle,
                focusedIngredientId = ingredientErrorIds.firstOrNull(),
                focusedStepId = if (ingredientErrorIds.isEmpty()) stepErrorIds.firstOrNull() else null,
            )
        }

        if (!canSave) {
            return null
        }

        val ingredients = current.ingredientInputs.mapIndexed { index, input ->
            RecipeIngredient(
                rawText = input.rawText.trim(),
                position = index,
            )
        }
        val steps = current.stepInputs.mapIndexed { index, input ->
            RecipeStep(
                description = input.description.trim(),
                position = index,
            )
        }

        return RecipeDraft(
            title = sanitizedTitle,
            ingredients = ingredients,
            steps = steps,
        )
    }

    private fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            runCatching { recipeRepository.getRecipe(recipeId) }
                .onSuccess { recipe ->
                    if (recipe == null) {
                        sendEffect(RecipeEditorUiEffect.ShowError(R.string.error_recipe_not_found))
                        sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
                        return@launch
                    }

                    currentRecipeId = recipe.id
                    updateState {
                        it.copy(
                            title = recipe.title,
                            titleError = null,
                            ingredientInputs = recipe.ingredients
                                .sortedBy { ingredient -> ingredient.position }
                                .map { ingredient ->
                                    IngredientInput(
                                        id = UUID.randomUUID().toString(),
                                        rawText = ingredient.rawText,
                                    )
                                }
                                .ifEmpty { listOf(IngredientInput()) },
                            ingredientErrors = emptySet(),
                            stepInputs = recipe.steps
                                .sortedBy { step -> step.position }
                                .map { step ->
                                    StepInput(
                                        id = UUID.randomUUID().toString(),
                                        description = step.description,
                                    )
                                }
                                .ifEmpty { listOf(StepInput()) },
                            stepErrors = emptySet(),
                            isLoading = false,
                            isEditing = true,
                            validationTriggered = false,
                            focusedIngredientId = null,
                            focusedStepId = null,
                            focusTitle = false,
                        )
                    }
                }
                .onFailure {
                    sendEffect(RecipeEditorUiEffect.ShowError(R.string.error_recipe_load))
                    sendEffect(RecipeEditorUiEffect.CloseWithoutSaving)
                }
        }
    }
}

data class RecipeEditorUiState(
    val title: String = "",
    @StringRes val titleError: Int? = null,
    val ingredientInputs: List<IngredientInput> = listOf(IngredientInput()),
    val ingredientErrors: Set<String> = emptySet(),
    val stepInputs: List<StepInput> = listOf(StepInput()),
    val stepErrors: Set<String> = emptySet(),
    val focusedIngredientId: String? = null,
    val focusedStepId: String? = null,
    val focusTitle: Boolean = false,
    val validationTriggered: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
) {
    val canSave: Boolean
        get() = !isSaving && !isLoading
}

data class IngredientInput(
    val id: String = UUID.randomUUID().toString(),
    val rawText: String = "",
)

data class StepInput(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
)

sealed interface RecipeEditorUiEffect {
    data class NavigateToRecipeDetail(val recipeId: Long) : RecipeEditorUiEffect
    data object CloseWithoutSaving : RecipeEditorUiEffect
    data class ShowError(@StringRes val messageRes: Int) : RecipeEditorUiEffect
}
