package com.strem.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.repository.ContentRepository
import com.strem.app.data.repository.PlaybackRepository
import com.strem.app.data.repository.TraktRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val playbackRepository: PlaybackRepository,
    private val traktRepository: TraktRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadContent()
    }
    
    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Continue watching
                val continueWatching = playbackRepository.getContinueWatchingContent(10)
                
                // Popular movies
                val popularMovies = contentRepository.getPopularMovies().results.map {
                    ContentEntity(
                        id = it.id.toString(),
                        imdbId = null,
                        title = it.title,
                        overview = it.overview,
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        releaseDate = it.releaseDate?.let { date ->
                            try {
                                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)
                            } catch (e: Exception) {
                                null
                            }
                        },
                        voteAverage = it.voteAverage,
                        contentType = "movie",
                        genres = emptyList()
                    )
                }
                
                // Top rated movies
                val topRatedMovies = contentRepository.getTopRatedMovies().results.map {
                    ContentEntity(
                        id = it.id.toString(),
                        imdbId = null,
                        title = it.title,
                        overview = it.overview,
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        releaseDate = it.releaseDate?.let { date ->
                            try {
                                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)
                            } catch (e: Exception) {
                                null
                            }
                        },
                        voteAverage = it.voteAverage,
                        contentType = "movie",
                        genres = emptyList()
                    )
                }
                
                // Popular TV shows
                val popularTvShows = contentRepository.getPopularTvShows().results.map {
                    ContentEntity(
                        id = it.id.toString(),
                        imdbId = null,
                        title = it.name,
                        overview = it.overview,
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        releaseDate = it.firstAirDate?.let { date ->
                            try {
                                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)
                            } catch (e: Exception) {
                                null
                            }
                        },
                        voteAverage = it.voteAverage,
                        contentType = "tv",
                        genres = emptyList()
                    )
                }
                
                // Top rated TV shows
                val topRatedTvShows = contentRepository.getTopRatedTvShows().results.map {
                    ContentEntity(
                        id = it.id.toString(),
                        imdbId = null,
                        title = it.name,
                        overview = it.overview,
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        releaseDate = it.firstAirDate?.let { date ->
                            try {
                                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)
                            } catch (e: Exception) {
                                null
                            }
                        },
                        voteAverage = it.voteAverage,
                        contentType = "tv",
                        genres = emptyList()
                    )
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        continueWatching = continueWatching,
                        popularMovies = popularMovies,
                        topRatedMovies = topRatedMovies,
                        popularTvShows = popularTvShows,
                        topRatedTvShows = topRatedTvShows
                    )
                }
                
                // Try to load Trakt data if logged in
                try {
                    val traktWatchlist = traktRepository.getWatchlist()
                    val traktCollection = traktRepository.getCollection()
                    
                    // TODO: Process Trakt data
                } catch (e: Exception) {
                    // Ignore Trakt errors
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
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val continueWatching: List<ContentEntity> = emptyList(),
    val popularMovies: List<ContentEntity> = emptyList(),
    val topRatedMovies: List<ContentEntity> = emptyList(),
    val popularTvShows: List<ContentEntity> = emptyList(),
    val topRatedTvShows: List<ContentEntity> = emptyList(),
    val traktWatchlist: List<ContentEntity> = emptyList(),
    val traktCollection: List<ContentEntity> = emptyList()
)