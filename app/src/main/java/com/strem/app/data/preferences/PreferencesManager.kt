package com.strem.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    // API Keys
    suspend fun setTmdbApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[TMDB_API_KEY] = apiKey
        }
    }
    
    suspend fun getTmdbApiKey(): String? {
        return dataStore.data.map { preferences ->
            preferences[TMDB_API_KEY]
        }.firstOrNull()
    }
    
    suspend fun setTraktClientId(clientId: String) {
        dataStore.edit { preferences ->
            preferences[TRAKT_CLIENT_ID] = clientId
        }
    }
    
    suspend fun getTraktClientId(): String? {
        return dataStore.data.map { preferences ->
            preferences[TRAKT_CLIENT_ID]
        }.firstOrNull()
    }
    
    suspend fun setTraktClientSecret(clientSecret: String) {
        dataStore.edit { preferences ->
            preferences[TRAKT_CLIENT_SECRET] = clientSecret
        }
    }
    
    suspend fun getTraktClientSecret(): String? {
        return dataStore.data.map { preferences ->
            preferences[TRAKT_CLIENT_SECRET]
        }.firstOrNull()
    }
    
    // Trakt Auth
    suspend fun setTraktAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[TRAKT_ACCESS_TOKEN] = accessToken
        }
    }
    
    suspend fun getTraktAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TRAKT_ACCESS_TOKEN]
        }.firstOrNull()
    }
    
    suspend fun setTraktRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[TRAKT_REFRESH_TOKEN] = refreshToken
        }
    }
    
    suspend fun getTraktRefreshToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TRAKT_REFRESH_TOKEN]
        }.firstOrNull()
    }
    
    suspend fun setTraktUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[TRAKT_USERNAME] = username
        }
    }
    
    fun getTraktUsername(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TRAKT_USERNAME]
        }
    }
    
    suspend fun clearTraktAuth() {
        dataStore.edit { preferences ->
            preferences.remove(TRAKT_ACCESS_TOKEN)
            preferences.remove(TRAKT_REFRESH_TOKEN)
            preferences.remove(TRAKT_USERNAME)
        }
    }
    
    // Playback Settings
    fun getResumePlayback(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[RESUME_PLAYBACK] ?: true
        }
    }
    
    suspend fun setResumePlayback(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[RESUME_PLAYBACK] = enabled
        }
    }
    
    fun getCacheStreams(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[CACHE_STREAMS] ?: true
        }
    }
    
    suspend fun setCacheStreams(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CACHE_STREAMS] = enabled
        }
    }
    
    companion object {
        // API Keys
        private val TMDB_API_KEY = stringPreferencesKey("tmdb_api_key")
        private val TRAKT_CLIENT_ID = stringPreferencesKey("trakt_client_id")
        private val TRAKT_CLIENT_SECRET = stringPreferencesKey("trakt_client_secret")
        
        // Trakt Auth
        private val TRAKT_ACCESS_TOKEN = stringPreferencesKey("trakt_access_token")
        private val TRAKT_REFRESH_TOKEN = stringPreferencesKey("trakt_refresh_token")
        private val TRAKT_USERNAME = stringPreferencesKey("trakt_username")
        
        // Playback Settings
        private val RESUME_PLAYBACK = booleanPreferencesKey("resume_playback")
        private val CACHE_STREAMS = booleanPreferencesKey("cache_streams")
    }
}