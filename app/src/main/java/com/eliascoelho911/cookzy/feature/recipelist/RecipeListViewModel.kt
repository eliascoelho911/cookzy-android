package com.eliascoelho911.cookzy.feature.recipelist

import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository
) : BaseViewModel<RecipeListUiState, Unit>(RecipeListUiState()) {

    init {
        observeRecipes()
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            recipeRepository.observeRecipes().collectLatest { recipes ->
                updateState {
                    it.copy(
                        recipes = recipes.map { recipe ->
                            RecipeSummaryUi(
                                id = recipe.id,
                                title = recipe.title
                            )
                        },
                        isEmpty = recipes.isEmpty()
                    )
                }
            }
        }
    }
}

data class RecipeListUiState(
    val recipes: List<RecipeSummaryUi> = emptyList(),
    val isEmpty: Boolean = true
)

data class RecipeSummaryUi(
    val id: Long,
    val title: String
)
