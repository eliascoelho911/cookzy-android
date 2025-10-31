package com.eliascoelho911.cookzy.data.repository

import com.eliascoelho911.cookzy.data.local.dao.RecipeDao
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.model.RecipeWithDetails
import com.eliascoelho911.cookzy.domain.repository.RecipeFeed
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineRecipeRepositoryTest {

    private lateinit var repository: OfflineRecipeRepository
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipesFlow: MutableSharedFlow<List<RecipeWithDetails>>

    @Before
    fun setup() {
        recipeDao = mockk(relaxed = true)
        recipesFlow = MutableSharedFlow(replay = 1)
        every { recipeDao.observeRecipes() } returns recipesFlow
        repository = OfflineRecipeRepository(recipeDao)
    }

    @Test
    fun setSearchQuery_filtersObservedRecipes() = runTest {
        val emissionsDeferred = async {
            repository.observeRecipes()
                .take(2)
                .toList()
        }

        val recipes = listOf(
            recipe(id = 1L, title = "Bolo de Fubá"),
            recipe(id = 2L, title = "Lasanha de Legumes")
        )
        recipesFlow.emit(recipes)
        advanceUntilIdle()

        repository.setSearchQuery("  lasanha ")
        advanceUntilIdle()

        val emissions: List<RecipeFeed> = emissionsDeferred.await()

        assertEquals(2, emissions.size)
        val initial = emissions.first()
        assertEquals(2, initial.filtered.size)

        val filtered = emissions.last()
        assertEquals(1, filtered.filtered.size)
        assertEquals("Lasanha de Legumes", filtered.filtered.first().title)

        // The repository preserves the original query but trims when filtering.
        assertEquals("  lasanha ", filtered.filters.query)
    }

    private fun recipe(
        id: Long,
        title: String
    ): RecipeWithDetails = RecipeWithDetails(
        recipe = RecipeEntity(
            id = id,
            title = title,
            updatedAt = 0L
        ),
        ingredients = emptyList(),
        steps = emptyList()
    )
}
