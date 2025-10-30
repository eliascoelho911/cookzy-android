package com.eliascoelho911.cookzy.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutMode
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATA_STORE_NAME = "home_layout_preferences"
private const val HOME_LAYOUT_MODE_KEY = "home_layout_mode"

private val Context.homeLayoutDataStore by preferencesDataStore(name = DATA_STORE_NAME)

class HomeLayoutPreferencesDataSource(
    private val context: Context
) : HomeLayoutPreferences {

    private val layoutModeKey = stringPreferencesKey(HOME_LAYOUT_MODE_KEY)

    override val layoutMode: Flow<HomeLayoutMode> =
        context.homeLayoutDataStore.data.map { preferences ->
            HomeLayoutMode.fromStorage(preferences[layoutModeKey])
        }

    override suspend fun setLayoutMode(mode: HomeLayoutMode) {
        context.homeLayoutDataStore.edit { preferences ->
            preferences[layoutModeKey] = mode.storageValue
        }
    }
}
