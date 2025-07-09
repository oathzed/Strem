package com.strem.app.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.strem.app.R
import com.strem.app.data.model.tmdb.TmdbEpisode
import com.strem.app.data.model.tmdb.TmdbSeason
import com.strem.app.ui.components.ErrorView
import com.strem.app.ui.components.LoadingView
import com.strem.app.ui.components.RatingBadge
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    contentId: String,
    contentType: String,
    viewModel: DetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(contentId, contentType) {
        viewModel.loadContent(contentId, contentType)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingView()
            }
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadContent(contentId, contentType) }
                )
            }
            uiState.content != null -> {
                DetailsContent(
                    uiState = uiState,
                    onPlayClick = { content, seasonNumber, episodeNumber ->
                        val seasonParam = seasonNumber?.let { "&seasonNumber=$it" } ?: ""
                        val episodeParam = episodeNumber?.let { "&episodeNumber=$it" } ?: ""
                        navController.navigate("sources/${content.id}/${content.contentType}?$seasonParam$episodeParam")
                    },
                    onTrailerClick = { trailerKey ->
                        // Open YouTube trailer
                    },
                    onSeasonSelect = { seasonNumber ->
                        viewModel.loadSeason(contentId, seasonNumber)
                    },
                    onToggleWatchlist = {
                        viewModel.toggleWatchlist()
                    },
                    onToggleCollection = {
                        viewModel.toggleCollection()
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
fun DetailsContent(
    uiState: DetailsUiState,
    onPlayClick: (com.strem.app.data.database.entity.ContentEntity, Int?, Int?) -> Unit,
    onTrailerClick: (String) -> Unit,
    onSeasonSelect: (Int) -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleCollection: () -> Unit,
    onBackClick: () -> Unit
) {
    val content = uiState.content!!
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop image
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w1280${content.backdropPath}",
            contentDescription = content.title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x80000000),
                            Color(0xE6000000)
                        )
                    )
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Back button
            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Back")
            }
            
            Row(modifier = Modifier.fillMaxWidth()) {
                // Poster
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w342${content.posterPath}",
                    contentDescription = content.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.width(200.dp)
                )
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Release date and rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        content.releaseDate?.let {
                            Text(
                                text = SimpleDateFormat("yyyy", Locale.US).format(it),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        
                        content.voteAverage?.let {
                            RatingBadge(rating = it)
                        }
                    }
                    
                    // Genres
                    if (content.genres.isNotEmpty()) {
                        Text(
                            text = content.genres.joinToString(" • "),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // Overview
                    content.overview?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Button(
                            onClick = { 
                                if (content.contentType == "movie") {
                                    onPlayClick(content, null, null)
                                } else if (uiState.selectedSeason != null && uiState.episodes.isNotEmpty()) {
                                    onPlayClick(content, uiState.selectedSeason, uiState.episodes.first().episodeNumber)
                                } else {
                                    onPlayClick(content, 1, 1) // Default to first episode of first season
                                }
                            }
                        ) {
                            Text(stringResource(R.string.play))
                        }
                        
                        uiState.trailerKey?.let { trailerKey ->
                            Button(
                                onClick = { onTrailerClick(trailerKey) }
                            ) {
                                Text(stringResource(R.string.watch_trailer))
                            }
                        }
                        
                        Button(
                            onClick = onToggleWatchlist
                        ) {
                            Text(
                                if (uiState.isInWatchlist) {
                                    stringResource(R.string.remove_from_watchlist)
                                } else {
                                    stringResource(R.string.add_to_watchlist)
                                }
                            )
                        }
                        
                        Button(
                            onClick = onToggleCollection
                        ) {
                            Text(
                                if (uiState.isInCollection) {
                                    stringResource(R.string.remove_from_collection)
                                } else {
                                    stringResource(R.string.add_to_collection)
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // TV Show specific content
            if (content.contentType == "tv" && uiState.seasons.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.seasons),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Seasons list
                TvLazyRow(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(uiState.seasons) { season ->
                        SeasonItem(
                            season = season,
                            isSelected = uiState.selectedSeason == season.seasonNumber,
                            onClick = { onSeasonSelect(season.seasonNumber) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Episodes
                if (uiState.selectedSeason != null) {
                    Text(
                        text = stringResource(R.string.episodes),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    if (uiState.isLoadingEpisodes) {
                        LoadingView(modifier = Modifier.height(100.dp))
                    } else if (uiState.episodes.isNotEmpty()) {
                        TvLazyRow(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(uiState.episodes) { episode ->
                                EpisodeItem(
                                    episode = episode,
                                    onClick = {
                                        onPlayClick(
                                            content,
                                            uiState.selectedSeason,
                                            episode.episodeNumber
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SeasonItem(
    season: TmdbSeason,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.season_number, season.seasonNumber),
            style = if (isSelected) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EpisodeItem(
    episode: TmdbEpisode,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(300.dp)
            .padding(end = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w300${episode.stillPath}",
                contentDescription = episode.name,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.episode_number, episode.episodeNumber),
                style = MaterialTheme.typography.bodySmall
            )
            
            Text(
                text = episode.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}