package com.eliascoelho911.cookzy.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eliascoelho911.cookzy.ui.CookzyApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val application = application as CookzyApplication
            CookzyApp(
                repository = application.recipeRepository,
                finishApp = { finish() }
            )
        }
    }
}
