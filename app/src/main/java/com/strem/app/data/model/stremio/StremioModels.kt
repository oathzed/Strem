package com.strem.app.data.model.stremio

import com.google.gson.annotations.SerializedName

data class StremioAddon(
    val id: String,
    val name: String,
    val version: String?,
    val description: String?,
    val resources: List<String>?,
    val types: List<String>?,
    val catalogs: List<StremioCatalog>?
)

data class StremioCatalog(
    val type: String,
    val id: String,
    val name: String
)

data class StremioStream(
    val name: String?,
    val title: String?,
    val url: String,
    @SerializedName("behaviorHints") val behaviorHints: BehaviorHints?,
    val description: String?,
    val subtitles: List<StremioSubtitle>?
)

data class BehaviorHints(
    @SerializedName("bingeGroup") val bingeGroup: String?,
    @SerializedName("notWebReady") val notWebReady: Boolean?,
    @SerializedName("proxyHeaders") val proxyHeaders: Map<String, String>?,
    @SerializedName("headers") val headers: Map<String, String>?
)

data class StremioSubtitle(
    val url: String,
    val lang: String,
    val id: String?
)