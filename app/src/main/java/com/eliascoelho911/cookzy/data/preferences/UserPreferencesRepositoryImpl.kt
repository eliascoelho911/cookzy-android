package com.eliascoelho911.cookzy.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eliascoelho911.cookzy.domain.model.RecipeListLayout
import com.eliascoelho911.cookzy.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    private object Keys {
        val RECIPE_LIST_LAYOUT = stringPreferencesKey("recipe_list_layout")
    }

    override fun observeRecipeListLayout(): Flow<RecipeListLayout> =
        context.userPrefsDataStore.data.map { prefs: Preferences ->
            when (prefs[Keys.RECIPE_LIST_LAYOUT]) {
                RecipeListLayout.GRID.name -> RecipeListLayout.GRID
                else -> RecipeListLayout.LIST
            }
        }

    override suspend fun setRecipeListLayout(layout: RecipeListLayout) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.RECIPE_LIST_LAYOUT] = layout.name
        }
    }
}

