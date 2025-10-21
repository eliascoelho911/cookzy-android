package com.eliascoelho911.cookzy.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorArgs
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorRoute
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailArgs
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailRoute
import com.eliascoelho911.cookzy.feature.recipedetail.RecipeDetailViewModel
import com.eliascoelho911.cookzy.feature.recipelist.RecipeListArgs
import com.eliascoelho911.cookzy.feature.recipelist.RecipeListRoute
import com.eliascoelho911.cookzy.feature.recipeeditor.RecipeEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CookzyNavHost(
    finishApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RecipeListArgs.ROUTE,
        modifier = modifier
    ) {
        composable(route = RecipeListArgs.ROUTE) {
            RecipeListRoute(
                onCreateRecipe = { navController.navigate(RecipeEditorArgs.ROUTE) },
                onRecipeSelected = { recipeId ->
                    navController.navigate("${RecipeDetailArgs.ROUTE}/$recipeId")
                }
            )
        }

        composable(route = RecipeEditorArgs.ROUTE) { backStackEntry ->
            val viewModel = koinViewModel<RecipeEditorViewModel>()
            RecipeEditorRoute(
                viewModel = viewModel,
                onNavigateToDetail = { recipeId ->
                    navController.navigate("${RecipeDetailArgs.ROUTE}/$recipeId") {
                        popUpTo(RecipeListArgs.ROUTE) {
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

        composable(
            route = "${RecipeEditorArgs.ROUTE}/{${RecipeEditorArgs.RECIPE_ID}}",
            arguments = listOf(
                navArgument(RecipeEditorArgs.RECIPE_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val viewModel = koinViewModel<RecipeEditorViewModel>()
            RecipeEditorRoute(
                viewModel = viewModel,
                onNavigateToDetail = { recipeId ->
                    navController.navigate("${RecipeDetailArgs.ROUTE}/$recipeId") {
                        popUpTo(RecipeListArgs.ROUTE) {
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

        composable(
            route = "${RecipeDetailArgs.ROUTE}/{${RecipeDetailArgs.RECIPE_ID}}",
            arguments = listOf(
                navArgument(RecipeDetailArgs.RECIPE_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val viewModel = koinViewModel<RecipeDetailViewModel>()
            RecipeDetailRoute(
                viewModel = viewModel,
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        finishApp()
                    }
                },
                onEditRecipe = { recipeId ->
                    navController.navigate("${RecipeEditorArgs.ROUTE}/$recipeId")
                }
            )
        }
    }
}
