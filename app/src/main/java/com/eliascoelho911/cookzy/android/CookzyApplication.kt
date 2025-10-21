package com.eliascoelho911.cookzy.android

import android.app.Application
import com.eliascoelho911.cookzy.data.local.database.CookzyDatabase
import com.eliascoelho911.cookzy.data.repository.OfflineRecipeRepository
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository

class CookzyApplication : Application() {

    val database: CookzyDatabase by lazy {
        CookzyDatabase.create(this)
    }

    val recipeRepository: RecipeRepository by lazy {
        OfflineRecipeRepository(database.recipeDao())
    }
}
