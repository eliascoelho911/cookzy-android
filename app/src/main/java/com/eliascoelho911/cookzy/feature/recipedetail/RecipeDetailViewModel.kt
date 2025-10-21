package com.eliascoelho911.cookzy.feature.recipedetail

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.domain.util.deriveQuantity
import java.math.BigDecimal
import kotlin.ranges.IntRange
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeId: Long,
    private val recipeRepository: RecipeRepository
) : BaseViewModel<RecipeDetailUiState, Unit>(
    initialState = RecipeDetailUiState(isLoading = true)
) {

    init {
        loadRecipe()
    }

    fun refresh() {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessageRes = null) }
            val recipe = recipeRepository.getRecipe(recipeId)
            if (recipe == null) {
                updateState {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_recipe_not_found
                    )
                }
                return@launch
            }

            updateState {
                it.copy(
                    recipeId = recipe.id,
                    title = recipe.title,
                    ingredients = recipe.ingredients
                        .sortedBy { ingredient -> ingredient.position }
                        .map { ingredient ->
                            val derivation = deriveQuantity(ingredient.rawText)
                            IngredientDetailUi(
                                rawText = ingredient.rawText,
                                derivedQuantity = derivation?.value,
                                highlightRange = derivation?.range
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
                    errorMessageRes = null
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
    @StringRes val errorMessageRes: Int? = null
)

data class IngredientDetailUi(
    val rawText: String,
    val derivedQuantity: BigDecimal?,
    val highlightRange: IntRange?
)

data class StepDetailUi(
    val position: Int,
    val description: String
)
