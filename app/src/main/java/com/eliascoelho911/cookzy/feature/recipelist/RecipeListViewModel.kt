package com.eliascoelho911.cookzy.feature.recipelist

import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.model.RecipeListLayout
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel<RecipeListUiState, Unit>(RecipeListUiState()) {

    private val query = MutableStateFlow("")
    private val loadingOnce = MutableStateFlow(false)

    init {
        observeAll()
    }

    @OptIn(FlowPreview::class)
    private fun observeAll() {
        val allRecipesUi = recipeRepository.observeRecipes()
            .map { recipes ->
                recipes.map { recipe ->
                    RecipeSummaryUi(id = recipe.id, title = recipe.title)
                }
            }
            .onStart { loadingOnce.value = false }
            .catch {
                updateState { it.copy(error = true, isLoading = false) }
            }

        val recents = allRecipesUi.map { list ->
            list.sortedByDescending { it.id }.take(10)
        }

        val filtered = combine(allRecipesUi, query.debounce(400)) { list, q ->
            if (q.isBlank()) list else list.filter { it.title.contains(q, ignoreCase = true) }
        }

        viewModelScope.launch {
            combine(
                filtered,
                recents,
                userPreferencesRepository.observeRecipeListLayout()
            ) { filteredList, recentList, layout ->
                Triple(filteredList, recentList, layout)
            }.collect { (filteredList, recentList, layout) ->
                updateState { state ->
                    state.copy(
                        recipes = filteredList,
                        recents = recentList,
                        totalCount = filteredList.size,
                        isEmpty = filteredList.isEmpty(),
                        isLoading = false,
                        layout = layout
                    )
                }
            }
        }
    }

    fun onToggleSearch() {
        val active = !currentState.searchActive
        updateState { it.copy(searchActive = active) }
        if (!active) onClearSearch()
    }

    fun onSearchQueryChanged(value: String) {
        updateState { it.copy(searchQuery = value) }
        query.value = value
    }

    fun onClearSearch() {
        updateState { it.copy(searchQuery = "") }
        query.value = ""
    }

    fun onToggleLayout() {
        val next = if (currentState.layout == RecipeListLayout.LIST) RecipeListLayout.GRID else RecipeListLayout.LIST
        viewModelScope.launch { userPreferencesRepository.setRecipeListLayout(next) }
    }

    fun onRetry() {
        updateState { it.copy(error = false, isLoading = true) }
        observeAll()
    }
}

data class RecipeListUiState(
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val searchActive: Boolean = false,
    val searchQuery: String = "",
    val layout: RecipeListLayout = RecipeListLayout.LIST,
    val recents: List<RecipeSummaryUi> = emptyList(),
    val recipes: List<RecipeSummaryUi> = emptyList(),
    val totalCount: Int = 0,
    val isEmpty: Boolean = true
)

data class RecipeSummaryUi(
    val id: Long,
    val title: String
)
