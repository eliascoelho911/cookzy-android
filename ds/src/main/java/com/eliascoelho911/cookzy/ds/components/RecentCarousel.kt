package com.eliascoelho911.cookzy.ds.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eliascoelho911.cookzy.ds.R
import com.eliascoelho911.cookzy.ds.preview.PreviewWrapper
import com.eliascoelho911.cookzy.ds.preview.ThemePreviews
import kotlin.time.Duration.Companion.minutes

object RecentCarouselDefaults {
    val PageSpacing: Dp = 16.dp
    val ContentPadding: PaddingValues = PaddingValues(horizontal = 32.dp)
    val PageSize: PageSize = androidx.compose.foundation.pager.PageSize.Fill

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    fun flingBehavior(state: PagerState): FlingBehavior = PagerDefaults.flingBehavior(state = state)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun rememberPagerState(itemCount: Int): PagerState =
        rememberPagerState(pageCount = { itemCount })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentCarousel(
    items: List<RecipeCardContent>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = RecentCarouselDefaults.rememberPagerState(itemCount = items.size),
    pageSpacing: Dp = RecentCarouselDefaults.PageSpacing,
    contentPadding: PaddingValues = RecentCarouselDefaults.ContentPadding,
    pageSize: PageSize = RecentCarouselDefaults.PageSize,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = pagerState),
) {
    if (items.isEmpty()) {
        return
    }

    val carouselHint = stringResource(id = R.string.recent_carousel_hint)

    HorizontalPager(
        state = pagerState,
        pageSize = pageSize,
        pageSpacing = pageSpacing,
        flingBehavior = flingBehavior,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = carouselHint
            },
    ) { page ->
        RecipeCard(
            variant = RecipeCardVariant.Tile,
            state = RecipeCardState.Content(items[page]),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ThemePreviews
@Preview(showBackground = true)
@Composable
private fun RecentCarouselPreview() {
    PreviewWrapper {
        val recipes = listOf(
            RecipeCardContent(
                title = "Bolo de cenoura com cobertura",
                duration = 50.minutes,
                cookbooks = listOf("Família", "Aniversário", "Café da tarde"),
                onClick = {},
            ),
            RecipeCardContent(
                title = "Risoto de cogumelos trufado",
                duration = 35.minutes,
                cookbooks = listOf("Gourmet"),
                onClick = {},
            ),
            RecipeCardContent(
                title = "Salada mediterrânea",
                duration = 15.minutes,
                cookbooks = listOf("Verão", "Low Carb"),
                onClick = {},
            ),
        )
        RecentCarousel(items = recipes)
    }
}
