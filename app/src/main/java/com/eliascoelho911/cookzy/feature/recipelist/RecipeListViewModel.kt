package com.eliascoelho911.cookzy.feature.recipelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeListUiState())
    val state: StateFlow<RecipeListUiState> = _state

    init {
        observeRecipes()
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            recipeRepository.observeRecipes().collectLatest { recipes ->
                _state.update {
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

    companion object {
        fun provideFactory(
            repository: RecipeRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecipeListViewModel(
                    recipeRepository = repository
                )
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

object RecipeListArgs {
    const val ROUTE = "recipeList"
}
