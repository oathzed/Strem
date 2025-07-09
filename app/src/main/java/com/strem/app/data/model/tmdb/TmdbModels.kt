package com.strem.app.data.model.tmdb

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TvResponse(
    val page: Int,
    val results: List<TvShow>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResponse(
    val page: Int,
    val results: List<SearchResult>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class TvShow(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class SearchResult(
    val id: Int,
    @SerializedName("media_type") val mediaType: String, // movie, tv, person
    val title: String?,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?
)

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    val genres: List<Genre>?,
    val runtime: Int?,
    val videos: Videos?,
    val credits: Credits?,
    val similar: MovieResponse?,
    @SerializedName("external_ids") val externalIds: ExternalIds?
)

data class TvDetailsResponse(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    val genres: List<Genre>?,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?,
    val videos: Videos?,
    val credits: Credits?,
    val similar: TvResponse?,
    val seasons: List<Season>?,
    @SerializedName("external_ids") val externalIds: ExternalIds?
)

data class TvSeasonResponse(
    val id: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    val episodes: List<Episode>?
)

data class Genre(
    val id: Int,
    val name: String
)

data class Videos(
    val results: List<Video>?
)

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

data class Credits(
    val cast: List<Cast>?,
    val crew: List<Crew>?
)

data class Cast(
    val id: Int,
    val name: String,
    val character: String?,
    @SerializedName("profile_path") val profilePath: String?
)

data class Crew(
    val id: Int,
    val name: String,
    val job: String?,
    @SerializedName("profile_path") val profilePath: String?
)

data class Season(
    val id: Int,
    val name: String,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("air_date") val airDate: String?
)

data class Episode(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    val runtime: Int?
)

data class ExternalIds(
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("tvdb_id") val tvdbId: Int?,
    @SerializedName("facebook_id") val facebookId: String?,
    @SerializedName("instagram_id") val instagramId: String?,
    @SerializedName("twitter_id") val twitterId: String?
)