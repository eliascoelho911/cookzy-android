package com.eliascoelho911.cookzy.ds.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import kotlin.math.roundToInt

@Immutable
data class IconToggleOption(
    val icon: ImageVector,
    val contentDescription: String,
)

@Composable
fun IconToggle(
    options: List<IconToggleOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: IconToggleColors = IconToggleDefaults.colors(),
    indicatorAnimationSpec: AnimationSpec<IntOffset> = IconToggleDefaults.indicatorAnimationSpec(),
    tintAnimationSpec: AnimationSpec<Color> = IconToggleDefaults.tintAnimationSpec(),
) {
    require(options.size == 2) { "IconToggle requires exactly 2 options" }

    val segmentLayouts = remember { mutableStateMapOf<Int, IconToggleSegmentLayout>() }
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .clip(IconToggleDefaults.ContainerShape)
            .background(colors.containerColor())
            .padding(IconToggleDefaults.ContainerPadding),
    ) {
        val selectedLayout = segmentLayouts[selectedIndex]
        if (selectedLayout != null) {
            val animatedOffset by animateIntOffsetAsState(
                targetValue = selectedLayout.position,
                animationSpec = indicatorAnimationSpec,
                label = "IconToggleIndicatorOffset",
            )
            Box(
                modifier = Modifier
                    .offset { animatedOffset }
                    .size(
                        width = with(density) { selectedLayout.size.width.toDp() },
                        height = with(density) { selectedLayout.size.height.toDp() },
                    )
                    .background(colors.indicatorColor(), IconToggleDefaults.IndicatorShape),
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IconToggleDefaults.SegmentSpacing),
        ) {
            options.forEachIndexed { index, option ->
                IconToggleSegment(
                    option = option,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    onPositioned = { layout ->
                        val current = segmentLayouts[index]
                        if (current != layout) {
                            segmentLayouts[index] = layout
                        }
                    },
                    colors = colors,
                    tintAnimationSpec = tintAnimationSpec,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun RowScope.IconToggleSegment(
    option: IconToggleOption,
    selected: Boolean,
    onClick: () -> Unit,
    onPositioned: (IconToggleSegmentLayout) -> Unit,
    colors: IconToggleColors,
    tintAnimationSpec: AnimationSpec<Color>,
    modifier: Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val stateText = if (selected) {
        stringResource(id = R.string.icon_toggle_selected)
    } else {
        stringResource(id = R.string.icon_toggle_not_selected)
    }
    val animatedTint by animateColorAsState(
        targetValue = colors.iconTint(selected),
        animationSpec = tintAnimationSpec,
        label = "IconToggleTint",
    )

    Icon(
        modifier = modifier
            .size(IconToggleDefaults.IconSize)
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInParent()
                onPositioned(
                    IconToggleSegmentLayout(
                        size = coordinates.size,
                        position = IntOffset(
                            x = position.x.roundToInt(),
                            y = position.y.roundToInt(),
                        ),
                    ),
                )
            }
            .padding(IconToggleDefaults.IconPadding)
            .clickable(
                enabled = !selected,
                role = Role.Tab,
                interactionSource = interactionSource,
                onClick = onClick,
                indication = null
            )
            .semantics {
                role = Role.Tab
                stateDescription = stateText
            },
        imageVector = option.icon,
        contentDescription = option.contentDescription,
        tint = animatedTint,
    )
}

private data class IconToggleSegmentLayout(
    val size: IntSize,
    val position: IntOffset,
)

object IconToggleDefaults {
    val ContainerPadding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    val SegmentSpacing = 8.dp
    val IconSize = 36.dp
    val IconPadding = 8.dp
    val ContainerShape = RoundedCornerShape(percent = 50)
    val IndicatorShape = CircleShape

    fun indicatorAnimationSpec(): AnimationSpec<IntOffset> =
        spring(stiffness = Spring.StiffnessMediumLow)

    fun tintAnimationSpec(): AnimationSpec<Color> =
        spring(stiffness = Spring.StiffnessMediumLow)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        indicatorColor: Color = MaterialTheme.colorScheme.primary,
        selectedIconTint: Color = MaterialTheme.colorScheme.onPrimary,
        unselectedIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): IconToggleColors = remember(containerColor, indicatorColor, selectedIconTint, unselectedIconTint) {
        IconToggleColors(
            containerColorValue = containerColor,
            indicatorColorValue = indicatorColor,
            selectedIconTintValue = selectedIconTint,
            unselectedIconTintValue = unselectedIconTint,
        )
    }
}

@Stable
class IconToggleColors internal constructor(
    private val containerColorValue: Color,
    private val indicatorColorValue: Color,
    private val selectedIconTintValue: Color,
    private val unselectedIconTintValue: Color,
) {
    @Composable
    internal fun containerColor(): Color = containerColorValue

    @Composable
    internal fun indicatorColor(): Color = indicatorColorValue

    @Composable
    internal fun iconTint(selected: Boolean): Color =
        if (selected) selectedIconTintValue else unselectedIconTintValue
}

@ThemePreviews
@Preview(showBackground = true)
@Preview(name = "FontScale 2x", fontScale = 2f, showBackground = true)
@Composable
private fun IconTogglePreview() {
    PreviewWrapper {
        var selected by remember { mutableStateOf(0) }
        IconToggle(
            options = listOf(
                IconToggleOption(
                    icon = IconRegistry.ViewList,
                    contentDescription = "Visualizar em lista",
                ),
                IconToggleOption(
                    icon = IconRegistry.ViewTile,
                    contentDescription = "Visualizar em grade",
                ),
            ),
            selectedIndex = selected,
            onSelect = { selected = it },
        )
    }
}
