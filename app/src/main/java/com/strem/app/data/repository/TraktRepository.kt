package com.strem.app.data.repository

import com.strem.app.data.api.trakt.TraktApi
import com.strem.app.data.database.dao.CollectionDao
import com.strem.app.data.database.dao.ContentDao
import com.strem.app.data.database.dao.PlaybackProgressDao
import com.strem.app.data.database.dao.WatchlistDao
import com.strem.app.data.database.entity.CollectionEntity
import com.strem.app.data.database.entity.PlaybackProgressEntity
import com.strem.app.data.database.entity.WatchlistEntity
import com.strem.app.data.model.trakt.CollectionRequest
import com.strem.app.data.model.trakt.HistoryRequest
import com.strem.app.data.model.trakt.TraktEpisode
import com.strem.app.data.model.trakt.TraktMovie
import com.strem.app.data.model.trakt.TraktShow
import com.strem.app.data.model.trakt.WatchlistRequest
import com.strem.app.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TraktRepository(
    private val traktApi: TraktApi,
    private val preferencesManager: PreferencesManager,
    private val contentDao: ContentDao,
    private val watchlistDao: WatchlistDao,
    private val collectionDao: CollectionDao,
    private val playbackProgressDao: PlaybackProgressDao
) {
    suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val clientId = traktApi.javaClass.getDeclaredField("clientId").get(null) as String
        val clientSecret = traktApi.javaClass.getDeclaredField("clientSecret").get(null) as String
        
        val response = traktApi.getAccessToken(
            code = code,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = TraktApi.REDIRECT_URI
        )
        
        preferencesManager.setTraktAccessToken(response.accessToken)
        preferencesManager.setTraktRefreshToken(response.refreshToken)
        
        response
    }
    
    suspend fun refreshAccessToken() = withContext(Dispatchers.IO) {
        val refreshToken = preferencesManager.getTraktRefreshToken() ?: throw IllegalStateException("No refresh token")
        val clientId = traktApi.javaClass.getDeclaredField("clientId").get(null) as String
        val clientSecret = traktApi.javaClass.getDeclaredField("clientSecret").get(null) as String
        
        val response = traktApi.refreshAccessToken(
            refreshToken = refreshToken,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = TraktApi.REDIRECT_URI
        )
        
        preferencesManager.setTraktAccessToken(response.accessToken)
        preferencesManager.setTraktRefreshToken(response.refreshToken)
        
        response
    }
    
    suspend fun getUser() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val user = traktApi.getUser("Bearer $accessToken")
        preferencesManager.setTraktUsername(user.username)
        user
    }
    
    suspend fun getWatchlist() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val username = preferencesManager.getTraktUsername().first() ?: throw IllegalStateException("No username")
        
        traktApi.getWatchlist(username, "Bearer $accessToken")
    }
    
    suspend fun addToWatchlist(contentId: String, contentType: String) = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val content = contentDao.getContentById(contentId) ?: throw IllegalStateException("Content not found")
        
        val request = when (contentType) {
            "movie" -> {
                val movie = TraktMovie(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                WatchlistRequest(movies = listOf(movie))
            }
            "tv" -> {
                val show = TraktShow(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                WatchlistRequest(shows = listOf(show))
            }
            else -> throw IllegalArgumentException("Invalid content type")
        }
        
        traktApi.addToWatchlist("Bearer $accessToken", request)
        
        // Add to local watchlist
        val watchlistEntity = WatchlistEntity(
            contentId = contentId,
            syncedWithTrakt = true
        )
        watchlistDao.insertWatchlistItem(watchlistEntity)
    }
    
    suspend fun removeFromWatchlist(contentId: String, contentType: String) = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val content = contentDao.getContentById(contentId) ?: throw IllegalStateException("Content not found")
        
        val request = when (contentType) {
            "movie" -> {
                val movie = TraktMovie(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                WatchlistRequest(movies = listOf(movie))
            }
            "tv" -> {
                val show = TraktShow(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                WatchlistRequest(shows = listOf(show))
            }
            else -> throw IllegalArgumentException("Invalid content type")
        }
        
        traktApi.removeFromWatchlist("Bearer $accessToken", request)
        
        // Remove from local watchlist
        watchlistDao.removeWatchlistItemById(contentId)
    }
    
    suspend fun getCollection() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val username = preferencesManager.getTraktUsername().first() ?: throw IllegalStateException("No username")
        
        traktApi.getCollection(username, "Bearer $accessToken")
    }
    
    suspend fun addToCollection(contentId: String, contentType: String) = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val content = contentDao.getContentById(contentId) ?: throw IllegalStateException("Content not found")
        
        val request = when (contentType) {
            "movie" -> {
                val movie = TraktMovie(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                CollectionRequest(movies = listOf(movie))
            }
            "tv" -> {
                val show = TraktShow(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                CollectionRequest(shows = listOf(show))
            }
            else -> throw IllegalArgumentException("Invalid content type")
        }
        
        traktApi.addToCollection("Bearer $accessToken", request)
        
        // Add to local collection
        val collectionEntity = CollectionEntity(
            contentId = contentId,
            syncedWithTrakt = true
        )
        collectionDao.insertCollectionItem(collectionEntity)
    }
    
    suspend fun removeFromCollection(contentId: String, contentType: String) = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val content = contentDao.getContentById(contentId) ?: throw IllegalStateException("Content not found")
        
        val request = when (contentType) {
            "movie" -> {
                val movie = TraktMovie(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                CollectionRequest(movies = listOf(movie))
            }
            "tv" -> {
                val show = TraktShow(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                CollectionRequest(shows = listOf(show))
            }
            else -> throw IllegalArgumentException("Invalid content type")
        }
        
        traktApi.removeFromCollection("Bearer $accessToken", request)
        
        // Remove from local collection
        collectionDao.removeCollectionItemById(contentId)
    }
    
    suspend fun getHistory() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val username = preferencesManager.getTraktUsername().first() ?: throw IllegalStateException("No username")
        
        traktApi.getHistory(username, "Bearer $accessToken", limit = 50)
    }
    
    suspend fun addToHistory(contentId: String, contentType: String, seasonNumber: Int?, episodeNumber: Int?) = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: throw IllegalStateException("Not logged in")
        val content = contentDao.getContentById(contentId) ?: throw IllegalStateException("Content not found")
        
        val request = when (contentType) {
            "movie" -> {
                val movie = TraktMovie(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                HistoryRequest(movies = listOf(movie))
            }
            "tv" -> {
                if (seasonNumber == null || episodeNumber == null) {
                    throw IllegalArgumentException("Season and episode numbers are required for TV shows")
                }
                
                val episode = TraktEpisode(
                    season = seasonNumber,
                    number = episodeNumber,
                    title = null,
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = null,
                        tmdb = null
                    )
                )
                
                val show = TraktShow(
                    title = content.title,
                    year = content.releaseDate?.let { it.year + 1900 },
                    ids = com.strem.app.data.model.trakt.TraktIds(
                        trakt = null,
                        slug = null,
                        tvdb = null,
                        imdb = content.imdbId,
                        tmdb = contentId.toIntOrNull()
                    )
                )
                
                HistoryRequest(episodes = listOf(episode.copy(ids = episode.ids.copy(tmdb = null))))
            }
            else -> throw IllegalArgumentException("Invalid content type")
        }
        
        traktApi.addToHistory("Bearer $accessToken", request)
        
        // Update local playback progress sync status
        val progressId = if (contentType == "tv") {
            "$contentId-$seasonNumber-$episodeNumber"
        } else {
            contentId
        }
        
        playbackProgressDao.updateSyncStatus(progressId, true)
    }
    
    suspend fun syncWatchlist() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: return@withContext
        
        // Get unsynchronized watchlist items
        val unsyncedItems = watchlistDao.getUnsyncedItems()
        if (unsyncedItems.isEmpty()) return@withContext
        
        // Group by content type
        val contentIds = unsyncedItems.map { it.contentId }
        val contents = contentDao.getContentByIds(contentIds)
        
        val movies = mutableListOf<TraktMovie>()
        val shows = mutableListOf<TraktShow>()
        
        contents.forEach { content ->
            when (content.contentType) {
                "movie" -> {
                    movies.add(
                        TraktMovie(
                            title = content.title,
                            year = content.releaseDate?.let { it.year + 1900 },
                            ids = com.strem.app.data.model.trakt.TraktIds(
                                trakt = null,
                                slug = null,
                                tvdb = null,
                                imdb = content.imdbId,
                                tmdb = content.id.toIntOrNull()
                            )
                        )
                    )
                }
                "tv" -> {
                    shows.add(
                        TraktShow(
                            title = content.title,
                            year = content.releaseDate?.let { it.year + 1900 },
                            ids = com.strem.app.data.model.trakt.TraktIds(
                                trakt = null,
                                slug = null,
                                tvdb = null,
                                imdb = content.imdbId,
                                tmdb = content.id.toIntOrNull()
                            )
                        )
                    )
                }
            }
        }
        
        // Sync with Trakt
        if (movies.isNotEmpty() || shows.isNotEmpty()) {
            val request = WatchlistRequest(
                movies = if (movies.isNotEmpty()) movies else null,
                shows = if (shows.isNotEmpty()) shows else null
            )
            
            traktApi.addToWatchlist("Bearer $accessToken", request)
            
            // Update sync status
            unsyncedItems.forEach { item ->
                watchlistDao.updateSyncStatus(item.contentId, true)
            }
        }
    }
    
    suspend fun syncCollection() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: return@withContext
        
        // Get unsynchronized collection items
        val unsyncedItems = collectionDao.getUnsyncedItems()
        if (unsyncedItems.isEmpty()) return@withContext
        
        // Group by content type
        val contentIds = unsyncedItems.map { it.contentId }
        val contents = contentDao.getContentByIds(contentIds)
        
        val movies = mutableListOf<TraktMovie>()
        val shows = mutableListOf<TraktShow>()
        
        contents.forEach { content ->
            when (content.contentType) {
                "movie" -> {
                    movies.add(
                        TraktMovie(
                            title = content.title,
                            year = content.releaseDate?.let { it.year + 1900 },
                            ids = com.strem.app.data.model.trakt.TraktIds(
                                trakt = null,
                                slug = null,
                                tvdb = null,
                                imdb = content.imdbId,
                                tmdb = content.id.toIntOrNull()
                            )
                        )
                    )
                }
                "tv" -> {
                    shows.add(
                        TraktShow(
                            title = content.title,
                            year = content.releaseDate?.let { it.year + 1900 },
                            ids = com.strem.app.data.model.trakt.TraktIds(
                                trakt = null,
                                slug = null,
                                tvdb = null,
                                imdb = content.imdbId,
                                tmdb = content.id.toIntOrNull()
                            )
                        )
                    )
                }
            }
        }
        
        // Sync with Trakt
        if (movies.isNotEmpty() || shows.isNotEmpty()) {
            val request = CollectionRequest(
                movies = if (movies.isNotEmpty()) movies else null,
                shows = if (shows.isNotEmpty()) shows else null
            )
            
            traktApi.addToCollection("Bearer $accessToken", request)
            
            // Update sync status
            unsyncedItems.forEach { item ->
                collectionDao.updateSyncStatus(item.contentId, true)
            }
        }
    }
    
    suspend fun syncPlaybackProgress() = withContext(Dispatchers.IO) {
        val accessToken = preferencesManager.getTraktAccessToken() ?: return@withContext
        
        // Get unsynchronized playback progress
        val unsyncedProgress = playbackProgressDao.getUnsyncedProgress()
        if (unsyncedProgress.isEmpty()) return@withContext
        
        // Group by content type
        val contentIds = unsyncedProgress.map { it.contentId }.distinct()
        val contents = contentDao.getContentByIds(contentIds)
        val contentMap = contents.associateBy { it.id }
        
        val movies = mutableListOf<TraktMovie>()
        val episodes = mutableListOf<TraktEpisode>()
        
        unsyncedProgress.forEach { progress ->
            val content = contentMap[progress.contentId] ?: return@forEach
            
            when (content.contentType) {
                "movie" -> {
                    movies.add(
                        TraktMovie(
                            title = content.title,
                            year = content.releaseDate?.let { it.year + 1900 },
                            ids = com.strem.app.data.model.trakt.TraktIds(
                                trakt = null,
                                slug = null,
                                tvdb = null,
                                imdb = content.imdbId,
                                tmdb = content.id.toIntOrNull()
                            )
                        )
                    )
                }
                "tv" -> {
                    if (progress.seasonNumber != null && progress.episodeNumber != null) {
                        episodes.add(
                            TraktEpisode(
                                season = progress.seasonNumber,
                                number = progress.episodeNumber,
                                title = null,
                                ids = com.strem.app.data.model.trakt.TraktIds(
                                    trakt = null,
                                    slug = null,
                                    tvdb = null,
                                    imdb = null,
                                    tmdb = null
                                )
                            )
                        )
                    }
                }
            }
        }
        
        // Sync with Trakt
        if (movies.isNotEmpty() || episodes.isNotEmpty()) {
            val request = HistoryRequest(
                movies = if (movies.isNotEmpty()) movies else null,
                episodes = if (episodes.isNotEmpty()) episodes else null
            )
            
            traktApi.addToHistory("Bearer $accessToken", request)
            
            // Update sync status
            unsyncedProgress.forEach { progress ->
                playbackProgressDao.updateSyncStatus(progress.id, true)
            }
        }
    }
    
    suspend fun logout() = withContext(Dispatchers.IO) {
        preferencesManager.clearTraktAuth()
    }
}