package com.strem.app.data.api.stremio

import com.google.gson.Gson
import com.strem.app.data.model.stremio.StremioAddon
import com.strem.app.data.model.stremio.StremioStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class StremioApi(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    suspend fun getAddonManifest(url: String): StremioAddon? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext null
            }
            
            val responseBody = response.body?.string() ?: return@withContext null
            return@withContext gson.fromJson(responseBody, StremioAddon::class.java)
        } catch (e: IOException) {
            return@withContext null
        }
    }
    
    suspend fun getStreams(addonUrl: String, type: String, id: String): List<StremioStream> = withContext(Dispatchers.IO) {
        try {
            val streamUrl = if (addonUrl.endsWith("/")) {
                "${addonUrl}stream/$type/$id.json"
            } else {
                "$addonUrl/stream/$type/$id.json"
            }
            
            val request = Request.Builder()
                .url(streamUrl)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext emptyList()
            }
            
            val responseBody = response.body?.string() ?: return@withContext emptyList()
            val streamResponse = gson.fromJson(responseBody, StremioStreamResponse::class.java)
            return@withContext streamResponse.streams ?: emptyList()
        } catch (e: IOException) {
            return@withContext emptyList()
        }
    }
    
    private data class StremioStreamResponse(
        val streams: List<StremioStream>?
    )
}