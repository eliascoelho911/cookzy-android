package com.eliascoelho911.cookzy.feature.recipelist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutMode
import com.eliascoelho911.cookzy.ds.components.AppTopBar
import com.eliascoelho911.cookzy.ds.components.AppTopBarSearchUi
import com.eliascoelho911.cookzy.ds.components.EmptyState
import com.eliascoelho911.cookzy.ds.components.EmptyStateCopy
import com.eliascoelho911.cookzy.ds.components.ErrorState
import com.eliascoelho911.cookzy.ds.components.ErrorStateCopy
import com.eliascoelho911.cookzy.ds.components.IconToggle
import com.eliascoelho911.cookzy.ds.components.IconToggleOption
import com.eliascoelho911.cookzy.ds.components.RecentCarousel
import com.eliascoelho911.cookzy.ds.components.RecipeCard
import com.eliascoelho911.cookzy.ds.components.RecipeCardContent
import com.eliascoelho911.cookzy.ds.components.RecipeCardState
import com.eliascoelho911.cookzy.ds.components.RecipeCardVariant
import com.eliascoelho911.cookzy.ds.components.RecipeListDefaults
import com.eliascoelho911.cookzy.ds.components.StateAction
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RecipeListRoute(
    onCreateRecipe: () -> Unit,
    onRecipeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecipeListUiEffect.NavigateToRecipeDetail -> onRecipeSelected(effect.recipeId)
            }
        }
    }

    RecipeListScreen(
        state = state,
        onCreateRecipe = onCreateRecipe,
        onRecipeClick = viewModel::onRecipeSelected,
        onLayoutModeSelected = viewModel::onLayoutModeSelected,
        onSearchActivate = viewModel::onSearchActivated,
        onSearchDeactivate = viewModel::onSearchDeactivated,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchClear = viewModel::onSearchClear,
        onSearchSubmit = viewModel::onSearchSubmit,
        onRetry = viewModel::onRetryRequested,
        modifier = modifier
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListScreen(
    state: HomeRecipeListUiState,
    onCreateRecipe: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onLayoutModeSelected: (HomeLayoutMode) -> Unit,
    onSearchActivate: () -> Unit,
    onSearchDeactivate: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClear: () -> Unit,
    onSearchSubmit: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val searchUi = AppTopBarSearchUi(
        active = state.searchActive,
        query = state.searchQuery,
        placeholder = stringResource(id = R.string.recipe_list_search_placeholder),
        onActivate = onSearchActivate,
        onDeactivate = onSearchDeactivate,
        onQueryChange = onSearchQueryChange,
        onSubmit = onSearchSubmit,
        onClear = onSearchClear
    )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection).safeContentPadding(),
        topBar = {
            AppTopBar(
                title = stringResource(id = R.string.recipe_list_top_bar_title),
                searchUi = searchUi,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRecipe) {
                Icon(
                    imageVector = IconRegistry.Add,
                    contentDescription = stringResource(id = R.string.recipe_list_fab_content_description)
                )
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = state.layoutMode,
            label = "RecipeListLayoutMode",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { layoutMode ->
            when (layoutMode) {
                HomeLayoutMode.List -> RecipeListListMode(
                    state = state,
                    onRecipeClick = onRecipeClick,
                    onLayoutModeSelected = onLayoutModeSelected,
                    onCreateRecipe = onCreateRecipe,
                    onRetry = onRetry,
                    onSearchClear = onSearchClear
                )

                HomeLayoutMode.Grid -> RecipeListGridMode(
                    state = state,
                    onRecipeClick = onRecipeClick,
                    onLayoutModeSelected = onLayoutModeSelected,
                    onCreateRecipe = onCreateRecipe,
                    onRetry = onRetry,
                    onSearchClear = onSearchClear
                )
            }
        }
    }
}

@Composable
private fun RecipeListListMode(
    state: HomeRecipeListUiState,
    onRecipeClick: (Long) -> Unit,
    onLayoutModeSelected: (HomeLayoutMode) -> Unit,
    onCreateRecipe: () -> Unit,
    onRetry: () -> Unit,
    onSearchClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 24.dp,
            bottom = 128.dp
        )
    ) {
        item {
            RecentSection(
                state = state,
                onRecipeClick = onRecipeClick,
                onCreateRecipe = onCreateRecipe,
                onRetry = onRetry
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            RecipeListHeader(
                count = state.totalCount,
                layoutMode = state.layoutMode,
                onLayoutModeSelected = onLayoutModeSelected
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }

        when (state.listStatus) {
            RecipeFeedStatus.Loading -> {
                items(RecipeListDefaults.LoadingItemsCount) { index ->
                    RecipeCard(
                        variant = RecipeCardVariant.List,
                        state = RecipeCardState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = if (index == RecipeListDefaults.LoadingItemsCount - 1) {
                                    0.dp
                                } else {
                                    12.dp
                                }
                            )
                    )
                }
            }

            RecipeFeedStatus.Content -> {
                itemsIndexed(state.filteredRecipes, key = { _, recipe -> recipe.id }) { index, recipe ->
                    RecipeCard(
                        variant = RecipeCardVariant.List,
                        state = RecipeCardState.Content(recipe.toCardContent(onRecipeClick)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (index == state.filteredRecipes.lastIndex) 0.dp else 12.dp)
                    )
                }
            }

            RecipeFeedStatus.Empty -> {
                item {
                    EmptyState(
                        copy = EmptyStateCopy(
                            title = stringResource(id = R.string.recipe_list_empty_title),
                            message = stringResource(id = R.string.recipe_list_empty_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_empty_action),
                            onClick = onCreateRecipe
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.EmptySearch -> {
                item {
                    EmptyState(
                        copy = EmptyStateCopy(
                            title = stringResource(id = R.string.recipe_list_search_empty_title, state.searchQuery.trim()),
                            message = stringResource(id = R.string.recipe_list_search_empty_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_search_empty_clear),
                            onClick = onSearchClear
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.Error -> {
                item {
                    ErrorState(
                        copy = ErrorStateCopy(
                            title = stringResource(id = R.string.recipe_list_error_title),
                            message = stringResource(id = R.string.recipe_list_error_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_error_retry),
                            onClick = onRetry
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeListGridMode(
    state: HomeRecipeListUiState,
    onRecipeClick: (Long) -> Unit,
    onLayoutModeSelected: (HomeLayoutMode) -> Unit,
    onCreateRecipe: () -> Unit,
    onRetry: () -> Unit,
    onSearchClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 24.dp,
            bottom = 128.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            RecentSection(
                state = state,
                onRecipeClick = onRecipeClick,
                onCreateRecipe = onCreateRecipe,
                onRetry = onRetry
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            RecipeListHeader(
                count = state.totalCount,
                layoutMode = state.layoutMode,
                onLayoutModeSelected = onLayoutModeSelected
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (state.listStatus) {
            RecipeFeedStatus.Loading -> {
                items(RecipeListDefaults.LoadingItemsCount) { index ->
                    RecipeCard(
                        variant = RecipeCardVariant.Tile,
                        state = RecipeCardState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.Content -> {
                items(state.filteredRecipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        variant = RecipeCardVariant.Tile,
                        state = RecipeCardState.Content(recipe.toCardContent(onRecipeClick)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.Empty -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        copy = EmptyStateCopy(
                            title = stringResource(id = R.string.recipe_list_empty_title),
                            message = stringResource(id = R.string.recipe_list_empty_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_empty_action),
                            onClick = onCreateRecipe
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.EmptySearch -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        copy = EmptyStateCopy(
                            title = stringResource(id = R.string.recipe_list_search_empty_title, state.searchQuery.trim()),
                            message = stringResource(id = R.string.recipe_list_search_empty_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_search_empty_clear),
                            onClick = onSearchClear
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            RecipeFeedStatus.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorState(
                        copy = ErrorStateCopy(
                            title = stringResource(id = R.string.recipe_list_error_title),
                            message = stringResource(id = R.string.recipe_list_error_message)
                        ),
                        primaryAction = StateAction(
                            label = stringResource(id = R.string.recipe_list_error_retry),
                            onClick = onRetry
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSection(
    state: HomeRecipeListUiState,
    onRecipeClick: (Long) -> Unit,
    onCreateRecipe: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.recipe_list_recent_title),
            style = MaterialTheme.typography.titleLarge
        )
        when (state.recentStatus) {
            RecentRecipesStatus.Loading -> {
                RecentCarouselSkeleton()
            }

            RecentRecipesStatus.Content -> {
                RecentCarousel(
                    items = state.recentRecipes.map { it.toCardContent(onRecipeClick) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RecentRecipesStatus.Empty -> {
                EmptyState(
                    copy = EmptyStateCopy(
                        title = stringResource(id = R.string.recipe_list_recent_empty_title),
                        message = stringResource(id = R.string.recipe_list_recent_empty_message)
                    ),
                    primaryAction = StateAction(
                        label = stringResource(id = R.string.recipe_list_recent_empty_action),
                        onClick = onCreateRecipe
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            RecentRecipesStatus.Error -> {
                ErrorState(
                    copy = ErrorStateCopy(
                        title = stringResource(id = R.string.recipe_list_recent_error_title),
                        message = stringResource(id = R.string.recipe_list_recent_error_message)
                    ),
                    primaryAction = StateAction(
                        label = stringResource(id = R.string.recipe_list_recent_error_retry),
                        onClick = onRetry
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RecentCarouselSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(modifier = Modifier.weight(1f)) {
                RecipeCard(
                    variant = RecipeCardVariant.Tile,
                    state = RecipeCardState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RecipeListHeader(
    count: Int,
    layoutMode: HomeLayoutMode,
    onLayoutModeSelected: (HomeLayoutMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = pluralStringResource(
                id = R.plurals.recipe_list_count,
                count = count,
                count
            ),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val options = listOf(
            IconToggleOption(
                icon = IconRegistry.ViewList,
                contentDescription = stringResource(id = R.string.recipe_list_toggle_list)
            ),
            IconToggleOption(
                icon = IconRegistry.ViewTile,
                contentDescription = stringResource(id = R.string.recipe_list_toggle_grid)
            )
        )
        IconToggle(
            options = options,
            selectedIndex = if (layoutMode == HomeLayoutMode.List) 0 else 1,
            onSelect = { index ->
                val mode = if (index == 0) HomeLayoutMode.List else HomeLayoutMode.Grid
                onLayoutModeSelected(mode)
            }
        )
    }
}

private fun RecipeSummaryUi.toCardContent(
    onRecipeClick: (Long) -> Unit
): RecipeCardContent = RecipeCardContent(
    title = title,
    duration = duration,
    cookbooks = cookbooks,
    onClick = { onRecipeClick(id) }
)

@ThemePreviews
@Composable
private fun RecipeListScreenPreview_List() {
    PreviewWrapper {
        RecipeListScreen(
            state = RecipeListPreviewData.stateContent(),
            onCreateRecipe = {},
            onRecipeClick = {},
            onLayoutModeSelected = {},
            onSearchActivate = {},
            onSearchDeactivate = {},
            onSearchQueryChange = {},
            onSearchClear = {},
            onSearchSubmit = {},
            onRetry = {}
        )
    }
}

@ThemePreviews
@Composable
private fun RecipeListScreenPreview_Grid() {
    PreviewWrapper {
        RecipeListScreen(
            state = RecipeListPreviewData.stateContent().copy(layoutMode = HomeLayoutMode.Grid),
            onCreateRecipe = {},
            onRecipeClick = {},
            onLayoutModeSelected = {},
            onSearchActivate = {},
            onSearchDeactivate = {},
            onSearchQueryChange = {},
            onSearchClear = {},
            onSearchSubmit = {},
            onRetry = {}
        )
    }
}

object RecipeListPreviewData {
    private fun sampleRecipe(id: Long, title: String, duration: Duration? = null): RecipeSummaryUi =
        RecipeSummaryUi(
            id = id,
            title = title,
            duration = duration,
            cookbooks = listOf("Família", "Favoritas")
        )

    fun stateContent(): HomeRecipeListUiState = HomeRecipeListUiState(
        isLoading = false,
        isRecentLoading = false,
        layoutMode = HomeLayoutMode.List,
        recipes = listOf(
            sampleRecipe(1L, "Pão de Fermentação Natural", 120.minutes),
            sampleRecipe(2L, "Lasanha Vegetariana", 70.minutes),
            sampleRecipe(3L, "Bolo de Cenoura com Cobertura")
        ),
        filteredRecipes = listOf(
            sampleRecipe(1L, "Pão de Fermentação Natural", 120.minutes),
            sampleRecipe(2L, "Lasanha Vegetariana", 70.minutes),
            sampleRecipe(3L, "Bolo de Cenoura com Cobertura")
        ),
        recentRecipes = listOf(
            sampleRecipe(4L, "Risoto de Cogumelos", 40.minutes),
            sampleRecipe(5L, "Waffles Integrais", 30.minutes)
        ),
        listStatus = RecipeFeedStatus.Content,
        recentStatus = RecentRecipesStatus.Content
    )
}
