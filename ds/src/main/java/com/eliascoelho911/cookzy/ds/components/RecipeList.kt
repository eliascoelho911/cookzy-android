package com.eliascoelho911.cookzy.ds.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import kotlin.time.Duration.Companion.minutes

object RecipeListDefaults {
    val ContentPadding: PaddingValues = PaddingValues(vertical = 16.dp)
    val ItemSpacing: Dp = 12.dp
    val ItemHorizontalPadding: Dp = 16.dp
    const val LoadingItemsCount: Int = 6

    val ItemArrangement: Arrangement.Vertical = Arrangement.spacedBy(ItemSpacing)

    @Composable
    fun rememberListState(): LazyListState = rememberLazyListState()
}

@Immutable
data class RecipeListItem(
    val key: Any,
    val state: RecipeCardState,
)

sealed interface RecipeListState {
    data class Content(val items: List<RecipeListItem>) : RecipeListState

    data class Loading(val placeholderCount: Int = RecipeListDefaults.LoadingItemsCount) : RecipeListState

    data class Empty(
        val copy: EmptyStateCopy? = null,
        val illustration: (@Composable (() -> Unit))? = { EmptyStateDefaults.Illustration() },
        val primaryAction: StateAction? = null,
        val secondaryAction: StateAction? = null,
    ) : RecipeListState

    data class Error(
        val copy: ErrorStateCopy? = null,
        val illustration: (@Composable (() -> Unit))? = { ErrorStateDefaults.Illustration() },
        val primaryAction: StateAction? = null,
        val secondaryAction: StateAction? = null,
    ) : RecipeListState
}

@Composable
fun RecipeList(
    state: RecipeListState,
    modifier: Modifier = Modifier,
    listState: LazyListState = RecipeListDefaults.rememberListState(),
    contentPadding: PaddingValues = RecipeListDefaults.ContentPadding,
    verticalArrangement: Arrangement.Vertical = RecipeListDefaults.ItemArrangement,
) {
    when (state) {
        is RecipeListState.Content -> RecipeListContent(
            items = state.items,
            modifier = modifier,
            listState = listState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
        )

        is RecipeListState.Loading -> RecipeListLoading(
            placeholderCount = state.placeholderCount,
            modifier = modifier,
            listState = listState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
        )

        is RecipeListState.Empty -> RecipeListEmpty(
            state = state,
            modifier = modifier,
            contentPadding = contentPadding,
        )

        is RecipeListState.Error -> RecipeListError(
            state = state,
            modifier = modifier,
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun RecipeListContent(
    items: List<RecipeListItem>,
    modifier: Modifier,
    listState: LazyListState,
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical,
) {
    if (items.isEmpty()) {
        RecipeListEmpty(
            state = RecipeListState.Empty(),
            modifier = modifier,
            contentPadding = contentPadding,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
    ) {
        items(
            items = items,
            key = { it.key },
        ) { item ->
            RecipeCard(
                variant = RecipeCardVariant.List,
                state = item.state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = RecipeListDefaults.ItemHorizontalPadding),
            )
        }
    }
}

@Composable
private fun RecipeListLoading(
    placeholderCount: Int,
    modifier: Modifier,
    listState: LazyListState,
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
    ) {
        items(placeholderCount, key = { it }) {
            RecipeCard(
                variant = RecipeCardVariant.List,
                state = RecipeCardState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = RecipeListDefaults.ItemHorizontalPadding),
            )
        }
    }
}

@Composable
private fun RecipeListEmpty(
    state: RecipeListState.Empty,
    modifier: Modifier,
    contentPadding: PaddingValues,
) {
    RecipeListInformationalStateContainer(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        EmptyState(
            copy = state.copy ?: EmptyStateDefaults.default(),
            illustration = state.illustration,
            primaryAction = state.primaryAction,
            secondaryAction = state.secondaryAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RecipeListError(
    state: RecipeListState.Error,
    modifier: Modifier,
    contentPadding: PaddingValues,
) {
    RecipeListInformationalStateContainer(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        ErrorState(
            copy = state.copy ?: ErrorStateDefaults.default(),
            illustration = state.illustration,
            primaryAction = state.primaryAction,
            secondaryAction = state.secondaryAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RecipeListInformationalStateContainer(
    modifier: Modifier,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(fontScale = 2f, name = "FontScale 2x", showBackground = true)
@Composable
private fun RecipeListPopulatedPreview() {
    PreviewWrapper {
        RecipeList(
            state = RecipeListState.Content(sampleRecipeListItems()),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecipeListLoadingPreview() {
    PreviewWrapper {
        RecipeList(
            state = RecipeListState.Loading(),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecipeListEmptyPreview() {
    PreviewWrapper {
        RecipeList(
            state = RecipeListState.Empty(
                copy = EmptyStateCopy(
                    title = "Nenhuma receita encontrada",
                    message = "Tente ajustar os filtros ou pesquisar novamente.",
                ),
                primaryAction = StateAction(
                    label = "Nova receita",
                    onClick = {},
                ),
            ),
        )
    }
}

@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecipeListErrorPreview() {
    PreviewWrapper {
        RecipeList(
            state = RecipeListState.Error(
                copy = ErrorStateCopy(
                    title = "Falha ao carregar receitas",
                    message = "Verifique sua conexão e tente novamente.",
                ),
                primaryAction = StateAction(
                    label = "Tentar novamente",
                    onClick = {},
                ),
            ),
        )
    }
}

@Composable
private fun sampleRecipeListItems(): List<RecipeListItem> = buildList {
    repeat(5) { index ->
        add(
            RecipeListItem(
                key = index,
                state = RecipeCardState.Content(
                    RecipeCardContent(
                        title = "Receita saborosa #$index",
                        duration = (index + 1).times(10).minutes,
                        cookbooks = listOf("Família", "Favoritas"),
                        onClick = {},
                    ),
                ),
            ),
        )
    }
}
