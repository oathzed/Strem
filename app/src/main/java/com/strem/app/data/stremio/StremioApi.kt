package com.strem.app.data.stremio

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit interface for Stremio API.
 */
interface StremioApi {
    
    /**
     * Get addon manifest.
     */
    @GET
    suspend fun getManifest(@Url url: String): Response<StremioAddon>
    
    /**
     * Get catalog items.
     */
    @GET("catalog/{type}/{id}.json")
    suspend fun getCatalog(
        @Path("type") type: String,
        @Path("id") id: String,
        @Query("genre") genre: String? = null,
        @Query("skip") skip: Int? = null,
        @Query("search") search: String? = null
    ): Response<CatalogResponse>
    
    /**
     * Get meta information.
     */
    @GET("meta/{type}/{id}.json")
    suspend fun getMeta(
        @Path("type") type: String,
        @Path("id") id: String
    ): Response<MetaResponse>
    
    /**
     * Get streams.
     */
    @GET("stream/{type}/{id}.json")
    suspend fun getStreams(
        @Path("type") type: String,
        @Path("id") id: String
    ): Response<StreamResponse>
}

/**
 * Response class for catalog requests.
 */
data class CatalogResponse(
    val metas: List<MetaItem>
)

/**
 * Response class for meta requests.
 */
data class MetaResponse(
    val meta: MetaItem
)

/**
 * Response class for stream requests.
 */
data class StreamResponse(
    val streams: List<Stream>
)