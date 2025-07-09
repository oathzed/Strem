package com.strem.app.di

import com.google.gson.GsonBuilder
import com.strem.app.BuildConfig
import com.strem.app.data.api.stremio.StremioApi
import com.strem.app.data.api.tmdb.TmdbApi
import com.strem.app.data.api.trakt.TraktApi
import com.strem.app.data.preferences.PreferencesManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    single {
        GsonBuilder()
            .setLenient()
            .create()
    }
    
    // TMDB API
    single {
        val preferencesManager: PreferencesManager = get()
        val tmdbApiKey = preferencesManager.getTmdbApiKey() ?: BuildConfig.TMDB_API_KEY
        
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(TmdbApi::class.java)
            .apply {
                setApiKey(tmdbApiKey)
            }
    }
    
    // Trakt API
    single {
        val preferencesManager: PreferencesManager = get()
        val traktClientId = preferencesManager.getTraktClientId() ?: BuildConfig.TRAKT_CLIENT_ID
        val traktClientSecret = preferencesManager.getTraktClientSecret() ?: BuildConfig.TRAKT_CLIENT_SECRET
        
        val okHttpClient = get<OkHttpClient>().newBuilder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("trakt-api-version", "2")
                    .header("trakt-api-key", traktClientId)
                    .build()
                chain.proceed(request)
            }
            .build()
        
        Retrofit.Builder()
            .baseUrl("https://api.trakt.tv/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(TraktApi::class.java)
            .apply {
                setClientId(traktClientId)
                setClientSecret(traktClientSecret)
            }
    }
    
    // Stremio API
    singleOf(::StremioApi)
}