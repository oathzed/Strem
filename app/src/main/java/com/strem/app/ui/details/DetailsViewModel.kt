package com.strem.app.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.model.tmdb.TmdbEpisode
import com.strem.app.data.model.tmdb.TmdbSeason
import com.strem.app.data.repository.ContentRepository
import com.strem.app.data.repository.TraktRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsViewModel(
    private val contentRepository: ContentRepository,
    private val traktRepository: TraktRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    
    fun loadContent(contentId: String, contentType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // First check if we have it in the database
                val contentFromDb = contentRepository.getContentById(contentId)
                
                if (contentFromDb != null) {
                    _uiState.update { 
                        it.copy(
                            content = contentFromDb,
                            isLoading = false
                        )
                    }
                }
                
                // Load from API
                when (contentType) {
                    "movie" -> {
                        val movie = contentRepository.getMovieDetails(contentId.toInt())
                        val content = ContentEntity(
                            id = movie.id.toString(),
                            imdbId = movie.imdbId,
                            title = movie.title,
                            overview = movie.overview,
                            posterPath = movie.posterPath,
                            backdropPath = movie.backdropPath,
                            releaseDate = movie.releaseDate?.let { date ->
                                try {
                                    SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            voteAverage = movie.voteAverage,
                            contentType = "movie",
                            genres = movie.genres?.map { it.name } ?: emptyList()
                        )
                        
                        // Save to database
                        contentRepository.saveContent(content)
                        
                        // Check if in watchlist or collection
                        val isInWatchlist = traktRepository.isInWatchlist(contentId)
                        val isInCollection = traktRepository.isInCollection(contentId)
                        
                        _uiState.update { 
                            it.copy(
                                content = content,
                                isLoading = false,
                                isInWatchlist = isInWatchlist,
                                isInCollection = isInCollection
                            )
                        }
                        
                        // Load videos (trailers)
                        val videos = contentRepository.getMovieVideos(contentId.toInt())
                        val trailers = videos.results.filter { it.type == "Trailer" && it.site == "YouTube" }
                        
                        _uiState.update { 
                            it.copy(
                                trailerKey = trailers.firstOrNull()?.key
                            )
                        }
                    }
                    "tv" -> {
                        val tvShow = contentRepository.getTvShowDetails(contentId.toInt())
                        val content = ContentEntity(
                            id = tvShow.id.toString(),
                            imdbId = tvShow.externalIds?.imdbId,
                            title = tvShow.name,
                            overview = tvShow.overview,
                            posterPath = tvShow.posterPath,
                            backdropPath = tvShow.backdropPath,
                            releaseDate = tvShow.firstAirDate?.let { date ->
                                try {
                                    SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            voteAverage = tvShow.voteAverage,
                            contentType = "tv",
                            genres = tvShow.genres?.map { it.name } ?: emptyList()
                        )
                        
                        // Save to database
                        contentRepository.saveContent(content)
                        
                        // Check if in watchlist or collection
                        val isInWatchlist = traktRepository.isInWatchlist(contentId)
                        val isInCollection = traktRepository.isInCollection(contentId)
                        
                        _uiState.update { 
                            it.copy(
                                content = content,
                                seasons = tvShow.seasons ?: emptyList(),
                                isLoading = false,
                                isInWatchlist = isInWatchlist,
                                isInCollection = isInCollection
                            )
                        }
                        
                        // Load videos (trailers)
                        val videos = contentRepository.getTvShowVideos(contentId.toInt())
                        val trailers = videos.results.filter { it.type == "Trailer" && it.site == "YouTube" }
                        
                        _uiState.update { 
                            it.copy(
                                trailerKey = trailers.firstOrNull()?.key
                            )
                        }
                    }
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
    
    fun loadSeason(contentId: String, seasonNumber: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEpisodes = true) }
            
            try {
                val season = contentRepository.getTvSeasonDetails(contentId.toInt(), seasonNumber)
                
                _uiState.update { 
                    it.copy(
                        selectedSeason = seasonNumber,
                        episodes = season.episodes ?: emptyList(),
                        isLoadingEpisodes = false
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoadingEpisodes = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
    
    fun toggleWatchlist() {
        viewModelScope.launch {
            val content = _uiState.value.content ?: return@launch
            val isInWatchlist = _uiState.value.isInWatchlist
            
            try {
                if (isInWatchlist) {
                    traktRepository.removeFromWatchlist(content.id, content.contentType)
                } else {
                    traktRepository.addToWatchlist(content.id, content.contentType)
                }
                
                _uiState.update { 
                    it.copy(
                        isInWatchlist = !isInWatchlist
                    )
                }
                
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun toggleCollection() {
        viewModelScope.launch {
            val content = _uiState.value.content ?: return@launch
            val isInCollection = _uiState.value.isInCollection
            
            try {
                if (isInCollection) {
                    traktRepository.removeFromCollection(content.id, content.contentType)
                } else {
                    traktRepository.addToCollection(content.id, content.contentType)
                }
                
                _uiState.update { 
                    it.copy(
                        isInCollection = !isInCollection
                    )
                }
                
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class DetailsUiState(
    val isLoading: Boolean = false,
    val isLoadingEpisodes: Boolean = false,
    val error: String? = null,
    val content: ContentEntity? = null,
    val seasons: List<TmdbSeason> = emptyList(),
    val selectedSeason: Int? = null,
    val episodes: List<TmdbEpisode> = emptyList(),
    val trailerKey: String? = null,
    val isInWatchlist: Boolean = false,
    val isInCollection: Boolean = false
)