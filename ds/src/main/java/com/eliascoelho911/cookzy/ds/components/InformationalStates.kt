package com.eliascoelho911.cookzy.ds.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Immutable
data class StateAction(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

@Immutable
data class EmptyStateCopy(
    val title: String,
    val message: String,
)

object InformationalStateDefaults {
    val ContentPadding: PaddingValues = PaddingValues(24.dp)
    val ContentSpacing: Dp = 16.dp
    val ActionsSpacing: Dp = 8.dp
    val IllustrationSize: Dp = 72.dp

    @Composable
    fun shape(): Shape = MaterialTheme.shapes.medium

    @Composable
    fun emptyContainerColor(): Color = MaterialTheme.colorScheme.surfaceContainerLow

    @Composable
    fun emptyContentColor(): Color = MaterialTheme.colorScheme.onSurface

    @Composable
    fun errorContainerColor(): Color = MaterialTheme.colorScheme.errorContainer

    @Composable
    fun errorContentColor(): Color = MaterialTheme.colorScheme.onErrorContainer
}

object EmptyStateDefaults {
    @Composable
    fun default(): EmptyStateCopy = EmptyStateCopy(
        title = stringResource(id = R.string.empty_state_default_title),
        message = stringResource(id = R.string.empty_state_default_message),
    )

    fun copy(title: String, message: String) = EmptyStateCopy(title, message)

    @Composable
    fun Illustration(
        modifier: Modifier = Modifier,
        icon: ImageVector = IconRegistry.Info,
        tint: Color = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier.size(InformationalStateDefaults.IllustrationSize),
            tint = tint,
        )
    }
}

@Immutable
data class ErrorStateCopy(
    val title: String,
    val message: String,
)

object ErrorStateDefaults {
    @Composable
    fun default(): ErrorStateCopy = ErrorStateCopy(
        title = stringResource(id = R.string.error_state_default_title),
        message = stringResource(id = R.string.error_state_default_message),
    )

    fun copy(title: String, message: String) = ErrorStateCopy(title, message)

    @Composable
    fun Illustration(
        modifier: Modifier = Modifier,
        icon: ImageVector = IconRegistry.Error,
        tint: Color = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier.size(InformationalStateDefaults.IllustrationSize),
            tint = tint,
        )
    }
}

@Composable
fun EmptyState(
    copy: EmptyStateCopy,
    modifier: Modifier = Modifier,
    illustration: (@Composable (() -> Unit))? = { EmptyStateDefaults.Illustration() },
    primaryAction: StateAction? = null,
    secondaryAction: StateAction? = null,
    shape: Shape = InformationalStateDefaults.shape(),
    containerColor: Color = InformationalStateDefaults.emptyContainerColor(),
    contentColor: Color = InformationalStateDefaults.emptyContentColor(),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
    ) {
        StateContent(
            title = copy.title,
            message = copy.message,
            illustration = illustration,
            primaryAction = primaryAction,
            secondaryAction = secondaryAction,
            contentColor = contentColor,
        )
    }
}

@Composable
fun ErrorState(
    copy: ErrorStateCopy,
    modifier: Modifier = Modifier,
    illustration: (@Composable (() -> Unit))? = { ErrorStateDefaults.Illustration() },
    primaryAction: StateAction? = null,
    secondaryAction: StateAction? = null,
    shape: Shape = InformationalStateDefaults.shape(),
    containerColor: Color = InformationalStateDefaults.errorContainerColor(),
    contentColor: Color = InformationalStateDefaults.errorContentColor(),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
    ) {
        StateContent(
            title = copy.title,
            message = copy.message,
            illustration = illustration,
            primaryAction = primaryAction,
            secondaryAction = secondaryAction,
            contentColor = contentColor,
        )
    }
}

@Composable
private fun StateContent(
    title: String,
    message: String,
    illustration: (@Composable (() -> Unit))?,
    primaryAction: StateAction?,
    secondaryAction: StateAction?,
    contentColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(InformationalStateDefaults.ContentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(InformationalStateDefaults.ContentSpacing),
    ) {
        illustration?.let {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .clearAndSetSemantics { },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                it()
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor,
            modifier = Modifier.semantics { heading() },
            textAlign = TextAlign.Center,
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            textAlign = TextAlign.Center,
        )

        if (primaryAction != null || secondaryAction != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(InformationalStateDefaults.ActionsSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                primaryAction?.let { action ->
                    Button(
                        onClick = action.onClick,
                        enabled = action.enabled,
                    ) {
                        Text(text = action.label)
                    }
                }

                secondaryAction?.let { action ->
                    TextButton(
                        onClick = action.onClick,
                        enabled = action.enabled,
                    ) {
                        Text(text = action.label)
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(fontScale = 2f, name = "FontScale 2x", showBackground = true)
@Composable
private fun EmptyStatePreview() {
    PreviewWrapper {
        var primaryClicks by remember { mutableStateOf(0) }
        EmptyState(
            copy = EmptyStateDefaults.default(),
            primaryAction = StateAction(
                label = stringResource(id = R.string.empty_state_default_primary_action),
                onClick = { primaryClicks++ },
            ),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun EmptyStateWithoutIllustrationPreview() {
    PreviewWrapper {
        EmptyState(
            copy = EmptyStateCopy(
                title = "Nenhum livro salvo",
                message = "Salve seus favoritos para acessá-los rapidamente.",
            ),
            illustration = null,
            secondaryAction = StateAction(
                label = stringResource(id = R.string.common_close),
                onClick = {},
            ),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(fontScale = 2f, name = "FontScale 2x", showBackground = true)
@Composable
private fun ErrorStatePreview() {
    PreviewWrapper {
        ErrorState(
            copy = ErrorStateDefaults.default(),
            primaryAction = StateAction(
                label = stringResource(id = R.string.common_retry),
                onClick = {},
            ),
        )
    }
}
