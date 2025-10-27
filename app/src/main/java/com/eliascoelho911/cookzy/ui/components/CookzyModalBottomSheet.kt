package com.eliascoelho911.cookzy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.role
import com.eliascoelho911.cookzy.ui.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ui.preview.ThemePreviews
import com.eliascoelho911.cookzy.ui.theme.AppTheme
import com.eliascoelho911.cookzy.R
import com.eliascoelho911.cookzy.ui.icons.IconRegistry

@Immutable
data class SheetButton(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Cookzy base ModalBottomSheet with:
 * - Title and Close action in header
 * - Dynamic scrollable body slot
 * - Optional footer with up to two actions (secondary + primary)
 *
 * This component is the foundation for all app bottom sheets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookzyModalBottomSheet(
    title: String,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = onDismissRequest,
    secondaryButton: SheetButton? = null,
    primaryButton: SheetButton? = null,
    content: @Composable ColumnScope.() -> Unit,
 ) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle() },
        modifier = modifier,
    ) {
        // Constrain max width for large screens, center horizontally
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 640.dp)
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Header(title = title, onCloseClick = onCloseClick)

                // Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    content = content,
                )

                // Footer (optional)
                if (secondaryButton != null || primaryButton != null) {
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    Footer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = 8.dp)
                            .navigationBarsPadding(),
                        secondary = secondaryButton,
                        primary = primaryButton,
                    )
                } else {
                    // Ensure some bottom padding to avoid tight edge near nav bar
                    Spacer(Modifier.height(16.dp).navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    onCloseClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp)
            .semantics(mergeDescendants = true) {
                stateDescription = title
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
        )
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.semantics { role = Role.Button },
        ) {
            Icon(
                imageVector = IconRegistry.Close,
                contentDescription = stringResource(id = R.string.common_close)
            )
        }
    }
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier,
    secondary: SheetButton?,
    primary: SheetButton?,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (secondary != null) {
            TextButton(
                onClick = secondary.onClick,
                enabled = secondary.enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = secondary.label)
            }
        }
        if (primary != null) {
            Button(
                onClick = primary.onClick,
                enabled = primary.enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = primary.label)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun CookzyModalBottomSheetPreview() {
    PreviewWrapper {
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        CookzyModalBottomSheet(
            title = "Adicionar receita",
            sheetState = state,
            onDismissRequest = {},
            secondaryButton = SheetButton(label = "Cancelar", onClick = {}),
            primaryButton = SheetButton(label = "Continuar", onClick = {}),
        ) {
            // Body content example
            Text(text = "Escolha uma opção para começar:")
            Spacer(Modifier.height(12.dp))
            Text(text = "• Importar receita (colar link)")
            Spacer(Modifier.height(8.dp))
            Text(text = "• Adicionar manualmente (abrir editor)")
        }
    }
}
