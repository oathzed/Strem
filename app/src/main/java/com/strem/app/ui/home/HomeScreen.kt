package com.strem.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.strem.app.R
import com.strem.app.ui.components.ContentRow
import com.strem.app.ui.components.ErrorView
import com.strem.app.ui.components.LoadingView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingView()
            }
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadContent() }
                )
            }
            else -> {
                HomeContent(
                    uiState = uiState,
                    onContentClick = { contentId, contentType ->
                        navController.navigate("details/$contentId/$contentType")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onContentClick: (String, String) -> Unit
) {
    TvLazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = androidx.tv.material3.MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        if (uiState.continueWatching.isNotEmpty()) {
            item {
                ContentRow(
                    title = stringResource(R.string.continue_watching),
                    items = uiState.continueWatching,
                    onItemClick = { content ->
                        onContentClick(content.id, content.contentType)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        if (uiState.traktWatchlist.isNotEmpty()) {
            item {
                ContentRow(
                    title = stringResource(R.string.watchlist),
                    items = uiState.traktWatchlist,
                    onItemClick = { content ->
                        onContentClick(content.id, content.contentType)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        if (uiState.traktCollection.isNotEmpty()) {
            item {
                ContentRow(
                    title = stringResource(R.string.collection),
                    items = uiState.traktCollection,
                    onItemClick = { content ->
                        onContentClick(content.id, content.contentType)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        item {
            ContentRow(
                title = stringResource(R.string.popular_movies),
                items = uiState.popularMovies,
                onItemClick = { content ->
                    onContentClick(content.id, content.contentType)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            ContentRow(
                title = stringResource(R.string.popular_shows),
                items = uiState.popularTvShows,
                onItemClick = { content ->
                    onContentClick(content.id, content.contentType)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            ContentRow(
                title = "Top Rated Movies",
                items = uiState.topRatedMovies,
                onItemClick = { content ->
                    onContentClick(content.id, content.contentType)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            ContentRow(
                title = "Top Rated TV Shows",
                items = uiState.topRatedTvShows,
                onItemClick = { content ->
                    onContentClick(content.id, content.contentType)
                }
            )
        }
    }
}