package com.strem.app.data.api.trakt

import com.strem.app.data.model.trakt.AccessTokenResponse
import com.strem.app.data.model.trakt.CollectionRequest
import com.strem.app.data.model.trakt.HistoryRequest
import com.strem.app.data.model.trakt.TraktCollection
import com.strem.app.data.model.trakt.TraktHistory
import com.strem.app.data.model.trakt.TraktUser
import com.strem.app.data.model.trakt.TraktWatchlist
import com.strem.app.data.model.trakt.WatchlistRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TraktApi {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): AccessTokenResponse
    
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): AccessTokenResponse
    
    @GET("users/me")
    suspend fun getUser(
        @Header("Authorization") authorization: String
    ): TraktUser
    
    @GET("users/{username}/watchlist")
    suspend fun getWatchlist(
        @Path("username") username: String,
        @Header("Authorization") authorization: String,
        @Query("type") type: String? = null,
        @Query("sort") sort: String = "added",
        @Query("extended") extended: String = "full"
    ): List<TraktWatchlist>
    
    @POST("sync/watchlist")
    suspend fun addToWatchlist(
        @Header("Authorization") authorization: String,
        @Body request: WatchlistRequest
    ): Any
    
    @POST("sync/watchlist/remove")
    suspend fun removeFromWatchlist(
        @Header("Authorization") authorization: String,
        @Body request: WatchlistRequest
    ): Any
    
    @GET("users/{username}/collection")
    suspend fun getCollection(
        @Path("username") username: String,
        @Header("Authorization") authorization: String,
        @Query("type") type: String? = null,
        @Query("extended") extended: String = "full"
    ): List<TraktCollection>
    
    @POST("sync/collection")
    suspend fun addToCollection(
        @Header("Authorization") authorization: String,
        @Body request: CollectionRequest
    ): Any
    
    @POST("sync/collection/remove")
    suspend fun removeFromCollection(
        @Header("Authorization") authorization: String,
        @Body request: CollectionRequest
    ): Any
    
    @GET("sync/watched/{type}")
    suspend fun getWatched(
        @Path("type") type: String,
        @Header("Authorization") authorization: String,
        @Query("extended") extended: String = "full"
    ): List<Any>
    
    @GET("users/{username}/history")
    suspend fun getHistory(
        @Path("username") username: String,
        @Header("Authorization") authorization: String,
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("extended") extended: String = "full"
    ): List<TraktHistory>
    
    @POST("sync/history")
    suspend fun addToHistory(
        @Header("Authorization") authorization: String,
        @Body request: HistoryRequest
    ): Any
    
    @POST("sync/history/remove")
    suspend fun removeFromHistory(
        @Header("Authorization") authorization: String,
        @Body request: HistoryRequest
    ): Any
    
    fun setClientId(clientId: String) {
        this.clientId = clientId
    }
    
    fun setClientSecret(clientSecret: String) {
        this.clientSecret = clientSecret
    }
    
    companion object {
        private lateinit var clientId: String
        private lateinit var clientSecret: String
        const val REDIRECT_URI = "strem://auth"
    }
}