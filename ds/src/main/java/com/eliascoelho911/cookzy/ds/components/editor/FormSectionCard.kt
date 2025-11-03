package com.eliascoelho911.cookzy.ds.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Composable
fun FormSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = FormSectionCardDefaults.containerShape(),
        color = FormSectionCardDefaults.containerColor(),
        shadowElevation = FormSectionCardDefaults.containerElevation(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FormSectionCardDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.ContentSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.HeaderSpacing),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.HeaderTextSpacing),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (!description.isNullOrBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.ActionsSpacing),
                    content = actions,
                )
            }

            content()
        }
    }
}

object FormSectionCardDefaults {
    val ContentPadding = PaddingValues(20.dp)
    val ContentSpacing = 20.dp
    val HeaderSpacing = 16.dp
    val HeaderTextSpacing = 4.dp
    val ActionsSpacing = 8.dp

    @Composable
    fun containerShape() = MaterialTheme.shapes.medium

    @Composable
    fun containerColor() = MaterialTheme.colorScheme.surface

    @Composable
    fun containerElevation() = 1.dp
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun FormSectionCardPreview() {
    PreviewWrapper {
        FormSectionCard(
            title = "Ingredientes",
            description = "Liste pelo menos um ingrediente para prosseguir.",
        ) {
            Text(
                text = "Conteúdo do formulário",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun FormSectionCardWithoutDescriptionPreview() {
    PreviewWrapper {
        FormSectionCard(title = "Preparo") {
            Text(
                text = "Descreva como preparar a receita",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
