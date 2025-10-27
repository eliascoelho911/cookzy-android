package com.eliascoelho911.cookzy.feature.recipedetail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.eliascoelho911.cookzy.R
import androidx.compose.ui.tooling.preview.Preview
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Composable
fun RecipeDetailRoute(
    onNavigateBack: () -> Unit,
    onEditRecipe: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecipeDetailScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onEditRecipe = onEditRecipe,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailScreen(
    state: RecipeDetailUiState,
    onNavigateBack: () -> Unit,
    onEditRecipe: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recipe_detail_top_bar_title)) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text(stringResource(R.string.common_back))
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }

            state.errorMessageRes != null -> {
                ErrorState(
                    messageRes = state.errorMessageRes,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            else -> {
                RecipeDetailContent(
                    state = state,
                    onEditRecipe = onEditRecipe,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(
    state: RecipeDetailUiState,
    onEditRecipe: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (state.ingredients.isNotEmpty()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recipe_detail_ingredients_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    state.ingredients.forEach { ingredient ->
                        val bulletPrefix = stringResource(R.string.recipe_detail_ingredient_prefix)
                        val annotated = buildAnnotatedString {
                            append(bulletPrefix)
                            if (!bulletPrefix.endsWith(" ")) {
                                append(" ")
                            }
                            append(ingredient.rawText)
                            val range = ingredient.highlightRange
                            if (range != null && range.first >= 0 && range.last >= range.first) {
                                val prefixLength = length - ingredient.rawText.length
                                val start = prefixLength + range.first
                                val end = prefixLength + range.last + 1
                                if (start in 0..length && end in 0..length && end > start) {
                                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                                }
                            }
                        }
                        Text(text = annotated)
                    }
                }
            }
        }

        if (state.steps.isNotEmpty()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recipe_detail_steps_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    state.steps.forEach { step ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.recipe_detail_step_number, step.position),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(step.description)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.recipeId?.let { id ->
            Button(
                onClick = { onEditRecipe(id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.recipe_detail_edit_button))
            }
        }
    }
}

@Composable
private fun ErrorState(
    @StringRes messageRes: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onNavigateBack) {
            Text(stringResource(R.string.common_back))
        }
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeDetail_Loading() {
    PreviewWrapper {
        RecipeDetailScreen(
            state = RecipeDetailPreviewData.stateLoading(),
            onNavigateBack = {},
            onEditRecipe = {}
        )
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeDetail_Error() {
    PreviewWrapper {
        RecipeDetailScreen(
            state = RecipeDetailPreviewData.stateError(),
            onNavigateBack = {},
            onEditRecipe = {}
        )
    }
}

@ThemePreviews
@Composable
private fun Preview_RecipeDetail_Success() {
    PreviewWrapper {
        RecipeDetailScreen(
            state = RecipeDetailPreviewData.stateSuccess(),
            onNavigateBack = {},
            onEditRecipe = {}
        )
    }
}
