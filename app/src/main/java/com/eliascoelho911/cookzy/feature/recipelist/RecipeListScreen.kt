package com.eliascoelho911.cookzy.feature.recipelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.eliascoelho911.cookzy.R
import androidx.compose.ui.tooling.preview.Preview
import com.eliascoelho911.cookzy.ui.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ui.preview.ThemePreviews

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
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListScreen(
    state: RecipeListUiState,
    onCreateRecipe: () -> Unit,
    onRecipeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recipe_list_top_bar_title)) },
                actions = {
                    TextButton(onClick = onCreateRecipe) {
                        Text(stringResource(R.string.recipe_list_new_recipe))
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
        if (state.isEmpty) {
            EmptyState(
                onCreateRecipe = onCreateRecipe,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            RecipeList(
                recipes = state.recipes,
                onRecipeSelected = onRecipeSelected,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeList_Empty() {
    PreviewWrapper {
        RecipeListScreen(
            state = RecipeListPreviewData.stateEmpty(),
            onCreateRecipe = {},
            onRecipeSelected = {}
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
            onRecipeSelected = {}
        )
    }
}
