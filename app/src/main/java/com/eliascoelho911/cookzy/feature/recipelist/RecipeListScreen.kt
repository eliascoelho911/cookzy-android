package com.eliascoelho911.cookzy.feature.recipelist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.domain.model.RecipeListLayout
import com.eliascoelho911.cookzy.ui.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ui.preview.ThemePreviews
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeListRoute(
    onCreateRecipe: () -> Unit,
    onRecipeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecipeListScreen(
        state = state,
        onCreateRecipe = onCreateRecipe,
        onRecipeSelected = onRecipeSelected,
        onToggleSearch = viewModel::onToggleSearch,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearch = viewModel::onClearSearch,
        onToggleLayout = viewModel::onToggleLayout,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    state: RecipeListUiState,
    onCreateRecipe: () -> Unit,
    onRecipeSelected: (Long) -> Unit,
    onToggleSearch: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleLayout: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.searchActive) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = onSearchQueryChanged,
                            placeholder = { Text(stringResource(R.string.recipe_list_search_hint)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Text(stringResource(R.string.recipe_list_top_bar_title))
                    }
                },
                actions = {
                    if (!state.searchActive) {
                        TextButton(onClick = onToggleSearch) {
                            Text(stringResource(R.string.recipe_list_action_search))
                        }
                        TextButton(onClick = onCreateRecipe) {
                            Text(stringResource(R.string.recipe_list_new_recipe))
                        }
                    } else {
                        TextButton(onClick = onClearSearch) {
                            Text(stringResource(R.string.recipe_list_clear_search))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRecipe) {
                Text(stringResource(R.string.recipe_list_fab_label))
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RecentsSection(
                recents = state.recents,
                onRecipeSelected = onRecipeSelected,
                onCreateRecipe = onCreateRecipe
            )

            if (state.isLoading) {
                LoadingSection()
            } else if (state.error) {
                ErrorSection(onRetry = onRetry)
            } else if (state.isEmpty) {
                EmptyState(
                    onCreateRecipe = onCreateRecipe,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Header(
                    count = state.totalCount,
                    layout = state.layout,
                    onToggleLayout = onToggleLayout
                )
                when (state.layout) {
                    RecipeListLayout.LIST -> RecipeList(
                        recipes = state.recipes,
                        onRecipeSelected = onRecipeSelected,
                        modifier = Modifier.weight(1f)
                    )
                    RecipeListLayout.GRID -> RecipeGrid(
                        recipes = state.recipes,
                        onRecipeSelected = onRecipeSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onCreateRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.recipe_list_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.recipe_list_empty_body),
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(onClick = onCreateRecipe) {
            Text(stringResource(R.string.recipe_list_empty_action))
        }
    }
}

@Composable
private fun Header(
    count: Int,
    layout: RecipeListLayout,
    onToggleLayout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.recipe_list_header_count, count))
        val contentDescription = stringResource(
            if (layout == RecipeListLayout.LIST) R.string.recipe_list_layout_switch_to_grid else R.string.recipe_list_layout_switch_to_list
        )
        TextButton(
            onClick = onToggleLayout,
            modifier = Modifier.semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
        ) {
            Text(
                text = if (layout == RecipeListLayout.LIST) stringResource(R.string.recipe_list_layout_grid) else stringResource(
                    R.string.recipe_list_layout_list
                )
            )
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<RecipeSummaryUi>,
    onRecipeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeListItem(
                recipe = recipe,
                onClick = { onRecipeSelected(recipe.id) }
            )
        }
    }
}

@Composable
private fun RecipeListItem(
    recipe: RecipeSummaryUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentDescription = stringResource(R.string.a11y_open_recipe, recipe.title)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RecipeGrid(
    recipes: List<RecipeSummaryUi>,
    onRecipeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeSelected(recipe.id) }
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipeSummaryUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentDescription = stringResource(R.string.a11y_open_recipe, recipe.title)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecentsSection(
    recents: List<RecipeSummaryUi>,
    onRecipeSelected: (Long) -> Unit,
    onCreateRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.recipe_list_recents_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (recents.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.recipe_list_recents_empty))
                TextButton(onClick = onCreateRecipe) {
                    Text(stringResource(R.string.recipe_list_recents_cta_create))
                }
            }
        } else {
            HorizontalPager(
                pageSize = PageSize.Fill,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                state = rememberPagerState(pageCount = { recents.size })
            ) { page ->
                val item = recents[page]
                RecipeCard(
                    recipe = item,
                    onClick = { onRecipeSelected(item.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LoadingSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ErrorSection(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.recipe_list_error_generic))
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.common_retry))
        }
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeList_Empty() {
    PreviewWrapper {
        RecipeListScreen(
            state = RecipeListPreviewData.stateEmpty(),
            onCreateRecipe = {},
            onRecipeSelected = {},
            onToggleSearch = {},
            onSearchQueryChanged = {},
            onClearSearch = {},
            onToggleLayout = {},
            onRetry = {}
        )
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeList_Populated() {
    PreviewWrapper {
        RecipeListScreen(
            state = RecipeListPreviewData.statePopulated(),
            onCreateRecipe = {},
            onRecipeSelected = {},
            onToggleSearch = {},
            onSearchQueryChanged = {},
            onClearSearch = {},
            onToggleLayout = {},
            onRetry = {}
        )
    }
}
