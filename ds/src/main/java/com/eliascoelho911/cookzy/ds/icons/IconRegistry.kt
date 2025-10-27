package com.eliascoelho911.cookzy.ds.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Central icon registry. Every UI icon must be consumed from here.
 * No domain mappings (e.g., WorkItemType → icon). Expose one property per icon.
 */
object IconRegistry {
    // Navigation & Actions
    val Back: ImageVector = Icons.Outlined.ArrowBack
    val Close: ImageVector = Icons.Outlined.Close
    val Add: ImageVector = Icons.Outlined.Add
    val Edit: ImageVector = Icons.Outlined.Edit
    val Delete: ImageVector = Icons.Outlined.Delete
    val Clear: ImageVector = Icons.Outlined.Clear
    val Copy: ImageVector = Icons.Outlined.ContentCopy

    // Status & Feedback
    val Check: ImageVector = Icons.Outlined.CheckCircle
    val Info: ImageVector = Icons.Outlined.Info
    val Error: ImageVector = Icons.Outlined.Error

    // Media / Timers
    val Play: ImageVector = Icons.Outlined.PlayArrow
    val Pause: ImageVector = Icons.Outlined.Pause
    val Stop: ImageVector = Icons.Outlined.Stop
    val Timer: ImageVector = Icons.Outlined.Timer

    // Discovery & Organization
    val Search: ImageVector = Icons.Outlined.Search
    val Sort: ImageVector = Icons.Outlined.Sort
    val Filter: ImageVector = Icons.Outlined.FilterList
    val Share: ImageVector = Icons.Outlined.Share

    // Disclosure
    val ExpandMore: ImageVector = Icons.Outlined.ExpandMore
    val ExpandLess: ImageVector = Icons.Outlined.ExpandLess
}
