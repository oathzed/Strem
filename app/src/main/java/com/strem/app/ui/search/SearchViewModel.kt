package com.strem.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.repository.ContentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SearchViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        searchJob?.cancel()
        
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isLoading = false) }
            return
        }
        
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Debounce search
            delay(300)
            
            try {
                val results = contentRepository.searchMulti(query)
                
                val contentEntities = results.results.mapNotNull { result ->
                    when (result.mediaType) {
                        "movie" -> {
                            ContentEntity(
                                id = result.id.toString(),
                                imdbId = null,
                                title = result.title ?: "",
                                overview = result.overview,
                                posterPath = result.posterPath,
                                backdropPath = result.backdropPath,
                                releaseDate = result.releaseDate?.let { date ->
                                    try {
                                        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
                                    } catch (e: Exception) {
                                        null
                                    }
                                },
                                voteAverage = result.voteAverage,
                                contentType = "movie",
                                genres = emptyList()
                            )
                        }
                        "tv" -> {
                            ContentEntity(
                                id = result.id.toString(),
                                imdbId = null,
                                title = result.name ?: "",
                                overview = result.overview,
                                posterPath = result.posterPath,
                                backdropPath = result.backdropPath,
                                releaseDate = result.firstAirDate?.let { date ->
                                    try {
                                        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
                                    } catch (e: Exception) {
                                        null
                                    }
                                },
                                voteAverage = result.voteAverage,
                                contentType = "tv",
                                genres = emptyList()
                            )
                        }
                        else -> null
                    }
                }
                
                _uiState.update { 
                    it.copy(
                        searchResults = contentEntities,
                        isLoading = false,
                        error = null
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
    
    fun clearSearch() {
        _uiState.update { 
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isLoading = false,
                error = null
            )
        }
    }
}

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<ContentEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)