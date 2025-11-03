package com.eliascoelho911.cookzy.ds.components.editor

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Composable
fun EditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    maxChars: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusRequester: FocusRequester? = null,
) {
    EditorTextFieldImpl(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        singleLine = true,
        minLines = 1,
        isError = isError,
        supportingText = supportingText,
        maxChars = maxChars,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        focusRequester = focusRequester,
    )
}

@Composable
fun EditorMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    maxChars: Int? = null,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusRequester: FocusRequester? = null,
) {
    EditorTextFieldImpl(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        singleLine = false,
        minLines = minLines,
        isError = isError,
        supportingText = supportingText,
        maxChars = maxChars,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        focusRequester = focusRequester,
    )
}

@Composable
private fun EditorTextFieldImpl(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String?,
    modifier: Modifier,
    singleLine: Boolean,
    minLines: Int,
    isError: Boolean,
    supportingText: String?,
    maxChars: Int?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    focusRequester: FocusRequester?,
) {
    val counterText = maxChars?.let { limit ->
        stringResource(
            id = R.string.editor_textfield_counter,
            value.length.coerceAtMost(limit),
            limit,
        )
    }

    val labelSemanticsModifier = if (!label.isNullOrBlank()) {
        Modifier.semantics { contentDescription = label }
    } else {
        Modifier
    }

    val errorSemanticsModifier = if (isError && !supportingText.isNullOrBlank()) {
        Modifier.semantics { error(supportingText) }
    } else {
        Modifier
    }

    val focusModifier = focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
    val textFieldModifier = labelSemanticsModifier
        .then(errorSemanticsModifier)
        .then(focusModifier)
        .fillMaxWidth()

    Column(modifier = modifier.fillMaxWidth()) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val onValueChangeSanitized: (String) -> Unit = { newValue ->
            val sanitized = maxChars?.let { newValue.take(it) } ?: newValue
            onValueChange(sanitized)
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChangeSanitized,
            modifier = textFieldModifier,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                EditorTextFieldDecoration(
                    value = value,
                    label = label,
                    placeholder = placeholder,
                    isFocused = isFocused,
                    isError = isError,
                    innerTextField = innerTextField,
                )
            },
        )

        EditorSupportingText(
            supportingText = supportingText,
            counterText = counterText,
            isError = isError,
            showCounter = isFocused,
            modifier = Modifier.padding(top = EditorTextFieldDefaults.SupportingTextTopPadding),
        )
    }
}

@Composable
private fun EditorSupportingText(
    supportingText: String?,
    counterText: String?,
    isError: Boolean,
    showCounter: Boolean,
    modifier: Modifier = Modifier,
) {
    val shouldShowCounter = showCounter && counterText != null
    if (supportingText.isNullOrBlank() && !shouldShowCounter) return

    Row(modifier = modifier.fillMaxWidth()) {
        if (!supportingText.isNullOrBlank()) {
            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = supportingText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else if (shouldShowCounter) {
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (shouldShowCounter) {
            Text(
                text = counterText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

object EditorTextFieldDefaults {
    val ContentPadding = PaddingValues(vertical = 12.dp)
    val SupportingTextTopPadding = 4.dp
    val LabelBottomPadding = 4.dp
    val LabelHorizontalPadding = 0.dp
}

@Composable
private fun EditorTextFieldDecoration(
    value: String,
    label: String?,
    placeholder: String?,
    isFocused: Boolean,
    isError: Boolean,
    innerTextField: @Composable () -> Unit,
) {
    Column {
        if (!label.isNullOrBlank()) {
            val labelColor = when {
                isFocused -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                modifier = Modifier.padding(
                    start = EditorTextFieldDefaults.LabelHorizontalPadding,
                    end = EditorTextFieldDefaults.LabelHorizontalPadding,
                    bottom = EditorTextFieldDefaults.LabelBottomPadding,
                ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            if (value.isEmpty() && !placeholder.isNullOrBlank()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            innerTextField()
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun EditorTextFieldPreview() {
    PreviewWrapper {
        Surface {
            EditorTextField(
                value = "Chocolate amargo",
                onValueChange = {},
                label = "Ingrediente",
                placeholder = "Ex.: Chocolate 70%",
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun EditorTextFieldErrorPreview() {
    PreviewWrapper {
        Surface {
            EditorTextField(
                value = "",
                onValueChange = {},
                label = "Ingrediente",
                placeholder = "Ex.: Chocolate 70%",
                isError = true,
                supportingText = "Informe pelo menos um ingrediente",
                maxChars = 100,
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun EditorMultilineFieldPreview() {
    PreviewWrapper {
        Surface {
            EditorMultilineField(
                value = "Derreta o chocolate em banho-maria e misture com o creme.",
                onValueChange = {},
                label = "Passo",
                maxChars = 280,
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun EditorMultilineFieldErrorPreview() {
    PreviewWrapper {
        Surface {
            EditorMultilineField(
                value = "",
                onValueChange = {},
                label = "Passo",
                isError = true,
                supportingText = "Descreva o passo",
                minLines = 4,
            )
        }
    }
}
