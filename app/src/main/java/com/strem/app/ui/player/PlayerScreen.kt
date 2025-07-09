package com.strem.app.ui.player

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Slider
import androidx.tv.material3.Text
import com.strem.app.ui.components.ErrorView
import com.strem.app.ui.components.LoadingView
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalTvMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerScreen(
    navController: NavController,
    url: String,
    contentId: String,
    contentType: String,
    seasonNumber: Int? = null,
    episodeNumber: Int? = null,
    viewModel: PlayerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Decode URL
    val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    
    // Controls visibility state
    var controlsVisible by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    
    // Initialize player
    LaunchedEffect(decodedUrl) {
        viewModel.initializePlayer(decodedUrl, contentId, contentType, seasonNumber, episodeNumber)
    }
    
    // Auto-hide controls
    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            delay(5000)
            controlsVisible = false
        }
    }
    
    // Handle back button
    DisposableEffect(Unit) {
        onDispose {
            // Save playback progress before leaving
            uiState.player?.let {
                if (it.currentPosition > 0) {
                    // This will be handled in onCleared()
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onKeyEvent { keyEvent ->
                when (keyEvent.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> {
                        if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            viewModel.togglePlayPause()
                            controlsVisible = true
                            true
                        } else false
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            viewModel.seekBackward()
                            controlsVisible = true
                            true
                        } else false
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            viewModel.seekForward()
                            controlsVisible = true
                            true
                        } else false
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            navController.popBackStack()
                            true
                        } else false
                    }
                    else -> {
                        controlsVisible = true
                        false
                    }
                }
            }
    ) {
        // Player view
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    player = uiState.player
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { playerView ->
                playerView.player = uiState.player
            }
        )
        
        // Loading indicator
        if (uiState.isLoading) {
            LoadingView()
        }
        
        // Error view
        if (uiState.error != null) {
            ErrorView(
                error = uiState.error!!,
                onRetry = {
                    viewModel.initializePlayer(decodedUrl, contentId, contentType, seasonNumber, episodeNumber)
                }
            )
        }
        
        // Controls overlay
        if (controlsVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .padding(24.dp)
            ) {
                // Top controls (back button)
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .focusRequester(focusRequester)
                ) {
                    Text("Back")
                }
                
                // Center controls (play/pause)
                Row(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Button(
                        onClick = { viewModel.seekBackward() }
                    ) {
                        Text("-10s")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { viewModel.togglePlayPause() }
                    ) {
                        Text(if (uiState.isPlaying) "Pause" else "Play")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { viewModel.seekForward() }
                    ) {
                        Text("+10s")
                    }
                }
                
                // Bottom controls (progress bar)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    // Progress slider
                    Slider(
                        value = uiState.currentPosition.toFloat(),
                        onValueChange = { viewModel.seekTo(it.toLong()) },
                        valueRange = 0f..uiState.duration.toFloat().coerceAtLeast(1f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Time display
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formatDuration(uiState.currentPosition),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = formatDuration(uiState.duration),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Request focus for the back button
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}