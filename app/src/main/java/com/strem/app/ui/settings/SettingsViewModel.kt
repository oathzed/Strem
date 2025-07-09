package com.strem.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strem.app.data.database.entity.StremioAddonEntity
import com.strem.app.data.preferences.PreferencesManager
import com.strem.app.data.repository.StremioRepository
import com.strem.app.data.repository.TraktRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val traktRepository: TraktRepository,
    private val stremioRepository: StremioRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            // Load Trakt username
            preferencesManager.getTraktUsername().collectLatest { username ->
                _uiState.update { 
                    it.copy(
                        traktUsername = username
                    )
                }
            }
        }
        
        viewModelScope.launch {
            // Load resume playback setting
            preferencesManager.getResumePlayback().collectLatest { resumePlayback ->
                _uiState.update { 
                    it.copy(
                        resumePlayback = resumePlayback
                    )
                }
            }
        }
        
        viewModelScope.launch {
            // Load cache streams setting
            preferencesManager.getCacheStreams().collectLatest { cacheStreams ->
                _uiState.update { 
                    it.copy(
                        cacheStreams = cacheStreams
                    )
                }
            }
        }
        
        viewModelScope.launch {
            // Load Stremio addons
            stremioRepository.getAllAddons().collectLatest { addons ->
                _uiState.update { 
                    it.copy(
                        stremioAddons = addons
                    )
                }
            }
        }
    }
    
    fun setResumePlayback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setResumePlayback(enabled)
        }
    }
    
    fun setCacheStreams(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setCacheStreams(enabled)
        }
    }
    
    fun addStremioAddon(url: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                stremioRepository.addAddon(url)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
    
    fun removeStremioAddon(addonId: String) {
        viewModelScope.launch {
            stremioRepository.removeAddon(addonId)
        }
    }
    
    fun loginToTrakt(authCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                traktRepository.getAccessToken(authCode)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
    
    fun logoutFromTrakt() {
        viewModelScope.launch {
            traktRepository.logout()
        }
    }
    
    fun setTmdbApiKey(apiKey: String) {
        viewModelScope.launch {
            preferencesManager.setTmdbApiKey(apiKey)
        }
    }
    
    fun setTraktClientId(clientId: String) {
        viewModelScope.launch {
            preferencesManager.setTraktClientId(clientId)
        }
    }
    
    fun setTraktClientSecret(clientSecret: String) {
        viewModelScope.launch {
            preferencesManager.setTraktClientSecret(clientSecret)
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            // TODO: Implement cache clearing
        }
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val traktUsername: String? = null,
    val resumePlayback: Boolean = true,
    val cacheStreams: Boolean = true,
    val stremioAddons: List<StremioAddonEntity> = emptyList()
)