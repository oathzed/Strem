package com.strem.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.strem.app.data.database.StremDatabase
import com.strem.app.data.preferences.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "strem_preferences")

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            StremDatabase::class.java,
            "strem_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    single { get<StremDatabase>().contentDao() }
    single { get<StremDatabase>().watchlistDao() }
    single { get<StremDatabase>().collectionDao() }
    single { get<StremDatabase>().playbackProgressDao() }
    single { get<StremDatabase>().stremioAddonDao() }
    
    single { androidContext().dataStore }
    singleOf(::PreferencesManager)
}