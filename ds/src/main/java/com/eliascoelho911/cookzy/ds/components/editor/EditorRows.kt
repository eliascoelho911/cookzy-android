package com.eliascoelho911.cookzy.ds.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Composable
fun IngredientEditorRow(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorText: String?,
    onRemove: () -> Unit,
    dragHandle: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    maxChars: Int? = null,
    focusRequester: FocusRequester? = null,
    textFieldModifier: Modifier = Modifier,
) {
    ReorderableListItem(
        modifier = modifier,
        dragHandle = dragHandle,
        content = {
            EditorTextField(
                value = value,
                onValueChange = onValueChange,
                label = stringResource(id = R.string.editor_ingredient_label),
                placeholder = placeholder,
                modifier = Modifier
                    .weight(1f)
                    .then(textFieldModifier),
                isError = isError,
                supportingText = errorText,
                maxChars = maxChars,
                focusRequester = focusRequester,
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.sizeIn(
                    minWidth = ReorderableListDefaults.TouchTarget,
                    minHeight = ReorderableListDefaults.TouchTarget,
                ),
            ) {
                Icon(
                    imageVector = IconRegistry.Delete,
                    contentDescription = stringResource(id = R.string.editor_remove_ingredient),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Composable
fun InstructionEditorRow(
    index: Int,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorText: String?,
    onRemove: () -> Unit,
    dragHandle: @Composable () -> Unit = {
        InstructionDragHandle(index = index)
    },
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    maxChars: Int? = null,
    onDuplicate: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    textFieldModifier: Modifier = Modifier,
) {
    ReorderableListItem(
        modifier = modifier,
        dragHandle = dragHandle,
        content = {
            EditorMultilineField(
                value = value,
                onValueChange = onValueChange,
                label = stringResource(id = R.string.editor_instruction_field_label),
                placeholder = placeholder,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(textFieldModifier),
                isError = isError,
                supportingText = errorText,
                maxChars = maxChars,
                focusRequester = focusRequester,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (onDuplicate != null) {
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.sizeIn(
                            minWidth = ReorderableListDefaults.TouchTarget,
                            minHeight = ReorderableListDefaults.TouchTarget,
                        ),
                    ) {
                        Icon(
                            imageVector = IconRegistry.Copy,
                            contentDescription = stringResource(id = R.string.editor_duplicate_instruction),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.sizeIn(
                        minWidth = ReorderableListDefaults.TouchTarget,
                        minHeight = ReorderableListDefaults.TouchTarget,
                    ),
                ) {
                    Icon(
                        imageVector = IconRegistry.Delete,
                        contentDescription = stringResource(id = R.string.editor_remove_instruction),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}

@Composable
fun InstructionDragHandle(
    index: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val dragDescription = stringResource(id = R.string.editor_drag_handle_description)

    Box(
        modifier = modifier
            .run { if (enabled) reorderableHandle() else this }
            .semantics { contentDescription = dragDescription }
            .padding(ReorderableListDefaults.HandlePadding),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = InstructionDragHandleDefaults.containerColor(),
            contentColor = InstructionDragHandleDefaults.contentColor(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

object InstructionDragHandleDefaults {
    @Composable
    fun containerColor() = MaterialTheme.colorScheme.secondaryContainer

    @Composable
    fun contentColor() = MaterialTheme.colorScheme.onSecondaryContainer
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun IngredientEditorRowPreview() {
    PreviewWrapper {
        Surface {
            IngredientEditorRow(
                value = "200g de chocolate amargo",
                onValueChange = {},
                isError = false,
                errorText = null,
                onRemove = {},
                dragHandle = { ReorderableDragHandle(enabled = false) },
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun IngredientEditorRowErrorPreview() {
    PreviewWrapper {
        Surface {
            IngredientEditorRow(
                value = "",
                onValueChange = {},
                isError = true,
                errorText = "Informe o ingrediente",
                onRemove = {},
                dragHandle = { ReorderableDragHandle(enabled = false) },
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun InstructionEditorRowPreview() {
    PreviewWrapper {
        Surface {
            InstructionEditorRow(
                index = 0,
                value = "Misture delicadamente até incorporar.",
                onValueChange = {},
                isError = false,
                errorText = null,
                onRemove = {},
                dragHandle = { InstructionDragHandle(index = 0, enabled = false) },
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun InstructionEditorRowErrorPreview() {
    PreviewWrapper {
        Surface {
            InstructionEditorRow(
                index = 1,
                value = "",
                onValueChange = {},
                isError = true,
                errorText = "Descreva o passo",
                onRemove = {},
                dragHandle = { InstructionDragHandle(index = 1, enabled = false) },
            )
        }
    }
}
