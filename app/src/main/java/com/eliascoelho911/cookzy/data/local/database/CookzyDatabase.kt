package com.eliascoelho911.cookzy.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 1,
    exportSchema = true
)
abstract class CookzyDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        const val DATABASE_NAME = "cookzy.db"

        val MIGRATION_0_1 = object : Migration(0, 1) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recipes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recipe_ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recipeId INTEGER NOT NULL,
                        position INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        quantity TEXT,
                        unit TEXT,
                        note TEXT,
                        FOREIGN KEY(recipeId) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recipe_steps (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recipeId INTEGER NOT NULL,
                        position INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        FOREIGN KEY(recipeId) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recipe_ingredients_recipeId ON recipe_ingredients(recipeId)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_recipe_steps_recipeId ON recipe_steps(recipeId)"
                )
            }
        }

        fun create(context: Context): CookzyDatabase =
            Room.databaseBuilder(context, CookzyDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_0_1)
                .build()
    }
}
