package com.strem.app.ui.sources

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.strem.app.R
import com.strem.app.data.model.stremio.StremioStream
import com.strem.app.ui.components.ErrorView
import com.strem.app.ui.components.LoadingView
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SourcesScreen(
    navController: NavController,
    contentId: String,
    contentType: String,
    seasonNumber: Int? = null,
    episodeNumber: Int? = null,
    viewModel: SourcesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(contentId, contentType, seasonNumber, episodeNumber) {
        viewModel.loadContent(contentId, contentType, seasonNumber, episodeNumber)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingView()
            }
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadContent(contentId, contentType, seasonNumber, episodeNumber) }
                )
            }
            uiState.content != null -> {
                SourcesContent(
                    uiState = uiState,
                    onSourceClick = { stream ->
                        val encodedUrl = URLEncoder.encode(stream.url, StandardCharsets.UTF_8.toString())
                        val seasonParam = seasonNumber?.let { "&seasonNumber=$it" } ?: ""
                        val episodeParam = episodeNumber?.let { "&episodeNumber=$it" } ?: ""
                        navController.navigate("player/$encodedUrl?contentId=$contentId&contentType=$contentType$seasonParam$episodeParam")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SourcesContent(
    uiState: SourcesUiState,
    onSourceClick: (StremioStream) -> Unit,
    onBackClick: () -> Unit
) {
    val content = uiState.content!!
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with content info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Back button
            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Back")
            }
            
            // Poster
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w92${content.posterPath}",
                contentDescription = content.title,
                modifier = Modifier.width(60.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Title and episode info
            Column {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (content.contentType == "tv" && uiState.seasonNumber != null && uiState.episodeNumber != null) {
                    Text(
                        text = "Season ${uiState.seasonNumber} Episode ${uiState.episodeNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sources title
        Text(
            text = stringResource(R.string.select_source),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sources list
        if (uiState.streams.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.no_sources_found),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            TvLazyColumn {
                items(uiState.streams) { stream ->
                    SourceItem(
                        stream = stream,
                        onClick = { onSourceClick(stream) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SourceItem(
    stream: StremioStream,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Quality
            Text(
                text = stream.title ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            // Source name
            stream.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            // Size if available
            stream.size?.let {
                Text(
                    text = formatFileSize(it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun formatFileSize(sizeBytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024
    
    return when {
        sizeBytes < mb -> String.format("%.2f KB", sizeBytes / kb)
        sizeBytes < gb -> String.format("%.2f MB", sizeBytes / mb)
        else -> String.format("%.2f GB", sizeBytes / gb)
    }
}