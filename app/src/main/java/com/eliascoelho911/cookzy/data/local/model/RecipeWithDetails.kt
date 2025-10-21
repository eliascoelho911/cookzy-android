package com.eliascoelho911.cookzy.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.eliascoelho911.cookzy.data.local.entity.IngredientEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeStepEntity

data class RecipeWithDetails(
    @Embedded
    val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = IngredientEntity::class
    )
    val ingredients: List<IngredientEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeStepEntity::class
    )
    val steps: List<RecipeStepEntity>
)
