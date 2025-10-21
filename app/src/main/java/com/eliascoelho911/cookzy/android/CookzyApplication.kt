package com.eliascoelho911.cookzy.android

import android.app.Application
import com.eliascoelho911.cookzy.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CookzyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CookzyApplication)
            modules(appModules)
        }
    }
}
