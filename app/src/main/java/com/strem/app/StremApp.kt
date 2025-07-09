package com.strem.app

import android.app.Application
import com.strem.app.di.apiModule
import com.strem.app.di.databaseModule
import com.strem.app.di.repositoryModule
import com.strem.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StremApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@StremApp)
            modules(listOf(
                apiModule,
                databaseModule,
                repositoryModule,
                viewModelModule
            ))
        }
    }
}