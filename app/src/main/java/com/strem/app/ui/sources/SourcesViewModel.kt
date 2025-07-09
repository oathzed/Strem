package com.strem.app.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.model.stremio.StremioStream
import com.strem.app.data.repository.ContentRepository
import com.strem.app.data.repository.StremioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SourcesViewModel(
    private val contentRepository: ContentRepository,
    private val stremioRepository: StremioRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SourcesUiState())
    val uiState: StateFlow<SourcesUiState> = _uiState.asStateFlow()
    
    fun loadContent(contentId: String, contentType: String, seasonNumber: Int?, episodeNumber: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load content details
                val content = contentRepository.getContentById(contentId)
                    ?: throw Exception("Content not found")
                
                _uiState.update { 
                    it.copy(
                        content = content,
                        seasonNumber = seasonNumber,
                        episodeNumber = episodeNumber
                    )
                }
                
                // Load streams from Stremio addons
                loadStreams(content, seasonNumber, episodeNumber)
                
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
    
    private suspend fun loadStreams(content: ContentEntity, seasonNumber: Int?, episodeNumber: Int?) {
        try {
            val imdbId = content.imdbId ?: run {
                // If we don't have IMDB ID, try to get it
                when (content.contentType) {
                    "movie" -> {
                        val movie = contentRepository.getMovieDetails(content.id.toInt())
                        movie.imdbId
                    }
                    "tv" -> {
                        val tvShow = contentRepository.getTvShowDetails(content.id.toInt())
                        tvShow.externalIds?.imdbId
                    }
                    else -> null
                }
            }
            
            if (imdbId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "IMDB ID not found for this content"
                    )
                }
                return
            }
            
            // Get streams from Stremio addons
            val streams = if (content.contentType == "movie") {
                stremioRepository.getMovieStreams(imdbId)
            } else {
                if (seasonNumber != null && episodeNumber != null) {
                    stremioRepository.getTvShowStreams(imdbId, seasonNumber, episodeNumber)
                } else {
                    emptyList()
                }
            }
            
            _uiState.update { 
                it.copy(
                    streams = streams,
                    isLoading = false
                )
            }
            
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

data class SourcesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val content: ContentEntity? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val streams: List<StremioStream> = emptyList()
)