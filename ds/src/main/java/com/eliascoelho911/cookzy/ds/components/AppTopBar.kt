package com.eliascoelho911.cookzy.ds.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.icons.IconRegistry
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import com.eliascoelho911.cookzy.ds.theme.provider

@Immutable
data class AppTopBarSearchUi(
    val active: Boolean,
    val query: String,
    val placeholder: String,
    val onActivate: () -> Unit,
    val onDeactivate: () -> Unit,
    val onQueryChange: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onClear: () -> Unit,
)

private val appTopBarTitleFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Bricolage Grotesque"),
        fontProvider = provider,
        weight = FontWeight.Bold,
    ),
)

object AppTopBarDefaults {
    val SearchFieldPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        actionIconContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.surface,
    ): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        titleContentColor = titleContentColor,
        navigationIconContentColor = navigationIconContentColor,
        actionIconContentColor = actionIconContentColor,
        scrolledContainerColor = scrolledContainerColor,
    )

    @Composable
    fun searchShape(): Shape = MaterialTheme.shapes.medium

    @Composable
    fun searchColors(
        focusedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        errorContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        focusedIndicatorColor: Color = Color.Transparent,
        unfocusedIndicatorColor: Color = Color.Transparent,
        disabledIndicatorColor: Color = Color.Transparent,
        errorIndicatorColor: Color = Color.Transparent,
    ): TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor = errorContainerColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
    )
}

/**
 * Cookzy top app bar with optional inline search experience.
 *
 * When `searchUi.active` is true, the title slot is swapped by a search field,
 * the navigation icon becomes a Back action, and the trailing clear button
 * clears the query. Consumers remain responsible for debouncing and state hoisting.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    searchUi: AppTopBarSearchUi? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = AppTopBarDefaults.colors(),
    searchShape: Shape = AppTopBarDefaults.searchShape(),
    searchFieldPadding: PaddingValues = AppTopBarDefaults.SearchFieldPadding,
    searchTextFieldColors: TextFieldColors = AppTopBarDefaults.searchColors(),
) {
    val currentSearch = searchUi
    val searchActive = currentSearch?.active == true
    val topAppBarColors = colors

    AnimatedContent(
        targetState = searchActive,
        transitionSpec = {
            val slideAnimationSpec = tween<IntOffset>(durationMillis = 300)
            val fadeAnimationSpec = tween<Float>(durationMillis = 300)
            val enterTransition = slideInVertically(
                animationSpec = slideAnimationSpec,
                initialOffsetY = { -it },
            ) + fadeIn(fadeAnimationSpec)
            val exitTransition = slideOutVertically(
                animationSpec = slideAnimationSpec,
                targetOffsetY = { it },
            ) + fadeOut(fadeAnimationSpec)
            (enterTransition togetherWith exitTransition).using(SizeTransform(clip = true))
        },
        label = "Search",
        modifier = modifier.background(topAppBarColors.containerColor),
    ) { target ->
        if (target && currentSearch != null) {
            InlineSearchField(
                searchUi = currentSearch,
                backgroundColor = topAppBarColors.containerColor,
                shape = searchShape,
                contentPadding = searchFieldPadding,
                colors = searchTextFieldColors,
            )
        } else {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = { navigationIcon?.invoke() },
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = appTopBarTitleFontFamily,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                },
                actions = {
                    currentSearch?.let { search ->
                        IconButton(
                            onClick = search.onActivate,
                            modifier = Modifier.semantics { role = Role.Button },
                        ) {
                            Icon(
                                imageVector = IconRegistry.Search,
                                contentDescription = stringResource(id = R.string.app_topbar_search_activate),
                            )
                        }
                    }
                    actions()
                },
                colors = topAppBarColors,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InlineSearchField(
    searchUi: AppTopBarSearchUi,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shape: Shape,
    contentPadding: PaddingValues,
    colors: TextFieldColors,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(searchUi.active) {
        if (searchUi.active) {
            focusRequester.requestFocus()
        }
    }

    TextField(
        value = searchUi.query,
        onValueChange = searchUi.onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(contentPadding)
            .focusRequester(focusRequester),
        placeholder = { Text(text = searchUi.placeholder) },
        singleLine = true,
        leadingIcon = {
            if (searchUi.query.isNotEmpty()) {
                IconButton(
                    onClick = searchUi.onDeactivate
                ) {
                    Icon(
                        imageVector = IconRegistry.Back,
                        contentDescription = stringResource(
                            id = R.string.app_topbar_search_back
                        ),
                    )
                }
            } else {
                Icon(
                    imageVector = IconRegistry.Search,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = {
            if (searchUi.query.isNotEmpty()) {
                IconButton(onClick = searchUi.onClear) {
                    Icon(
                        imageVector = IconRegistry.Clear,
                        contentDescription = stringResource(
                            id = R.string.app_topbar_search_clear
                        ),
                    )
                }
            } else {
                IconButton(onClick = searchUi.onDeactivate) {
                    Icon(
                        imageVector = IconRegistry.Close,
                        contentDescription = stringResource(
                            id = R.string.app_topbar_search_back
                        ),
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { searchUi.onSubmit() }),
        shape = shape,
        colors = colors,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Preview(showBackground = true)
@Preview(
    name = "FontScale 2.0",
    showBackground = true,
    fontScale = 2f,
)
@Composable
private fun AppTopBarPreview() {
    PreviewWrapper {
        var searchActive by rememberSaveable { mutableStateOf(false) }
        var query by rememberSaveable { mutableStateOf("") }
        AppTopBar(
            title = "Cookzy",
            searchUi = AppTopBarSearchUi(
                active = searchActive,
                query = query,
                placeholder = "Buscar receitas",
                onActivate = { searchActive = true },
                onDeactivate = {
                    searchActive = false
                    query = ""
                },
                onQueryChange = { query = it },
                onSubmit = {},
                onClear = { query = "" },
            ),
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = IconRegistry.Sort,
                        contentDescription = "Ordenar receitas",
                    )
                }
            },
        )
    }
}
