package com.strem.app.data.stremio

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class representing a Stremio addon.
 */
data class StremioAddon(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val resources: List<String>,
    val types: List<String>,
    val catalogs: List<Catalog>,
    val background: String? = null,
    val logo: String? = null,
    val contactEmail: String? = null,
    val endpoint: String
) : Serializable

/**
 * Data class representing a Stremio catalog.
 */
data class Catalog(
    val type: String,
    val id: String,
    val name: String,
    val extra: List<Extra>? = null
) : Serializable

/**
 * Data class representing extra parameters for a Stremio catalog.
 */
data class Extra(
    val name: String,
    val isRequired: Boolean,
    val options: List<String>? = null,
    val optionsLimit: Int? = null
) : Serializable

/**
 * Data class representing a Stremio meta item.
 */
data class MetaItem(
    val id: String,
    val type: String,
    val name: String,
    val poster: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val runtime: String? = null,
    val genres: List<String>? = null,
    val imdbRating: String? = null,
    val videos: List<Video>? = null
) : Serializable

/**
 * Data class representing a video in a Stremio meta item.
 */
data class Video(
    val id: String,
    val title: String,
    val released: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val overview: String? = null,
    val thumbnail: String? = null
) : Serializable

/**
 * Data class representing a Stremio stream.
 */
data class Stream(
    val url: String,
    val title: String? = null,
    val name: String? = null,
    @SerializedName("behaviorHints")
    val behaviorHints: BehaviorHints? = null
) : Serializable

/**
 * Data class representing behavior hints for a Stremio stream.
 */
data class BehaviorHints(
    @SerializedName("notWebReady")
    val notWebReady: Boolean? = null,
    @SerializedName("bingeGroup")
    val bingeGroup: String? = null
) : Serializable