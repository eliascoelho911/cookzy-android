package com.eliascoelho911.cookzy.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorRoute
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorViewModel
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailRoute
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailViewModel
import com.eliascoelho911.cookzy.feature.recipelist.RecipeListRoute
import com.eliascoelho911.cookzy.navigation.RecipeDetailDeepLinks
import com.eliascoelho911.cookzy.navigation.RecipeDetailDestination
import com.eliascoelho911.cookzy.navigation.RecipeEditorDeepLinks
import com.eliascoelho911.cookzy.navigation.RecipeEditorDestination
import com.eliascoelho911.cookzy.navigation.RecipeListDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CookzyNavHost(
    finishApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RecipeListDestination,
        modifier = modifier
    ) {
        composable<RecipeListDestination> {
            RecipeListRoute(
                onCreateRecipe = { navController.navigate(RecipeEditorDestination()) },
                onRecipeSelected = { recipeId ->
                    navController.navigate(RecipeDetailDestination(recipeId))
                }
            )
        }

        composable<RecipeEditorDestination>(
            deepLinks = RecipeEditorDeepLinks
        ) { backStackEntry ->
            val destination = backStackEntry.toRoute<RecipeEditorDestination>()
            val viewModel = koinViewModel<RecipeEditorViewModel>(
                parameters = { parametersOf(destination.recipeId) }
            )

            RecipeEditorRoute(
                viewModel = viewModel,
                onNavigateToDetail = { recipeId ->
                    navController.navigate(RecipeDetailDestination(recipeId)) {
                        popUpTo(route = RecipeListDestination) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onClose = {
                    if (!navController.popBackStack()) {
                        finishApp()
                    }
                }
            )
        }

        composable<RecipeDetailDestination>(
            deepLinks = RecipeDetailDeepLinks
        ) { backStackEntry ->
            val destination = backStackEntry.toRoute<RecipeDetailDestination>()
            val viewModel = koinViewModel<RecipeDetailViewModel>(
                parameters = { parametersOf(destination.recipeId) }
            )

            RecipeDetailRoute(
                viewModel = viewModel,
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        finishApp()
                    }
                },
                onEditRecipe = { recipeId ->
                    navController.navigate(RecipeEditorDestination(recipeId))
                }
            )
        }
    }
}
