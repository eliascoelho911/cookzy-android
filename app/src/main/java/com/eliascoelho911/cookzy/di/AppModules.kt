package com.eliascoelho911.cookzy.di

import androidx.lifecycle.SavedStateHandle
import com.eliascoelho911.cookzy.data.local.database.CookzyDatabase
import com.eliascoelho911.cookzy.data.repository.OfflineRecipeRepository
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorViewModel
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailViewModel
import com.eliascoelho911.cookzy.feature.recipelist.RecipeListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

private val databaseModule = module {
    single { CookzyDatabase.create(androidContext()) }
    single { get<CookzyDatabase>().recipeDao() }
}

private val repositoryModule = module {
    single { OfflineRecipeRepository(get()) } bind RecipeRepository::class
}

private val viewModelModule = module {
    viewModel { RecipeListViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) -> RecipeEditorViewModel(savedStateHandle, get()) }
    viewModel { (savedStateHandle: SavedStateHandle) -> RecipeDetailViewModel(savedStateHandle, get()) }
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)
