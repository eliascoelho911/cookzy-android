package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import kotlinx.coroutines.launch

@Composable
fun RecipeEditorRoute(
    repository: RecipeRepository,
    onNavigateToDetail: (Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeEditorViewModel = viewModel(
        factory = RecipeEditorViewModel.provideFactory(repository)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecipeEditorUiEffect.NavigateToRecipeDetail -> onNavigateToDetail(effect.recipeId)
                RecipeEditorUiEffect.CloseWithoutSaving -> onClose()
                is RecipeEditorUiEffect.ShowError -> coroutineScope.launch {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    RecipeEditorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onTitleChange = viewModel::onTitleChange,
        onIngredientChange = viewModel::onIngredientChange,
        onAddIngredient = viewModel::addIngredient,
        onRemoveIngredient = viewModel::removeIngredient,
        onStepChange = viewModel::onStepChange,
        onAddStep = viewModel::addStep,
        onRemoveStep = viewModel::removeStep,
        onCancel = viewModel::onCancel,
        onSave = viewModel::onSave,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeEditorScreen(
    state: RecipeEditorUiState,
    snackbarHostState: SnackbarHostState,
    onTitleChange: (String) -> Unit,
    onIngredientChange: (String, (IngredientInput) -> IngredientInput) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onStepChange: (String, (StepInput) -> StepInput) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditing) "Editar receita" else "Nova receita"
                    )
                },
                actions = {
                    TextButton(
                        onClick = onCancel,
                        enabled = !state.isSaving
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TitleSection(
                    title = state.title,
                    titleError = state.titleError,
                    onTitleChange = onTitleChange
                )

                IngredientSection(
                    ingredients = state.ingredientInputs,
                    onIngredientChange = onIngredientChange,
                    onAddIngredient = onAddIngredient,
                    onRemoveIngredient = onRemoveIngredient
                )

                StepSection(
                    steps = state.stepInputs,
                    onStepChange = onStepChange,
                    onAddStep = onAddStep,
                    onRemoveStep = onRemoveStep
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSave,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text("Salvar receita")
                }

                TextButton(
                    onClick = onCancel,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Descartar alterações")
                }
            }
        }
    }
}

@Composable
private fun TitleSection(
    title: String,
    titleError: String?,
    onTitleChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Título",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            isError = titleError != null,
            singleLine = true,
            supportingText = {
                if (titleError != null) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun IngredientSection(
    ingredients: List<IngredientInput>,
    onIngredientChange: (String, (IngredientInput) -> IngredientInput) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RowTitle(title = "Ingredientes", actionLabel = "Adicionar", onActionClick = onAddIngredient)

        ingredients.forEachIndexed { index, ingredient ->
            IngredientCard(
                index = index,
                ingredient = ingredient,
                onIngredientChange = onIngredientChange,
                onRemoveIngredient = onRemoveIngredient,
                canRemove = ingredients.size > 1
            )
        }
    }
}

@Composable
private fun IngredientCard(
    index: Int,
    ingredient: IngredientInput,
    onIngredientChange: (String, (IngredientInput) -> IngredientInput) -> Unit,
    onRemoveIngredient: (String) -> Unit,
    canRemove: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Ingrediente ${index + 1}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = { value ->
                onIngredientChange(ingredient.id) { it.copy(name = value) }
            },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = ingredient.quantity,
            onValueChange = { value ->
                onIngredientChange(ingredient.id) { it.copy(quantity = value) }
            },
            label = { Text("Quantidade") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = { value ->
                onIngredientChange(ingredient.id) { it.copy(unit = value) }
            },
            label = { Text("Unidade") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = ingredient.note,
            onValueChange = { value ->
                onIngredientChange(ingredient.id) { it.copy(note = value) }
            },
            label = { Text("Observações") },
            modifier = Modifier.fillMaxWidth()
        )
        if (canRemove) {
            TextButton(
                onClick = { onRemoveIngredient(ingredient.id) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Remover ingrediente")
            }
        }
    }
}

@Composable
private fun StepSection(
    steps: List<StepInput>,
    onStepChange: (String, (StepInput) -> StepInput) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RowTitle(title = "Passos de preparo", actionLabel = "Adicionar passo", onActionClick = onAddStep)

        steps.forEachIndexed { index, step ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Passo ${index + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = step.description,
                    onValueChange = { value ->
                        onStepChange(step.id) { it.copy(description = value) }
                    },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (steps.size > 1) {
                    TextButton(
                        onClick = { onRemoveStep(step.id) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Remover passo")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowTitle(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onActionClick) {
            Text(actionLabel)
        }
    }
}
