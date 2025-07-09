package com.strem.app.data.stremio

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for interacting with Stremio addons.
 */
class StremioRepository {
    
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val gson = GsonBuilder()
        .setLenient()
        .create()
    
    /**
     * Get addon manifest from URL.
     */
    suspend fun getAddonManifest(url: String): Result<StremioAddon> = withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://example.com/") // Base URL doesn't matter as we use @Url
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            
            val api = retrofit.create(StremioApi::class.java)
            val response = api.getManifest(url)
            
            if (response.isSuccessful) {
                val addon = response.body()
                if (addon != null) {
                    Result.success(addon)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get catalog items from addon.
     */
    suspend fun getCatalog(
        addonUrl: String,
        type: String,
        id: String,
        genre: String? = null,
        skip: Int? = null,
        search: String? = null
    ): Result<List<MetaItem>> = withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(addonUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            
            val api = retrofit.create(StremioApi::class.java)
            val response = api.getCatalog(type, id, genre, skip, search)
            
            if (response.isSuccessful) {
                val catalog = response.body()
                if (catalog != null) {
                    Result.success(catalog.metas)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get meta information from addon.
     */
    suspend fun getMeta(
        addonUrl: String,
        type: String,
        id: String
    ): Result<MetaItem> = withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(addonUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            
            val api = retrofit.create(StremioApi::class.java)
            val response = api.getMeta(type, id)
            
            if (response.isSuccessful) {
                val meta = response.body()
                if (meta != null) {
                    Result.success(meta.meta)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get streams from addon.
     */
    suspend fun getStreams(
        addonUrl: String,
        type: String,
        id: String
    ): Result<List<Stream>> = withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(addonUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            
            val api = retrofit.create(StremioApi::class.java)
            val response = api.getStreams(type, id)
            
            if (response.isSuccessful) {
                val streams = response.body()
                if (streams != null) {
                    Result.success(streams.streams)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}