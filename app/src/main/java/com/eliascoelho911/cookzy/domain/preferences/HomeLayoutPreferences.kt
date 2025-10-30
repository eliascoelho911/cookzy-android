package com.eliascoelho911.cookzy.domain.preferences

import kotlinx.coroutines.flow.Flow

interface HomeLayoutPreferences {
    val layoutMode: Flow<HomeLayoutMode>
    suspend fun setLayoutMode(mode: HomeLayoutMode)
}
