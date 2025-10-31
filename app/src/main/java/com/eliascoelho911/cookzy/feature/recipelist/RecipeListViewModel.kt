package com.eliascoelho911.cookzy.feature.recipelist

import androidx.lifecycle.viewModelScope
import com.eliascoelho911.cookzy.core.BaseViewModel
import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutMode
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutPreferences
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.FlowPreview
import kotlin.time.Duration
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 400L

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val layoutPreferences: HomeLayoutPreferences
) : BaseViewModel<HomeRecipeListUiState, RecipeListUiEffect>(HomeRecipeListUiState()) {

    private val searchQuery = MutableStateFlow("")
    private var recipesJob: Job? = null
    private var recentRecipesJob: Job? = null

    init {
        observeLayoutMode()
        observeRecipes()
        observeRecentRecipes()
        observeSearch()
    }

    fun onRecipeSelected(recipeId: Long) {
        sendEffect(RecipeListUiEffect.NavigateToRecipeDetail(recipeId))
    }

    fun onLayoutModeSelected(mode: HomeLayoutMode) {
        if (currentState.layoutMode == mode) return
        viewModelScope.launch {
            layoutPreferences.setLayoutMode(mode)
        }
    }

    fun onSearchActivated() {
        updateState { it.copy(searchActive = true) }
    }

    fun onSearchDeactivated() {
        updateState { it.copy(searchActive = false) }
        resetSearch()
    }

    fun onSearchQueryChange(query: String) {
        if (searchQuery.value == query) return
        updateState { it.copy(searchQuery = query, isSearchDebouncing = true) }
        searchQuery.value = query
    }

    fun onSearchClear() {
        updateState { it.copy(searchQuery = "", isSearchDebouncing = false) }
        searchQuery.value = ""
        applyFilterNow("")
    }

    fun onSearchSubmit() {
        applyFilterNow(searchQuery.value)
    }

    fun onRetryRequested() {
        observeRecipes()
        observeRecentRecipes()
    }

    private fun observeLayoutMode() {
        viewModelScope.launch {
            layoutPreferences.layoutMode.collectLatest { mode ->
                updateState { it.copy(layoutMode = mode) }
            }
        }
    }

    private fun observeRecipes() {
        recipesJob?.cancel()
        recipesJob = viewModelScope.launch {
            recipeRepository.observeRecipes()
                .onStart {
                    updateState {
                        it.copy(
                            isLoading = true,
                            listStatus = RecipeFeedStatus.Loading,
                            errorMessage = null
                        )
                    }
                }
                .catch { throwable ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            listStatus = RecipeFeedStatus.Error,
                            errorMessage = throwable.message.orEmpty()
                        )
                    }
                }
                .collectLatest { feed ->
                    val all = feed.all.map { it.toSummaryUi() }
                    val filtered = feed.filtered.map { it.toSummaryUi() }
                    val query = feed.filters.query
                    updateState { current ->
                        current.copy(
                            recipes = all,
                            filteredRecipes = filtered,
                            isLoading = false,
                            listStatus = determineListStatus(
                                allRecipes = all,
                                filteredRecipes = filtered,
                                query = query,
                                hasError = false
                            ),
                            searchQuery = query,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private fun observeRecentRecipes() {
        recentRecipesJob?.cancel()
        recentRecipesJob = viewModelScope.launch {
            recipeRepository.observeRecentRecipes()
                .onStart {
                    updateState {
                        it.copy(
                            isRecentLoading = true,
                            recentStatus = RecentRecipesStatus.Loading,
                            recentErrorMessage = null
                        )
                    }
                }
                .catch { throwable ->
                    updateState {
                        it.copy(
                            isRecentLoading = false,
                            recentStatus = RecentRecipesStatus.Error,
                            recentErrorMessage = throwable.message.orEmpty()
                        )
                    }
                }
                .collectLatest { recipes ->
                    val mapped = recipes.map { it.toSummaryUi() }
                    updateState {
                        it.copy(
                            recentRecipes = mapped,
                            isRecentLoading = false,
                            recentStatus = if (mapped.isEmpty()) {
                                RecentRecipesStatus.Empty
                            } else {
                                RecentRecipesStatus.Content
                            },
                            recentErrorMessage = null
                        )
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .collectLatest { query ->
                    applyFilterNow(query)
                }
        }
    }

    private fun applyFilterNow(query: String) {
        recipeRepository.setSearchQuery(query)
        updateState { current ->
            current.copy(
                searchQuery = query,
                isSearchDebouncing = false
            )
        }
    }

    private fun resetSearch() {
        searchQuery.value = ""
        applyFilterNow("")
    }

    private fun determineListStatus(
        allRecipes: List<RecipeSummaryUi>,
        filteredRecipes: List<RecipeSummaryUi>,
        query: String,
        hasError: Boolean
    ): RecipeFeedStatus {
        if (hasError) return RecipeFeedStatus.Error
        if (allRecipes.isEmpty()) return RecipeFeedStatus.Empty
        if (query.isNotBlank() && filteredRecipes.isEmpty()) return RecipeFeedStatus.EmptySearch
        return RecipeFeedStatus.Content
    }

    private fun Recipe.toSummaryUi(): RecipeSummaryUi =
        RecipeSummaryUi(
            id = id,
            title = title,
            duration = null,
            cookbooks = emptyList()
        )
}

data class HomeRecipeListUiState(
    val isLoading: Boolean = true,
    val isRecentLoading: Boolean = true,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.List,
    val searchQuery: String = "",
    val searchActive: Boolean = false,
    val isSearchDebouncing: Boolean = false,
    val recipes: List<RecipeSummaryUi> = emptyList(),
    val filteredRecipes: List<RecipeSummaryUi> = emptyList(),
    val recentRecipes: List<RecipeSummaryUi> = emptyList(),
    val listStatus: RecipeFeedStatus = RecipeFeedStatus.Loading,
    val recentStatus: RecentRecipesStatus = RecentRecipesStatus.Loading,
    val errorMessage: String? = null,
    val recentErrorMessage: String? = null
) {
    val totalCount: Int = filteredRecipes.size
}

enum class RecipeFeedStatus {
    Loading,
    Content,
    Empty,
    EmptySearch,
    Error
}

enum class RecentRecipesStatus {
    Loading,
    Content,
    Empty,
    Error
}

data class RecipeSummaryUi(
    val id: Long,
    val title: String,
    val duration: Duration?,
    val cookbooks: List<String>
)

sealed interface RecipeListUiEffect {
    data class NavigateToRecipeDetail(val recipeId: Long) : RecipeListUiEffect
}
