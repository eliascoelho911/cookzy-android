package com.eliascoelho911.cookzy.feature.recipeeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.ds.components.AppTopBar
import com.eliascoelho911.cookzy.ds.components.editor.EditorTextField
import com.eliascoelho911.cookzy.ds.components.editor.FormSectionCard
import com.eliascoelho911.cookzy.ds.components.editor.FormSectionCardDefaults
import com.eliascoelho911.cookzy.ds.components.editor.IngredientEditorRow
import com.eliascoelho911.cookzy.ds.components.editor.InstructionEditorRow
import com.eliascoelho911.cookzy.ds.components.editor.ReorderableDragHandle
import com.eliascoelho911.cookzy.ds.components.editor.ReorderableList
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorScreenDefaults.ItemsSpacing
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorScreenDefaults.SectionsSpacing
import kotlinx.coroutines.launch

@Composable
fun RecipeEditorRoute(
    onNavigateToDetail: (Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeEditorViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecipeEditorUiEffect.NavigateToRecipeDetail -> onNavigateToDetail(effect.recipeId)
                RecipeEditorUiEffect.CloseWithoutSaving -> onClose()
                is RecipeEditorUiEffect.ShowError -> coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))
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
        onMoveIngredient = viewModel::moveIngredient,
        onStepChange = viewModel::onStepChange,
        onAddStep = viewModel::addStep,
        onRemoveStep = viewModel::removeStep,
        onMoveStep = viewModel::moveStep,
        onCancel = viewModel::onCancel,
        onSave = viewModel::onSave,
        onConsumeIngredientFocus = viewModel::consumeIngredientFocus,
        onConsumeStepFocus = viewModel::consumeStepFocus,
        onConsumeTitleFocus = viewModel::consumeTitleFocus,
        modifier = modifier.safeContentPadding(),
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
    onMoveIngredient: (Int, Int) -> Unit,
    onStepChange: (String, (StepInput) -> StepInput) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (String) -> Unit,
    onMoveStep: (Int, Int) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onConsumeIngredientFocus: () -> Unit,
    onConsumeStepFocus: () -> Unit,
    onConsumeTitleFocus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val titleFocusRequester = remember { FocusRequester() }
    val ingredientFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }
    val stepFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }

    LaunchedEffect(state.focusTitle) {
        if (state.focusTitle) {
            titleFocusRequester.requestFocus()
            onConsumeTitleFocus()
        }
    }

    LaunchedEffect(state.focusedIngredientId) {
        val focusId = state.focusedIngredientId ?: return@LaunchedEffect
        val requester = ingredientFocusRequesters[focusId]
        if (requester != null) {
            requester.requestFocus()
            onConsumeIngredientFocus()
        }
    }

    LaunchedEffect(state.focusedStepId) {
        val focusId = state.focusedStepId ?: return@LaunchedEffect
        val requester = stepFocusRequesters[focusId]
        if (requester != null) {
            requester.requestFocus()
            onConsumeStepFocus()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(
                    if (state.isEditing) {
                        R.string.recipe_editor_top_bar_title_edit
                    } else {
                        R.string.recipe_editor_top_bar_title_new
                    },
                ),
                navigationIcon = {
                    IconButton(onClick = onCancel, enabled = !state.isSaving) {
                        Icon(
                            imageVector = IconRegistry.Back,
                            contentDescription = stringResource(id = R.string.editor_back),
                        )
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        IconButton(onClick = onSave, enabled = state.canSave) {
                            Icon(imageVector = IconRegistry.Check, contentDescription = null)
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Bound the reorderable lazy lists to a finite height to avoid infinite measurement crashes.
            val listMaxHeight = (maxHeight - 48.dp).coerceAtLeast(0.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(SectionsSpacing),
            ) {
                FormSectionCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    EditorTextField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        label = stringResource(id = R.string.editor_title_label),
                        placeholder = stringResource(id = R.string.editor_title_placeholder),
                        isError = state.titleError != null,
                        supportingText = state.titleError?.let { stringResource(id = it) },
                        focusRequester = titleFocusRequester,
                        modifier = Modifier.focusRequester(titleFocusRequester),
                    )
                }

                FormSectionCard(
                    title = stringResource(id = R.string.editor_ingredients_title),
                    description = stringResource(id = R.string.editor_ingredients_description),
                ) {
                    val lastIngredientFilled = state.ingredientInputs.lastOrNull()
                        ?.rawText
                        ?.isNotBlank() == true
                    ReorderableList(
                        items = state.ingredientInputs,
                        key = { it.id },
                        onMove = onMoveIngredient,
                        modifier = Modifier.heightIn(max = listMaxHeight),
                        verticalArrangement = Arrangement.spacedBy(ItemsSpacing),
                    ) { index, ingredient ->
                        val focusRequester =
                            ingredientFocusRequesters.getOrPut(ingredient.id) { FocusRequester() }
                        val isError =
                            state.validationTriggered && ingredient.id in state.ingredientErrors
                        val isLast = index == state.ingredientInputs.lastIndex
                        val hasText = ingredient.rawText.isNotBlank()
                        val canHandleEnter = !state.isSaving && (hasText || !isLast)
                        val handleSubmit = {
                            if (isLast) {
                                if (hasText) {
                                    onAddIngredient()
                                }
                            } else {
                                val nextId = state.ingredientInputs.getOrNull(index + 1)?.id
                                if (nextId != null) {
                                    val nextRequester =
                                        ingredientFocusRequesters.getOrPut(nextId) { FocusRequester() }
                                    nextRequester.requestFocus()
                                }
                            }
                        }
                        IngredientEditorRow(
                            value = ingredient.rawText,
                            onValueChange = { value ->
                                onIngredientChange(ingredient.id) { input ->
                                    input.copy(rawText = value)
                                }
                            },
                            isError = isError,
                            errorText = if (isError) stringResource(id = R.string.editor_ingredients_error_required) else null,
                            onRemove = {
                                if (!state.isSaving) {
                                    onRemoveIngredient(ingredient.id)
                                }
                            },
                            dragHandle = {
                                ReorderableDragHandle()
                            },
                            //                            placeholder = stringResource(id = R.string.editor_ingredients_placeholder),
                            focusRequester = focusRequester,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    if (canHandleEnter) handleSubmit()
                                },
                                onDone = {
                                    if (canHandleEnter) handleSubmit()
                                },
                            ),
                            textFieldModifier = Modifier.onPreviewKeyEvent { keyEvent ->
                                if (
                                    canHandleEnter &&
                                    keyEvent.type == KeyEventType.KeyUp &&
                                    keyEvent.key == Key.Enter &&
                                    !keyEvent.isShiftPressed
                                ) {
                                    handleSubmit()
                                    true
                                } else {
                                    false
                                }
                            },
                        )
                    }

                    if (lastIngredientFilled) {
                        TextButton(
                            onClick = onAddIngredient,
                            enabled = !state.isSaving,
                            modifier = Modifier.align(Alignment.Start),
                        ) {
                            Text(text = stringResource(id = R.string.editor_ingredients_add))
                        }
                    }
                }

                FormSectionCard(
                    title = stringResource(id = R.string.editor_instructions_title),
                    description = stringResource(id = R.string.editor_instructions_description),
                ) {
                    val lastStepFilled = state.stepInputs.lastOrNull()
                        ?.description
                        ?.isNotBlank() == true
                    ReorderableList(
                        items = state.stepInputs,
                        key = { it.id },
                        onMove = onMoveStep,
                        modifier = Modifier.heightIn(max = listMaxHeight),
                        verticalArrangement = Arrangement.spacedBy(ItemsSpacing),
                    ) { index, step ->
                        val focusRequester =
                            stepFocusRequesters.getOrPut(step.id) { FocusRequester() }
                        val isError = state.validationTriggered && step.id in state.stepErrors
                        val isLast = index == state.stepInputs.lastIndex
                        val hasText = step.description.isNotBlank()
                        val canHandleEnter = !state.isSaving && (hasText || !isLast)
                        val handleSubmit = {
                            if (isLast) {
                                if (hasText) {
                                    onAddStep()
                                }
                            } else {
                                val nextId = state.stepInputs.getOrNull(index + 1)?.id
                                if (nextId != null) {
                                    val nextRequester =
                                        stepFocusRequesters.getOrPut(nextId) { FocusRequester() }
                                    nextRequester.requestFocus()
                                }
                            }
                        }
                        InstructionEditorRow(
                            index = index,
                            value = step.description,
                            onValueChange = { value ->
                                onStepChange(step.id) { input ->
                                    input.copy(description = value)
                                }
                            },
                            isError = isError,
                            errorText = if (isError) stringResource(id = R.string.editor_instructions_error_required) else null,
                            onRemove = {
                                if (!state.isSaving) {
                                    onRemoveStep(step.id)
                                }
                            },
                            //                            placeholder = stringResource(id = R.string.editor_instructions_placeholder),
                            focusRequester = focusRequester,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    if (canHandleEnter) handleSubmit()
                                },
                                onDone = {
                                    if (canHandleEnter) handleSubmit()
                                },
                            ),
                            textFieldModifier = Modifier.onPreviewKeyEvent { keyEvent ->
                                if (
                                    canHandleEnter &&
                                    keyEvent.type == KeyEventType.KeyUp &&
                                    keyEvent.key == Key.Enter &&
                                    !keyEvent.isShiftPressed
                                ) {
                                    handleSubmit()
                                    true
                                } else {
                                    false
                                }
                            },
                        )
                    }

                    if (lastStepFilled) {
                        TextButton(
                            onClick = onAddStep,
                            enabled = !state.isSaving,
                            modifier = Modifier.align(Alignment.Start),
                        ) {
                            Text(text = stringResource(id = R.string.editor_instructions_add))
                        }
                    }
                }
            }
        }
    }
}

private object RecipeEditorScreenDefaults {
    val ItemsSpacing = 4.dp
    val SectionsSpacing = 4.dp
}

@ThemePreviews
@Composable
private fun PreviewRecipeEditorNew() {
    PreviewWrapper {
        RecipeEditorScreen(
            state = RecipeEditorPreviewData.stateNew(),
            snackbarHostState = SnackbarHostState(),
            onTitleChange = {},
            onIngredientChange = { _, _ -> },
            onAddIngredient = {},
            onRemoveIngredient = {},
            onMoveIngredient = { _, _ -> },
            onStepChange = { _, _ -> },
            onAddStep = {},
            onRemoveStep = {},
            onMoveStep = { _, _ -> },
            onCancel = {},
            onSave = {},
            onConsumeIngredientFocus = {},
            onConsumeStepFocus = {},
            onConsumeTitleFocus = {},
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewRecipeEditorEditing() {
    PreviewWrapper {
        RecipeEditorScreen(
            state = RecipeEditorPreviewData.stateEditing(),
            snackbarHostState = SnackbarHostState(),
            onTitleChange = {},
            onIngredientChange = { _, _ -> },
            onAddIngredient = {},
            onRemoveIngredient = {},
            onMoveIngredient = { _, _ -> },
            onStepChange = { _, _ -> },
            onAddStep = {},
            onRemoveStep = {},
            onMoveStep = { _, _ -> },
            onCancel = {},
            onSave = {},
            onConsumeIngredientFocus = {},
            onConsumeStepFocus = {},
            onConsumeTitleFocus = {},
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewRecipeEditorSaving() {
    PreviewWrapper {
        RecipeEditorScreen(
            state = RecipeEditorPreviewData.stateSaving(),
            snackbarHostState = SnackbarHostState(),
            onTitleChange = {},
            onIngredientChange = { _, _ -> },
            onAddIngredient = {},
            onRemoveIngredient = {},
            onMoveIngredient = { _, _ -> },
            onStepChange = { _, _ -> },
            onAddStep = {},
            onRemoveStep = {},
            onMoveStep = { _, _ -> },
            onCancel = {},
            onSave = {},
            onConsumeIngredientFocus = {},
            onConsumeStepFocus = {},
            onConsumeTitleFocus = {},
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewRecipeEditorValidationErrors() {
    PreviewWrapper {
        RecipeEditorScreen(
            state = RecipeEditorPreviewData.stateValidationErrors(),
            snackbarHostState = SnackbarHostState(),
            onTitleChange = {},
            onIngredientChange = { _, _ -> },
            onAddIngredient = {},
            onRemoveIngredient = {},
            onMoveIngredient = { _, _ -> },
            onStepChange = { _, _ -> },
            onAddStep = {},
            onRemoveStep = {},
            onMoveStep = { _, _ -> },
            onCancel = {},
            onSave = {},
            onConsumeIngredientFocus = {},
            onConsumeStepFocus = {},
            onConsumeTitleFocus = {},
        )
    }
}
