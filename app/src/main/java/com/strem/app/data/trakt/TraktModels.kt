package com.strem.app.data.trakt

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

/**
 * Data class representing a Trakt OAuth token.
 */
data class TraktToken(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("created_at")
    val createdAt: Long
) : Serializable

/**
 * Data class representing a Trakt user.
 */
data class TraktUser(
    val username: String,
    @SerializedName("private")
    val isPrivate: Boolean,
    val name: String,
    val vip: Boolean,
    @SerializedName("vip_ep")
    val vipEp: Boolean,
    val ids: TraktUserIds,
    @SerializedName("joined_at")
    val joinedAt: Date,
    val location: String?,
    val about: String?,
    val gender: String?,
    val age: Int?,
    val images: TraktUserImages
) : Serializable

/**
 * Data class representing Trakt user IDs.
 */
data class TraktUserIds(
    val slug: String,
    val uuid: String
) : Serializable

/**
 * Data class representing Trakt user images.
 */
data class TraktUserImages(
    val avatar: TraktUserAvatar
) : Serializable

/**
 * Data class representing a Trakt user avatar.
 */
data class TraktUserAvatar(
    val full: String
) : Serializable

/**
 * Data class representing a Trakt list.
 */
data class TraktList(
    val name: String,
    val description: String,
    @SerializedName("privacy")
    val privacyType: String,
    @SerializedName("display_numbers")
    val displayNumbers: Boolean,
    @SerializedName("allow_comments")
    val allowComments: Boolean,
    @SerializedName("sort_by")
    val sortBy: String,
    @SerializedName("sort_how")
    val sortHow: String,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    val likes: Int,
    val ids: TraktListIds
) : Serializable

/**
 * Data class representing Trakt list IDs.
 */
data class TraktListIds(
    val trakt: Int,
    val slug: String
) : Serializable

/**
 * Data class representing a Trakt list item.
 */
data class TraktListItem(
    val rank: Int,
    val id: Int,
    @SerializedName("listed_at")
    val listedAt: Date,
    val type: String,
    val movie: TraktMovie?,
    val show: TraktShow?,
    val person: TraktPerson?,
    val notes: String?
) : Serializable

/**
 * Data class representing a Trakt movie.
 */
data class TraktMovie(
    val title: String,
    val year: Int,
    val ids: TraktMovieIds,
    val tagline: String?,
    val overview: String?,
    val released: Date?,
    val runtime: Int?,
    val country: String?,
    val trailer: String?,
    val homepage: String?,
    val rating: Float?,
    val votes: Int?,
    @SerializedName("updated_at")
    val updatedAt: Date?,
    val language: String?,
    val genres: List<String>?,
    val certification: String?
) : Serializable

/**
 * Data class representing Trakt movie IDs.
 */
data class TraktMovieIds(
    val trakt: Int,
    val slug: String,
    val imdb: String?,
    val tmdb: Int?
) : Serializable

/**
 * Data class representing a Trakt show.
 */
data class TraktShow(
    val title: String,
    val year: Int,
    val ids: TraktShowIds,
    val overview: String?,
    @SerializedName("first_aired")
    val firstAired: Date?,
    val airs: TraktAirs?,
    val runtime: Int?,
    val certification: String?,
    val network: String?,
    val country: String?,
    val trailer: String?,
    val homepage: String?,
    val status: String?,
    val rating: Float?,
    val votes: Int?,
    @SerializedName("updated_at")
    val updatedAt: Date?,
    val language: String?,
    val genres: List<String>?,
    @SerializedName("aired_episodes")
    val airedEpisodes: Int?
) : Serializable

/**
 * Data class representing Trakt show IDs.
 */
data class TraktShowIds(
    val trakt: Int,
    val slug: String,
    val tvdb: Int?,
    val imdb: String?,
    val tmdb: Int?
) : Serializable

/**
 * Data class representing Trakt show air times.
 */
data class TraktAirs(
    val day: String,
    val time: String,
    val timezone: String
) : Serializable

/**
 * Data class representing a Trakt person.
 */
data class TraktPerson(
    val name: String,
    val ids: TraktPersonIds,
    val biography: String?,
    val birthday: Date?,
    val death: Date?,
    val birthplace: String?,
    val homepage: String?
) : Serializable

/**
 * Data class representing Trakt person IDs.
 */
data class TraktPersonIds(
    val trakt: Int,
    val slug: String,
    val imdb: String?,
    val tmdb: Int?
) : Serializable

/**
 * Data class representing a Trakt watched item.
 */
data class TraktWatchedItem(
    @SerializedName("watched_at")
    val watchedAt: Date,
    val action: String,
    val type: String,
    val movie: TraktMovie?,
    val show: TraktShow?,
    val episode: TraktEpisode?
) : Serializable

/**
 * Data class representing a Trakt episode.
 */
data class TraktEpisode(
    val season: Int,
    val number: Int,
    val title: String,
    val ids: TraktEpisodeIds,
    val overview: String?,
    @SerializedName("first_aired")
    val firstAired: Date?,
    val runtime: Int?
) : Serializable

/**
 * Data class representing Trakt episode IDs.
 */
data class TraktEpisodeIds(
    val trakt: Int,
    val tvdb: Int?,
    val imdb: String?,
    val tmdb: Int?
) : Serializable

/**
 * Data class representing a Trakt playback item.
 */
data class TraktPlaybackItem(
    val id: Long,
    @SerializedName("progress")
    val progressPercent: Double,
    @SerializedName("paused_at")
    val pausedAt: Date,
    val type: String,
    val movie: TraktMovie?,
    val show: TraktShow?,
    val episode: TraktEpisode?
) : Serializable

/**
 * Data class representing a Trakt sync request.
 */
data class TraktSyncRequest(
    val movies: List<TraktSyncMovie>? = null,
    val shows: List<TraktSyncShow>? = null,
    val episodes: List<TraktSyncEpisode>? = null
) : Serializable

/**
 * Data class representing a Trakt sync movie.
 */
data class TraktSyncMovie(
    val ids: TraktSyncIds,
    @SerializedName("watched_at")
    val watchedAt: String? = null,
    @SerializedName("collected_at")
    val collectedAt: String? = null,
    val progress: Double? = null
) : Serializable

/**
 * Data class representing a Trakt sync show.
 */
data class TraktSyncShow(
    val ids: TraktSyncIds,
    @SerializedName("watched_at")
    val watchedAt: String? = null,
    @SerializedName("collected_at")
    val collectedAt: String? = null
) : Serializable

/**
 * Data class representing a Trakt sync episode.
 */
data class TraktSyncEpisode(
    val ids: TraktSyncIds,
    @SerializedName("watched_at")
    val watchedAt: String? = null,
    @SerializedName("collected_at")
    val collectedAt: String? = null,
    val progress: Double? = null
) : Serializable

/**
 * Data class representing Trakt sync IDs.
 */
data class TraktSyncIds(
    val trakt: Int? = null,
    val imdb: String? = null,
    val tmdb: Int? = null,
    val tvdb: Int? = null
) : Serializable

/**
 * Data class representing a Trakt sync response.
 */
data class TraktSyncResponse(
    val added: TraktSyncStats,
    val existing: TraktSyncStats,
    @SerializedName("not_found")
    val notFound: TraktSyncNotFound
) : Serializable

/**
 * Data class representing Trakt sync stats.
 */
data class TraktSyncStats(
    val movies: Int,
    val shows: Int,
    val seasons: Int,
    val episodes: Int
) : Serializable

/**
 * Data class representing Trakt sync not found items.
 */
data class TraktSyncNotFound(
    val movies: List<TraktSyncIds>,
    val shows: List<TraktSyncIds>,
    val seasons: List<TraktSyncIds>,
    val episodes: List<TraktSyncIds>
) : Serializable