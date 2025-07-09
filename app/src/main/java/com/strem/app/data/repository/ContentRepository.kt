package com.strem.app.data.repository

import com.strem.app.data.api.tmdb.TmdbApi
import com.strem.app.data.database.dao.ContentDao
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.model.tmdb.Movie
import com.strem.app.data.model.tmdb.SearchResult
import com.strem.app.data.model.tmdb.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContentRepository(
    private val tmdbApi: TmdbApi,
    private val contentDao: ContentDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    suspend fun getPopularMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getPopularMovies(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheMovies(response.results)
        response
    }
    
    suspend fun getTopRatedMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getTopRatedMovies(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheMovies(response.results)
        response
    }
    
    suspend fun getNowPlayingMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getNowPlayingMovies(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheMovies(response.results)
        response
    }
    
    suspend fun getPopularTvShows(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getPopularTvShows(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheTvShows(response.results)
        response
    }
    
    suspend fun getTopRatedTvShows(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getTopRatedTvShows(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheTvShows(response.results)
        response
    }
    
    suspend fun getOnTheAirTvShows(page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getOnTheAirTvShows(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, page)
        cacheTvShows(response.results)
        response
    }
    
    suspend fun getMovieDetails(movieId: Int) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getMovieDetails(movieId, tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String)
        
        // Cache movie details
        val genres = response.genres?.map { it.name } ?: emptyList()
        val releaseDate = response.releaseDate?.let { parseDate(it) }
        
        val contentEntity = ContentEntity(
            id = response.id.toString(),
            imdbId = response.externalIds?.imdbId,
            title = response.title,
            overview = response.overview,
            posterPath = response.posterPath,
            backdropPath = response.backdropPath,
            releaseDate = releaseDate,
            voteAverage = response.voteAverage,
            contentType = "movie",
            genres = genres
        )
        
        contentDao.insertContent(contentEntity)
        response
    }
    
    suspend fun getTvDetails(tvId: Int) = withContext(Dispatchers.IO) {
        val response = tmdbApi.getTvDetails(tvId, tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String)
        
        // Cache TV details
        val genres = response.genres?.map { it.name } ?: emptyList()
        val firstAirDate = response.firstAirDate?.let { parseDate(it) }
        
        val contentEntity = ContentEntity(
            id = response.id.toString(),
            imdbId = response.externalIds?.imdbId,
            title = response.name,
            overview = response.overview,
            posterPath = response.posterPath,
            backdropPath = response.backdropPath,
            releaseDate = firstAirDate,
            voteAverage = response.voteAverage,
            contentType = "tv",
            genres = genres
        )
        
        contentDao.insertContent(contentEntity)
        response
    }
    
    suspend fun getTvSeason(tvId: Int, seasonNumber: Int) = withContext(Dispatchers.IO) {
        tmdbApi.getTvSeason(tvId, seasonNumber, tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String)
    }
    
    suspend fun searchMulti(query: String, page: Int = 1) = withContext(Dispatchers.IO) {
        val response = tmdbApi.searchMulti(tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, query, page)
        cacheSearchResults(response.results)
        response
    }
    
    suspend fun findByImdbId(imdbId: String) = withContext(Dispatchers.IO) {
        tmdbApi.findByExternalId(imdbId, tmdbApi.javaClass.getDeclaredField("apiKey").get(null) as String, "imdb_id")
    }
    
    private suspend fun cacheMovies(movies: List<Movie>) {
        val contentEntities = movies.map { movie ->
            val releaseDate = movie.releaseDate?.let { parseDate(it) }
            ContentEntity(
                id = movie.id.toString(),
                imdbId = null, // We don't have IMDB ID from this endpoint
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                releaseDate = releaseDate,
                voteAverage = movie.voteAverage,
                contentType = "movie",
                genres = emptyList() // We don't have genres from this endpoint
            )
        }
        contentDao.insertAllContent(contentEntities)
    }
    
    private suspend fun cacheTvShows(tvShows: List<TvShow>) {
        val contentEntities = tvShows.map { tvShow ->
            val firstAirDate = tvShow.firstAirDate?.let { parseDate(it) }
            ContentEntity(
                id = tvShow.id.toString(),
                imdbId = null, // We don't have IMDB ID from this endpoint
                title = tvShow.name,
                overview = tvShow.overview,
                posterPath = tvShow.posterPath,
                backdropPath = tvShow.backdropPath,
                releaseDate = firstAirDate,
                voteAverage = tvShow.voteAverage,
                contentType = "tv",
                genres = emptyList() // We don't have genres from this endpoint
            )
        }
        contentDao.insertAllContent(contentEntities)
    }
    
    private suspend fun cacheSearchResults(searchResults: List<SearchResult>) {
        val contentEntities = searchResults.mapNotNull { result ->
            if (result.mediaType != "person") {
                val isMovie = result.mediaType == "movie"
                val title = if (isMovie) result.title ?: return@mapNotNull null else result.name ?: return@mapNotNull null
                val releaseDate = if (isMovie) {
                    result.releaseDate?.let { parseDate(it) }
                } else {
                    result.firstAirDate?.let { parseDate(it) }
                }
                
                ContentEntity(
                    id = result.id.toString(),
                    imdbId = null, // We don't have IMDB ID from this endpoint
                    title = title,
                    overview = result.overview,
                    posterPath = result.posterPath,
                    backdropPath = result.backdropPath,
                    releaseDate = releaseDate,
                    voteAverage = result.voteAverage,
                    contentType = result.mediaType,
                    genres = emptyList() // We don't have genres from this endpoint
                )
            } else null
        }
        contentDao.insertAllContent(contentEntities)
    }
    
    private fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}