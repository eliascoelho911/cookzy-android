package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.eliascoelho911.cookzy.domain.model.RecipeDraft
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import java.util.UUID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RECIPE_ID_ARG = "recipeId"

class RecipeEditorViewModel(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeIdArg: Long? = savedStateHandle[RECIPE_ID_ARG]

    private val _state = MutableStateFlow(RecipeEditorUiState(isLoading = recipeIdArg != null))
    val state: StateFlow<RecipeEditorUiState> = _state

    private val _effects = Channel<RecipeEditorUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var currentRecipeId: Long? = null

    init {
        if (recipeIdArg != null) {
            loadRecipe(recipeIdArg)
        }
    }

    fun onTitleChange(newTitle: String) {
        _state.update {
            it.copy(
                title = newTitle,
                titleError = null
            )
        }
    }

    fun onIngredientChange(id: String, transform: (IngredientInput) -> IngredientInput) {
        _state.update { current ->
            current.copy(
                ingredientInputs = current.ingredientInputs.map { input ->
                    if (input.id == id) transform(input) else input
                }
            )
        }
    }

    fun addIngredient() {
        _state.update {
            it.copy(
                ingredientInputs = it.ingredientInputs + IngredientInput()
            )
        }
    }

    fun removeIngredient(id: String) {
        _state.update { current ->
            val updated = current.ingredientInputs.filterNot { it.id == id }
            current.copy(
                ingredientInputs = updated.ifEmpty { listOf(IngredientInput()) }
            )
        }
    }

    fun onStepChange(id: String, transform: (StepInput) -> StepInput) {
        _state.update { current ->
            current.copy(
                stepInputs = current.stepInputs.map { input ->
                    if (input.id == id) transform(input) else input
                }
            )
        }
    }

    fun addStep() {
        _state.update {
            it.copy(
                stepInputs = it.stepInputs + StepInput()
            )
        }
    }

    fun removeStep(id: String) {
        _state.update { current ->
            val updated = current.stepInputs.filterNot { it.id == id }
            current.copy(
                stepInputs = updated.ifEmpty { listOf(StepInput()) }
            )
        }
    }

    fun onCancel() {
        viewModelScope.launch {
            _effects.send(RecipeEditorUiEffect.CloseWithoutSaving)
        }
    }

    fun onSave() {
        val currentState = _state.value
        val sanitizedTitle = currentState.title.trim()

        if (sanitizedTitle.isEmpty()) {
            _state.update { it.copy(titleError = "Título é obrigatório") }
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
            try {
                _state.update { it.copy(isSaving = true) }
                val recipeId = currentRecipeId?.let { existingId ->
                    recipeRepository.updateRecipe(existingId, draft)
                    existingId
                } ?: recipeRepository.createRecipe(draft).also { createdId ->
                    currentRecipeId = createdId
                }

                _effects.send(RecipeEditorUiEffect.NavigateToRecipeDetail(recipeId))
            } catch (error: Throwable) {
                _effects.send(
                    RecipeEditorUiEffect.ShowError(
                        message = error.message ?: "Não foi possível salvar a receita."
                    )
                )
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val recipe = recipeRepository.getRecipe(recipeId)
                if (recipe == null) {
                    _effects.send(
                        RecipeEditorUiEffect.ShowError("Receita não encontrada.")
                    )
                    _effects.send(RecipeEditorUiEffect.CloseWithoutSaving)
                    return@launch
                }
                currentRecipeId = recipe.id
                _state.update {
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
            } catch (error: Throwable) {
                _effects.send(
                    RecipeEditorUiEffect.ShowError(
                        message = error.message ?: "Erro ao carregar a receita."
                    )
                )
                _effects.send(RecipeEditorUiEffect.CloseWithoutSaving)
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: RecipeRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecipeEditorViewModel(
                    recipeRepository = repository,
                    savedStateHandle = this.createSavedStateHandle()
                )
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
