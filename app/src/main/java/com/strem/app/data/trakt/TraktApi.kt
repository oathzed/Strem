package com.strem.app.data.trakt

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for Trakt API.
 */
interface TraktApi {
    
    /**
     * Get OAuth token.
     */
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Response<TraktToken>
    
    /**
     * Refresh OAuth token.
     */
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Response<TraktToken>
    
    /**
     * Get user profile.
     */
    @GET("users/me")
    suspend fun getUser(
        @Header("Authorization") authorization: String
    ): Response<TraktUser>
    
    /**
     * Get user's lists.
     */
    @GET("users/me/lists")
    suspend fun getLists(
        @Header("Authorization") authorization: String
    ): Response<List<TraktList>>
    
    /**
     * Get items in a list.
     */
    @GET("users/me/lists/{list_id}/items")
    suspend fun getListItems(
        @Header("Authorization") authorization: String,
        @Path("list_id") listId: String,
        @Query("type") type: String? = null
    ): Response<List<TraktListItem>>
    
    /**
     * Get user's watched history.
     */
    @GET("sync/watched/{type}")
    suspend fun getWatchedHistory(
        @Header("Authorization") authorization: String,
        @Path("type") type: String,
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null
    ): Response<List<TraktWatchedItem>>
    
    /**
     * Get user's watching progress.
     */
    @GET("sync/playback/{type}")
    suspend fun getPlaybackProgress(
        @Header("Authorization") authorization: String,
        @Path("type") type: String,
        @Query("limit") limit: Int? = null
    ): Response<List<TraktPlaybackItem>>
    
    /**
     * Add item to history.
     */
    @POST("sync/history")
    suspend fun addToHistory(
        @Header("Authorization") authorization: String,
        @Body body: TraktSyncRequest
    ): Response<TraktSyncResponse>
    
    /**
     * Add item to watchlist.
     */
    @POST("sync/watchlist")
    suspend fun addToWatchlist(
        @Header("Authorization") authorization: String,
        @Body body: TraktSyncRequest
    ): Response<TraktSyncResponse>
}