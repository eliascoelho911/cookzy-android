package com.eliascoelho911.cookzy.ds.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun Modifier.shimmerLoading(
    bounds: ShimmerBounds = ShimmerBounds.Window,
    theme: ShimmerTheme = LocalShimmerTheme.current
): Modifier {
    val shimmer: Shimmer = rememberShimmer(bounds, theme)
    return this.shimmer(shimmer)
}