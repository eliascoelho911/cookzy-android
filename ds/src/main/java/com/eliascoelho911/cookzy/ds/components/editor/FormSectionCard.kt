package com.eliascoelho911.cookzy.ds.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews

@Composable
fun FormSectionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    colors: CardColors = FormSectionCardDefaults.colors(),
    shape: CornerBasedShape = FormSectionCardDefaults.containerShape(),
    actions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FormSectionCardDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.ContentSpacing),
        ) {
            if (!title.isNullOrBlank() || actions != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.HeaderSpacing),
                    verticalAlignment = Alignment.Top,
                ) {
                    if (!title.isNullOrBlank()) {
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
                    }

                    actions?.let {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(FormSectionCardDefaults.ActionsSpacing),
                            content = it,
                        )
                    }
                }
            }

            content()
        }
    }
}

object FormSectionCardDefaults {
    val ContentPadding = PaddingValues(20.dp)
    val ContentSpacing = 8.dp
    val HeaderSpacing = 16.dp
    val HeaderTextSpacing = 4.dp
    val ActionsSpacing = 8.dp

    @Composable
    fun containerShape() = MaterialTheme.shapes.medium

    @Composable
    fun colors() = CardDefaults.cardColors()
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
private fun FormSectionCardWithoutTitlePreview() {
    PreviewWrapper {
        FormSectionCard {
            Text(
                text = "Descreva como preparar a receita",
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
