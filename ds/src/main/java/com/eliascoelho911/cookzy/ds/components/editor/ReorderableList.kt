package com.eliascoelho911.cookzy.ds.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import kotlin.collections.buildList
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun <T> ReorderableList(
    items: List<T>,
    key: (T) -> Any,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(ReorderableListDefaults.ItemSpacing),
    itemContent: @Composable (index: Int, item: T) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        scrollThresholdPadding = contentPadding,
    ) { from, to ->
        val fromIndex = from.index
        val toIndex = to.index
        if (fromIndex != toIndex) {
            onMove(fromIndex, toIndex)
        }
    }

    val context = remember(onMove, items.size) {
        ReorderableListContext(onMove = onMove, itemCount = items.size)
    }

    CompositionLocalProvider(LocalReorderableListContext provides context) {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
        ) {
            itemsIndexed(items, key = { _, item -> key(item) }) { index, item ->
                val itemKey = key(item)
                ReorderableItem(
                    state = reorderableState,
                    key = itemKey,
                ) { isDragging ->
                    CompositionLocalProvider(
                        LocalReorderableItemIndex provides index,
                        LocalReorderableCollectionItemScope provides this,
                        LocalReorderableItemDragging provides isDragging,
                    ) {
                        itemContent(index, item)
                    }
                }
            }
        }
    }
}

@Composable
fun ReorderableListItem(
    dragHandle: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ReorderableListDefaults.ItemMinHeight),
        horizontalArrangement = Arrangement.spacedBy(ReorderableListDefaults.ItemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dragHandle()
        content()
    }
}

@Composable
fun ReorderableDragHandle(
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.editor_drag_handle_description),
    enabled: Boolean = true,
) {
    val isDragging = LocalReorderableItemDragging.current

    Box(
        modifier = modifier
            .run {
                if (enabled) reorderableHandle() else this
            }
            .padding(ReorderableListDefaults.HandlePadding),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = IconRegistry.DragHandle,
            contentDescription = contentDescription,
            tint = if (isDragging) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(ReorderableListDefaults.HandleIconSize),
        )
    }
}

fun Modifier.reorderableHandle(): Modifier = composed {
    val scope = LocalReorderableCollectionItemScope.current
    val context = LocalReorderableListContext.current
    val index = LocalReorderableItemIndex.current

    val moveUpLabel = stringResource(id = R.string.editor_reorder_move_up)
    val moveDownLabel = stringResource(id = R.string.editor_reorder_move_down)
    val positionText = stringResource(
        id = R.string.editor_drag_handle_state,
        index + 1,
        context.itemCount,
    )

    val customActions = remember(moveUpLabel, moveDownLabel, index, context.itemCount, context.onMove) {
        buildList {
            if (index > 0) {
                add(
                    CustomAccessibilityAction(label = moveUpLabel) {
                        context.onMove(index, index - 1)
                        true
                    },
                )
            }

            if (index < context.itemCount - 1) {
                add(
                    CustomAccessibilityAction(label = moveDownLabel) {
                        context.onMove(index, index + 1)
                        true
                    },
                )
            }
        }
    }

    with(scope) {
        this@composed.draggableHandle()
    }
        .semantics {
            role = Role.Button
            stateDescription = positionText
            if (customActions.isNotEmpty()) {
                this.customActions = customActions
            }
        }
        .sizeIn(
            minWidth = ReorderableListDefaults.TouchTarget,
            minHeight = ReorderableListDefaults.TouchTarget,
        )
}

@Immutable
private data class ReorderableListContext(
    val onMove: (Int, Int) -> Unit,
    val itemCount: Int,
)

private val LocalReorderableListContext = compositionLocalOf<ReorderableListContext> {
    error("ReorderableListItem must be used inside ReorderableList")
}

private val LocalReorderableCollectionItemScope = compositionLocalOf<ReorderableCollectionItemScope> {
    error("ReorderableListItem must be used inside ReorderableList")
}

private val LocalReorderableItemIndex = compositionLocalOf<Int> {
    error("ReorderableListItem must be used inside ReorderableList")
}

private val LocalReorderableItemDragging = compositionLocalOf { false }

object ReorderableListDefaults {
    val ItemSpacing = 12.dp
    val ItemMinHeight = 64.dp
    val TouchTarget = 48.dp
    val HandlePadding = PaddingValues(8.dp)
    val HandleIconSize = 24.dp
}

@ThemePreviews
@Preview(name = "FontScale 2.0", fontScale = 2f, showBackground = true)
@Composable
private fun ReorderableListPreview() {
    PreviewWrapper {
        Surface {
            val items = remember {
                mutableStateListOf("Farinha", "Ovos", "Leite")
            }
            ReorderableList(
                items = items,
                key = { it },
                onMove = { from, to ->
                    if (from == to) return@ReorderableList
                    val item = items.removeAt(from)
                    items.add(to, item)
                },
                itemContent = { index, item ->
                    ReorderableListItem(
                        dragHandle = { ReorderableDragHandle() },
                        content = {
                            Text(
                                text = "${index + 1}. $item",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        },
                    )
                },
            )
        }
    }
}
