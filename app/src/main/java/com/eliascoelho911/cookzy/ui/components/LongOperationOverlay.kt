package com.eliascoelho911.cookzy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ui.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ui.preview.ThemePreviews

/**
 * Full‑screen blocking overlay for long operations.
 * Shows a scrim covering the UI with a centered progress indicator and message.
 *
 * Place this as the last child in your screen content tree and toggle via [visible].
 */
@Composable
fun LongOperationOverlay(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    val scrim: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scrim)
            // Consume interactions to block underlying UI
            .clickable(enabled = true, onClick = {})
            .semantics {
                stateDescription = message
                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun LongOperationOverlayPreview() {
    PreviewWrapper {
        Box(Modifier.fillMaxSize()) {
            // Underlying content example (dimmed by scrim)
            Text(
                text = "Conteúdo da tela",
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.6f)
            )
            LongOperationOverlay(
                visible = true,
                message = "Importando receita…"
            )
        }
    }
}

