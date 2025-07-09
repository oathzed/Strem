package com.strem.app.ui.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.strem.app.data.preferences.PreferencesManager
import com.strem.app.data.repository.PlaybackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

@UnstableApi
class PlayerViewModel(
    application: Application,
    private val playbackRepository: PlaybackRepository,
    private val preferencesManager: PreferencesManager,
    private val exoPlayerCache: SimpleCache
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    private var player: ExoPlayer? = null
    private var playbackProgressTrackingJob: kotlinx.coroutines.Job? = null
    
    fun initializePlayer(
        url: String,
        contentId: String,
        contentType: String,
        seasonNumber: Int?,
        episodeNumber: Int?
    ) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    contentId = contentId,
                    contentType = contentType,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber
                )
            }
            
            try {
                // Create player
                val context = getApplication<Application>()
                
                // Check if we should use cache
                val useCache = preferencesManager.getCacheStreams().first()
                
                // Create data source factory
                val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(15000)
                    .setReadTimeoutMs(15000)
                
                val dataSourceFactory = if (useCache) {
                    CacheDataSource.Factory()
                        .setCache(exoPlayerCache)
                        .setUpstreamDataSourceFactory(httpDataSourceFactory)
                        .setCacheWriteDataSinkFactory(null) // Read-only for now
                } else {
                    DefaultDataSource.Factory(context, httpDataSourceFactory)
                }
                
                // Create media source
                val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
                
                // Create player
                player = ExoPlayer.Builder(context)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build()
                
                // Set player listener
                player?.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_READY -> {
                                _uiState.update { 
                                    it.copy(
                                        isLoading = false,
                                        isPlaying = player?.isPlaying ?: false,
                                        duration = player?.duration ?: 0
                                    )
                                }
                                
                                // Check if we should resume playback
                                checkAndResumePlayback()
                                
                                // Start tracking playback progress
                                startPlaybackProgressTracking()
                            }
                            Player.STATE_ENDED -> {
                                _uiState.update { 
                                    it.copy(
                                        isPlaying = false
                                    )
                                }
                                
                                // Save playback progress
                                savePlaybackProgress(player?.duration ?: 0)
                            }
                            Player.STATE_BUFFERING -> {
                                _uiState.update { 
                                    it.copy(
                                        isLoading = true
                                    )
                                }
                            }
                            Player.STATE_IDLE -> {
                                _uiState.update { 
                                    it.copy(
                                        isLoading = false,
                                        error = "Playback error"
                                    )
                                }
                            }
                        }
                    }
                    
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.update { 
                            it.copy(
                                isPlaying = isPlaying
                            )
                        }
                    }
                })
                
                // Create media item
                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(url))
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .build()
                
                // Set media item
                player?.setMediaItem(mediaItem)
                player?.prepare()
                
                _uiState.update { 
                    it.copy(
                        player = player
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
    
    private fun checkAndResumePlayback() {
        viewModelScope.launch {
            try {
                val shouldResumePlayback = preferencesManager.getResumePlayback().first()
                
                if (shouldResumePlayback) {
                    val contentId = _uiState.value.contentId ?: return@launch
                    val seasonNumber = _uiState.value.seasonNumber
                    val episodeNumber = _uiState.value.episodeNumber
                    
                    val progress = playbackRepository.getPlaybackProgress(
                        contentId, seasonNumber, episodeNumber
                    )
                    
                    progress?.let {
                        // Only resume if we're less than 95% through the video
                        if (it.position < it.duration * 0.95) {
                            player?.seekTo(it.position)
                        }
                    }
                }
                
                // Start playback
                player?.play()
                
            } catch (e: Exception) {
                // Ignore errors, just start playing
                player?.play()
            }
        }
    }
    
    private fun startPlaybackProgressTracking() {
        playbackProgressTrackingJob?.cancel()
        
        playbackProgressTrackingJob = viewModelScope.launch {
            while (true) {
                val currentPosition = player?.currentPosition ?: 0
                val duration = player?.duration ?: 0
                
                _uiState.update { 
                    it.copy(
                        currentPosition = currentPosition,
                        duration = duration
                    )
                }
                
                // Save progress every 5 seconds
                if (currentPosition > 0 && currentPosition % 5000 < 1000) {
                    savePlaybackProgress(currentPosition)
                }
                
                kotlinx.coroutines.delay(1000)
            }
        }
    }
    
    private fun savePlaybackProgress(position: Long) {
        viewModelScope.launch {
            val contentId = _uiState.value.contentId ?: return@launch
            val seasonNumber = _uiState.value.seasonNumber
            val episodeNumber = _uiState.value.episodeNumber
            val duration = _uiState.value.duration
            
            if (contentId.isNotEmpty() && duration > 0) {
                playbackRepository.savePlaybackProgress(
                    contentId = contentId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    position = position,
                    duration = duration
                )
            }
        }
    }
    
    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
    
    fun seekForward() {
        player?.let {
            val newPosition = (it.currentPosition + 10000).coerceAtMost(it.duration)
            it.seekTo(newPosition)
        }
    }
    
    fun seekBackward() {
        player?.let {
            val newPosition = (it.currentPosition - 10000).coerceAtLeast(0)
            it.seekTo(newPosition)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        
        // Save final playback progress
        player?.let {
            if (it.currentPosition > 0) {
                savePlaybackProgress(it.currentPosition)
            }
        }
        
        // Release player
        playbackProgressTrackingJob?.cancel()
        player?.release()
        player = null
    }
}

data class PlayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val contentId: String? = null,
    val contentType: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val player: ExoPlayer? = null
)