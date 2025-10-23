package com.eliascoelho911.cookzy.feature.recipelist

import com.eliascoelho911.cookzy.domain.model.Recipe
import com.eliascoelho911.cookzy.domain.model.RecipeListLayout
import com.eliascoelho911.cookzy.domain.model.RecipeStep
import com.eliascoelho911.cookzy.domain.model.RecipeIngredient
import com.eliascoelho911.cookzy.domain.repository.RecipeRepository
import com.eliascoelho911.cookzy.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.AdvanceTimeBy
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers

private class FakeRecipeRepository(initial: List<Recipe>) : RecipeRepository {
    private val flow = MutableStateFlow(initial)
    override suspend fun createRecipe(draft: com.eliascoelho911.cookzy.domain.model.RecipeDraft): Long = 0
    override suspend fun updateRecipe(id: Long, draft: com.eliascoelho911.cookzy.domain.model.RecipeDraft) = Unit
    override suspend fun getRecipe(id: Long): Recipe? = null
    override fun observeRecipes(): Flow<List<Recipe>> = flow.asStateFlow()
}

private class FakeUserPrefs(layout: RecipeListLayout) : UserPreferencesRepository {
    private val flow = MutableStateFlow(layout)
    override fun observeRecipeListLayout(): Flow<RecipeListLayout> = flow.asStateFlow()
    override suspend fun setRecipeListLayout(layout: RecipeListLayout) { flow.value = layout }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchDebounceAndFilterWorks() = runTest(dispatcher) {
        val repo = FakeRecipeRepository(
            listOf(
                Recipe(1, "Pão Caseiro", emptyList(), emptyList()),
                Recipe(2, "Bolo de Fubá", emptyList(), emptyList()),
                Recipe(3, "Lasanha", emptyList(), emptyList())
            )
        )
        val prefs = FakeUserPrefs(RecipeListLayout.LIST)
        val vm = RecipeListViewModel(repo, prefs)

        // Allow initial combine to run
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, vm.state.value.totalCount)
        vm.onSearchQueryChanged("bo")

        // Before debounce completes, count should still be previous
        assertEquals(3, vm.state.value.totalCount)

        // Advance time beyond debounce (400ms)
        dispatcher.scheduler.advanceTimeBy(500)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, vm.state.value.totalCount)
        assertEquals("Bolo de Fubá", vm.state.value.recipes.first().title)
    }

    @Test
    fun toggleLayoutPersists() = runTest(dispatcher) {
        val repo = FakeRecipeRepository(emptyList())
        val prefs = FakeUserPrefs(RecipeListLayout.LIST)
        val vm = RecipeListViewModel(repo, prefs)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(RecipeListLayout.LIST, vm.state.value.layout)
        vm.onToggleLayout()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(RecipeListLayout.GRID, vm.state.value.layout)
    }
}

