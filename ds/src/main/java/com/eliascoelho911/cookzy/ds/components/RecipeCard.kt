package com.eliascoelho911.cookzy.ds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.loading.shimmerLoading
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import com.valentinilk.shimmer.ShimmerBounds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class RecipeCardVariant { List, Tile }

sealed interface RecipeCardState {
    data class Content(val data: RecipeCardContent) : RecipeCardState
    data object Loading : RecipeCardState
    data class Error(
        val message: String? = null,
        val onRetry: (() -> Unit)? = null,
    ) : RecipeCardState
}

@Immutable
data class RecipeCardContent(
    val title: String,
    val duration: Duration?,
    val cookbooks: List<String> = emptyList(),
    val onClick: () -> Unit,
    val image: RecipeCardImage? = null,
)

@Immutable
data class RecipeCardImage(
    val model: Any,
    val contentDescription: String? = null,
)

object RecipeCardDefaults {
    private const val TileImageAspectRatio = 4f / 3f

    object ListVariant {
        val ContentPadding: PaddingValues = PaddingValues(16.dp)
        val HorizontalSpacing = 16.dp
        val ImageSize = 88.dp
        val ImageShape = RoundedCornerShape(12.dp)
        val MinHeight = 104.dp
        val ContentVerticalSpacing = 8.dp
        val MetadataSpacing = 6.dp
    }

    object TileVariant {
        val ContentPadding: PaddingValues = PaddingValues(16.dp)
        val ContentVerticalSpacing = 12.dp
        val ImageShape = RoundedCornerShape(16.dp)
        const val ImageAspectRatio: Float = TileImageAspectRatio
    }

    val MetadataHorizontalSpacing = 8.dp
    val MetadataVerticalSpacing = 4.dp
    val DurationIconSize = 16.dp
    val DurationSpacing = 4.dp

    val CapsuleShape = RoundedCornerShape(percent = 50)
    val CapsulePadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp)

    val LoadingMinHeight = 120.dp
    val LoadingContentPadding: PaddingValues = PaddingValues(16.dp)
    val LoadingContentSpacing = 12.dp
    val LoadingIndicatorSize = 32.dp

    val ErrorMinHeight = 120.dp
    val ErrorContentPadding: PaddingValues = PaddingValues(16.dp)
    val ErrorContentSpacing = 12.dp

    @Composable
    fun cardShape() = MaterialTheme.shapes.medium

    @Composable
    fun cardColors() = CardDefaults.outlinedCardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun RecipeCard(
    variant: RecipeCardVariant,
    state: RecipeCardState,
    modifier: Modifier = Modifier,
    colors: CardColors = RecipeCardDefaults.cardColors(),
    shape: CornerBasedShape = RecipeCardDefaults.cardShape(),
) {
    when (state) {
        is RecipeCardState.Content -> RecipeCardContentLayout(
            variant = variant,
            content = state.data,
            colors = colors,
            modifier = modifier,
            shape = shape,
        )

        RecipeCardState.Loading -> RecipeCardLoading(
            modifier = modifier,
            shape = shape,
            color = colors,
        )

        is RecipeCardState.Error -> RecipeCardError(
            message = state.message,
            onRetry = state.onRetry,
            modifier = modifier,
            shape = shape,
        )
    }
}

@Composable
private fun RecipeCardContentLayout(
    variant: RecipeCardVariant,
    content: RecipeCardContent,
    colors: CardColors,
    shape: CornerBasedShape,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = content.onClick,
        modifier = modifier
            .semantics { role = Role.Button },
        shape = shape,
        colors = colors,
    ) {
        when (variant) {
            RecipeCardVariant.List -> RecipeCardListContent(content)
            RecipeCardVariant.Tile -> RecipeCardTileContent(content)
        }
    }
}

@Composable
private fun RecipeCardListContent(content: RecipeCardContent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = RecipeCardDefaults.ListVariant.MinHeight)
            .padding(RecipeCardDefaults.ListVariant.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(RecipeCardDefaults.ListVariant.HorizontalSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecipeCardImageContainer(
            image = content.image,
            modifier = Modifier
                .size(RecipeCardDefaults.ListVariant.ImageSize)
                .clip(RecipeCardDefaults.ListVariant.ImageShape),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.ListVariant.ContentVerticalSpacing),
        ) {
            Text(
                text = content.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            MetadataRow(
                variant = RecipeCardVariant.List,
                content = content,
            )
        }
    }
}

@Composable
private fun RecipeCardTileContent(content: RecipeCardContent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(RecipeCardDefaults.TileVariant.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.TileVariant.ContentVerticalSpacing),
    ) {
        RecipeCardImageContainer(
            image = content.image,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RecipeCardDefaults.TileVariant.ImageShape)
                .aspectRatio(RecipeCardDefaults.TileVariant.ImageAspectRatio),
        )

        Text(
            text = content.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        MetadataRow(
            variant = RecipeCardVariant.Tile,
            content = content,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetadataRow(
    variant: RecipeCardVariant,
    content: RecipeCardContent,
) {
    val cookbooks = content.cookbooks
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    if (content.duration == null && cookbooks.isEmpty()) {
        return
    }

    val visibleCookbooks = cookbooks.take(2)
    val remainingCount = (cookbooks.size - visibleCookbooks.size).coerceAtLeast(0)
    val moreCapsule = if (remainingCount > 0) {
        listOf(
            pluralStringResource(
                id = R.plurals.recipe_card_more_cookbooks,
                count = remainingCount,
                remainingCount,
            )
        )
    } else {
        emptyList()
    }

    val capsules = visibleCookbooks + moreCapsule

    when (variant) {
        RecipeCardVariant.List -> Column(
            verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.ListVariant.MetadataSpacing),
        ) {
            content.duration?.let { duration ->
                DurationRow(duration = duration)
            }
            if (capsules.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(RecipeCardDefaults.MetadataHorizontalSpacing),
                    verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.MetadataVerticalSpacing),
                ) {
                    capsules.forEach { text ->
                        CookbookCapsule(text = text)
                    }
                }
            }
        }

        RecipeCardVariant.Tile -> FlowRow(
            horizontalArrangement = Arrangement.spacedBy(RecipeCardDefaults.MetadataHorizontalSpacing),
            verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.MetadataVerticalSpacing),
        ) {
            content.duration?.let { duration ->
                DurationRow(
                    duration = duration,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            capsules.forEach { text ->
                CookbookCapsule(text = text)
            }
        }
    }
}

@Composable
private fun DurationRow(duration: Duration, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(RecipeCardDefaults.DurationSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = IconRegistry.Timer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(RecipeCardDefaults.DurationIconSize),
        )
        Text(
            text = duration.toReadableString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CookbookCapsule(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RecipeCardDefaults.CapsuleShape,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(RecipeCardDefaults.CapsulePadding),
        )
    }
}

@Composable
private fun RecipeCardImageContainer(
    image: RecipeCardImage?,
    modifier: Modifier = Modifier,
) {
    if (image != null) {
        SubcomposeAsyncImage(
            model = image.model,
            contentDescription = image.contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
            loading = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .shimmerLoading(
                            bounds = ShimmerBounds.Window
                        )
                        .clearAndSetSemantics {},
                )
            },
            error = {
                RecipeCardImageFallback(
                    modifier = Modifier.matchParentSize(),
                )
            },
        )
    } else {
        RecipeCardImageFallback(modifier)
    }
}

@Composable
private fun RecipeCardImageFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = IconRegistry.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RecipeCardLoading(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape,
    color: CardColors
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = RecipeCardDefaults.LoadingMinHeight)
            .shimmerLoading(bounds = ShimmerBounds.Window)
            .background(color.containerColor, shape)
            .clip(shape)
    )
}

@Composable
private fun RecipeCardError(
    message: String?,
    onRetry: (() -> Unit)?,
    shape: CornerBasedShape,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = RecipeCardDefaults.ErrorMinHeight),
        shape = shape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RecipeCardDefaults.ErrorContentPadding),
            verticalArrangement = Arrangement.spacedBy(RecipeCardDefaults.ErrorContentSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = IconRegistry.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message ?: stringResource(id = R.string.recipe_card_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(id = R.string.common_retry))
                }
            }
        }
    }
}

@Composable
private fun Duration.toReadableString(): String {
    val totalMinutes = inWholeMinutes
    val hours = totalMinutes / 60
    val minutes = (totalMinutes % 60).toInt()
    return when {
        hours > 0 && minutes > 0 -> stringResource(
            id = R.string.recipe_card_duration_hours_minutes,
            hours.toInt(),
            minutes,
        )

        hours > 0 -> stringResource(
            id = R.string.recipe_card_duration_hours,
            hours.toInt(),
        )

        else -> stringResource(
            id = R.string.recipe_card_duration_minutes,
            totalMinutes.toInt(),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(name = "FontScale 2x", fontScale = 2f, showBackground = true)
@Composable
private fun RecipeCardListPreview() {
    PreviewWrapper {
        RecipeCard(
            variant = RecipeCardVariant.List,
            state = RecipeCardState.Content(
                data = RecipeCardContent(
                    title = "Curry cremoso com grão-de-bico",
                    duration = 45.minutes,
                    cookbooks = listOf("Comfort Food", "Sem Lactose", "Receitas Rápidas", "Jantar"),
                    image = RecipeCardImage(
                        model = "https://sabores-new.s3.amazonaws.com/public/2024/11/curry-de-frango-com-grao-de-bico-1024x493.webp",
                        contentDescription = "Foto do curry",
                    ),
                    onClick = {},
                )
            ),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(name = "FontScale 2x", fontScale = 2f, showBackground = true)
@Composable
private fun RecipeCardTilePreview() {
    PreviewWrapper {
        RecipeCard(
            variant = RecipeCardVariant.Tile,
            state = RecipeCardState.Content(
                data = RecipeCardContent(
                    title = "Curry cremoso com grão-de-bico",
                    duration = 45.minutes,
                    cookbooks = listOf("Comfort Food", "Sem Lactose", "Receitas Rápidas", "Jantar"),
                    image = RecipeCardImage(
                        model = "https://sabores-new.s3.amazonaws.com/public/2024/11/curry-de-frango-com-grao-de-bico-1024x493.webp",
                        contentDescription = "Foto do curry",
                    ),
                    onClick = {},
                )
            ),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecipeCardLoadingPreview() {
    PreviewWrapper {
        RecipeCard(
            variant = RecipeCardVariant.List,
            state = RecipeCardState.Loading,
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecipeCardErrorPreview() {
    PreviewWrapper {
        RecipeCard(
            variant = RecipeCardVariant.Tile,
            state = RecipeCardState.Error(onRetry = {}),
        )
    }
}
