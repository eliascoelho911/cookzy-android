package com.eliascoelho911.cookzy.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

