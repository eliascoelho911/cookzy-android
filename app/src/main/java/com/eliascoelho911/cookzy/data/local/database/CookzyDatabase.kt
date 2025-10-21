package com.eliascoelho911.cookzy.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eliascoelho911.cookzy.data.local.dao.RecipeDao
import com.eliascoelho911.cookzy.data.local.entity.IngredientEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeEntity
import com.eliascoelho911.cookzy.data.local.entity.RecipeStepEntity

@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        RecipeStepEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CookzyDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        const val DATABASE_NAME = "cookzy.db"

        fun create(context: Context): CookzyDatabase =
            Room.databaseBuilder(context, CookzyDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}
