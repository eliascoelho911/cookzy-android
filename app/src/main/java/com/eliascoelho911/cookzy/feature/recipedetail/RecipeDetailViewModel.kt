package com.eliascoelho911.cookzy.feature.recipedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Long = checkNotNull(savedStateHandle[RecipeEditorArgs.RECIPE_ID]) {
        "recipeId is required to show recipe details."
    }

    private val _state = MutableStateFlow(RecipeDetailUiState(isLoading = true))
    val state: StateFlow<RecipeDetailUiState> = _state

    init {
        loadRecipe()
    }

    fun refresh() {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val recipe = recipeRepository.getRecipe(recipeId)
            if (recipe == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Receita não encontrada."
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    recipeId = recipe.id,
                    title = recipe.title,
                    ingredients = recipe.ingredients
                        .sortedBy { ingredient -> ingredient.position }
                        .map { ingredient ->
                            IngredientDetailUi(
                                name = ingredient.name,
                                quantity = ingredient.quantity,
                                unit = ingredient.unit,
                                note = ingredient.note
                            )
                        },
                    steps = recipe.steps
                        .sortedBy { step -> step.position }
                        .mapIndexed { index, step ->
                            StepDetailUi(
                                position = index + 1,
                                description = step.description
                            )
                        },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: RecipeRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecipeDetailViewModel(
                    recipeRepository = repository,
                    savedStateHandle = this.createSavedStateHandle()
                )
            }
        }
    }
}

data class RecipeDetailUiState(
    val recipeId: Long? = null,
    val title: String = "",
    val ingredients: List<IngredientDetailUi> = emptyList(),
    val steps: List<StepDetailUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class IngredientDetailUi(
    val name: String,
    val quantity: String?,
    val unit: String?,
    val note: String?
)

data class StepDetailUi(
    val position: Int,
    val description: String
)

object RecipeDetailArgs {
    const val ROUTE = "recipeDetail"
    const val RECIPE_ID = "recipeId"
}
