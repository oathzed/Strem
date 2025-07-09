package com.strem.app.data.trakt

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for interacting with Trakt API.
 */
class TraktRepository(private val context: Context) {
    
    companion object {
        private const val TRAKT_API_URL = "https://api.trakt.tv/"
        private const val TRAKT_CLIENT_ID = "YOUR_TRAKT_CLIENT_ID" // Replace with your Trakt client ID
        private const val TRAKT_CLIENT_SECRET = "YOUR_TRAKT_CLIENT_SECRET" // Replace with your Trakt client secret
        private const val TRAKT_REDIRECT_URI = "strem://auth"
        
        private const val PREFS_NAME = "trakt_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
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
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
    
    private val api: TraktApi by lazy {
        Retrofit.Builder()
            .baseUrl(TRAKT_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TraktApi::class.java)
    }
    
    /**
     * Check if user is logged in.
     */
    fun isLoggedIn(): Boolean {
        val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
        val tokenExpiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        
        return accessToken != null && System.currentTimeMillis() < tokenExpiry
    }
    
    /**
     * Get authorization URL for OAuth.
     */
    fun getAuthorizationUrl(): String {
        return "${TRAKT_API_URL}oauth/authorize" +
                "?response_type=code" +
                "&client_id=$TRAKT_CLIENT_ID" +
                "&redirect_uri=$TRAKT_REDIRECT_URI"
    }
    
    /**
     * Exchange authorization code for token.
     */
    suspend fun getToken(code: String): Result<TraktToken> = withContext(Dispatchers.IO) {
        try {
            val response = api.getToken(
                code = code,
                clientId = TRAKT_CLIENT_ID,
                clientSecret = TRAKT_CLIENT_SECRET,
                redirectUri = TRAKT_REDIRECT_URI
            )
            
            if (response.isSuccessful) {
                val token = response.body()
                if (token != null) {
                    saveToken(token)
                    Result.success(token)
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
     * Refresh token.
     */
    suspend fun refreshToken(): Result<TraktToken> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
                ?: return@withContext Result.failure(Exception("No refresh token"))
            
            val response = api.refreshToken(
                refreshToken = refreshToken,
                clientId = TRAKT_CLIENT_ID,
                clientSecret = TRAKT_CLIENT_SECRET,
                redirectUri = TRAKT_REDIRECT_URI
            )
            
            if (response.isSuccessful) {
                val token = response.body()
                if (token != null) {
                    saveToken(token)
                    Result.success(token)
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
     * Save token to SharedPreferences.
     */
    private fun saveToken(token: TraktToken) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, token.accessToken)
            putString(KEY_REFRESH_TOKEN, token.refreshToken)
            putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (token.expiresIn * 1000))
            apply()
        }
    }
    
    /**
     * Get user profile.
     */
    suspend fun getUser(): Result<TraktUser> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.getUser("Bearer $accessToken")
            
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
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
     * Get user's lists.
     */
    suspend fun getLists(): Result<List<TraktList>> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.getLists("Bearer $accessToken")
            
            if (response.isSuccessful) {
                val lists = response.body()
                if (lists != null) {
                    Result.success(lists)
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
     * Get items in a list.
     */
    suspend fun getListItems(
        listId: String,
        type: String? = null
    ): Result<List<TraktListItem>> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.getListItems("Bearer $accessToken", listId, type)
            
            if (response.isSuccessful) {
                val items = response.body()
                if (items != null) {
                    Result.success(items)
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
     * Get user's watched history.
     */
    suspend fun getWatchedHistory(
        type: String,
        limit: Int? = null,
        page: Int? = null
    ): Result<List<TraktWatchedItem>> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.getWatchedHistory("Bearer $accessToken", type, limit, page)
            
            if (response.isSuccessful) {
                val items = response.body()
                if (items != null) {
                    Result.success(items)
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
     * Get user's watching progress.
     */
    suspend fun getPlaybackProgress(
        type: String,
        limit: Int? = null
    ): Result<List<TraktPlaybackItem>> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.getPlaybackProgress("Bearer $accessToken", type, limit)
            
            if (response.isSuccessful) {
                val items = response.body()
                if (items != null) {
                    Result.success(items)
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
     * Add item to history.
     */
    suspend fun addToHistory(request: TraktSyncRequest): Result<TraktSyncResponse> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.addToHistory("Bearer $accessToken", request)
            
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Result.success(result)
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
     * Add item to watchlist.
     */
    suspend fun addToWatchlist(request: TraktSyncRequest): Result<TraktSyncResponse> = withContext(Dispatchers.IO) {
        try {
            val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
                ?: return@withContext Result.failure(Exception("Not logged in"))
            
            val response = api.addToWatchlist("Bearer $accessToken", request)
            
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Result.success(result)
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
     * Logout user.
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}