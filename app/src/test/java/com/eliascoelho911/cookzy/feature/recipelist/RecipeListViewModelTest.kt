package com.eliascoelho911.cookzy.feature.recipelist

import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutMode
import com.eliascoelho911.cookzy.domain.preferences.HomeLayoutPreferences
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.testing.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: RecipeRepository
    private lateinit var layoutPreferences: FakeLayoutPreferences
    private lateinit var recipesFlow: MutableSharedFlow<List<Recipe>>
    private lateinit var recentFlow: MutableSharedFlow<List<Recipe>>

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        layoutPreferences = FakeLayoutPreferences()
        recipesFlow = MutableSharedFlow()
        recentFlow = MutableSharedFlow()

        every { repository.observeRecipes() } returns recipesFlow
        every { repository.observeRecentRecipes(any()) } returns recentFlow
    }

    @Test
    fun initialEmission_updatesContentState() = runTest {
        val viewModel = createViewModel()

        val recipes = listOf(
            recipe(id = 1L, title = "Bolo de Fubá"),
            recipe(id = 2L, title = "Lasanha de Legumes")
        )
        recipesFlow.emit(recipes)
        recentFlow.emit(recipes.take(1))

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.filteredRecipes.size)
        assertEquals(RecipeFeedStatus.Content, state.listStatus)
        assertEquals(RecipeSummaryUi(id = 1L, title = "Bolo de Fubá", duration = null, cookbooks = emptyList()), state.filteredRecipes.first())
        assertEquals(RecentRecipesStatus.Content, state.recentStatus)
    }

    @Test
    fun searchQuery_filtersAfterDebounce() = runTest {
        val viewModel = createViewModel()
        recipesFlow.emit(
            listOf(
                recipe(id = 1L, title = "Pão de Queijo"),
                recipe(id = 2L, title = "Risoto de Cogumelos")
            )
        )
        recentFlow.emit(emptyList())

        viewModel.onSearchQueryChange("risoto")
        assertTrue(viewModel.state.value.isSearchDebouncing)

        advanceTimeBy(500)

        val state = viewModel.state.value
        assertFalse(state.isSearchDebouncing)
        assertEquals(1, state.filteredRecipes.size)
        assertEquals("Risoto de Cogumelos", state.filteredRecipes.first().title)
    }

    @Test
    fun layoutModeSelection_persistsPreference() = runTest {
        val viewModel = createViewModel()
        recipesFlow.emit(emptyList())
        recentFlow.emit(emptyList())

        viewModel.onLayoutModeSelected(HomeLayoutMode.Grid)

        assertEquals(HomeLayoutMode.Grid, layoutPreferences.current.value)
        assertEquals(HomeLayoutMode.Grid, viewModel.state.value.layoutMode)
    }

    private fun createViewModel(): RecipeListViewModel =
        RecipeListViewModel(
            recipeRepository = repository,
            layoutPreferences = layoutPreferences
        )

    private fun recipe(
        id: Long,
        title: String
    ): Recipe = Recipe(
        id = id,
        title = title,
        ingredients = listOf(
            RecipeIngredient(
                rawText = "1 ingrediente",
                position = 0
            )
        ),
        steps = listOf(
            RecipeStep(
                description = "Passo 1",
                position = 0
            )
        ),
        updatedAt = 0L
    )

    private class FakeLayoutPreferences(
        initialMode: HomeLayoutMode = HomeLayoutMode.List
    ) : HomeLayoutPreferences {
        val current = MutableStateFlow(initialMode)

        override val layoutMode = current

        override suspend fun setLayoutMode(mode: HomeLayoutMode) {
            current.value = mode
        }
    }
}
