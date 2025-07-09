package com.strem.app.data.model.trakt

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    val scope: String,
    @SerializedName("created_at") val createdAt: Long
)

data class TraktUser(
    val username: String,
    @SerializedName("private") val isPrivate: Boolean,
    val name: String?,
    val vip: Boolean?,
    @SerializedName("joined_at") val joinedAt: String?,
    val location: String?,
    val about: String?,
    val gender: String?,
    val age: Int?,
    val images: TraktImages?
)

data class TraktImages(
    val avatar: TraktImage?,
    val banner: TraktImage?
)

data class TraktImage(
    val full: String?
)

data class TraktWatchlist(
    val id: Long,
    @SerializedName("listed_at") val listedAt: String,
    val type: String,
    val movie: TraktMovie?,
    val show: TraktShow?,
    val season: TraktSeason?,
    val episode: TraktEpisode?
)

data class TraktCollection(
    val id: Long,
    @SerializedName("collected_at") val collectedAt: String,
    val movie: TraktMovie?,
    val show: TraktShow?
)

data class TraktHistory(
    val id: Long,
    @SerializedName("watched_at") val watchedAt: String,
    val action: String,
    val type: String,
    val movie: TraktMovie?,
    val show: TraktShow?,
    val episode: TraktEpisode?
)

data class TraktMovie(
    val title: String,
    val year: Int?,
    val ids: TraktIds
)

data class TraktShow(
    val title: String,
    val year: Int?,
    val ids: TraktIds
)

data class TraktSeason(
    val number: Int,
    val ids: TraktIds
)

data class TraktEpisode(
    val season: Int,
    val number: Int,
    val title: String?,
    val ids: TraktIds
)

data class TraktIds(
    val trakt: Int?,
    val slug: String?,
    val tvdb: Int?,
    val imdb: String?,
    val tmdb: Int?
)

data class WatchlistRequest(
    val movies: List<TraktMovie>? = null,
    val shows: List<TraktShow>? = null,
    val seasons: List<TraktSeason>? = null,
    val episodes: List<TraktEpisode>? = null
)

data class CollectionRequest(
    val movies: List<TraktMovie>? = null,
    val shows: List<TraktShow>? = null,
    val seasons: List<TraktSeason>? = null,
    val episodes: List<TraktEpisode>? = null
)

data class HistoryRequest(
    val movies: List<TraktMovie>? = null,
    val shows: List<TraktShow>? = null,
    val episodes: List<TraktEpisode>? = null
)