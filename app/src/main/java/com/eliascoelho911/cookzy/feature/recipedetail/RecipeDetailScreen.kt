package com.eliascoelho911.cookzy.feature.recipedetail

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
                title = { Text("Detalhes da receita") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Voltar")
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

            state.errorMessage != null -> {
                ErrorState(
                    message = state.errorMessage,
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
                        text = "Ingredientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    state.ingredients.forEach { ingredient ->
                        Text(
                            text = buildString {
                                append("• ")
                                append(ingredient.name)
                                val quantity = ingredient.quantity
                                val unit = ingredient.unit
                                if (!quantity.isNullOrBlank()) {
                                    append(" — ")
                                    append(quantity)
                                    if (!unit.isNullOrBlank()) {
                                        append(" ")
                                        append(unit)
                                    }
                                } else if (!unit.isNullOrBlank()) {
                                    append(" — ")
                                    append(unit)
                                }
                                if (!ingredient.note.isNullOrBlank()) {
                                    append(" (")
                                    append(ingredient.note)
                                    append(")")
                                }
                            }
                        )
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
                        text = "Modo de preparo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    state.steps.forEach { step ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Passo ${step.position}",
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
                Text("Editar receita")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onNavigateBack) {
            Text("Voltar")
        }
    }
}
