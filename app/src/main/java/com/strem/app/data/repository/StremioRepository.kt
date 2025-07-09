package com.strem.app.data.repository

import com.strem.app.data.api.stremio.StremioApi
import com.strem.app.data.database.dao.StremioAddonDao
import com.strem.app.data.database.entity.StremioAddonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class StremioRepository(
    private val stremioApi: StremioApi,
    private val stremioAddonDao: StremioAddonDao
) {
    suspend fun addAddon(url: String) = withContext(Dispatchers.IO) {
        val manifest = stremioApi.getAddonManifest(url) ?: throw IllegalArgumentException("Invalid addon URL")
        
        val addonEntity = StremioAddonEntity(
            id = manifest.id,
            name = manifest.name,
            url = url,
            description = manifest.description,
            version = manifest.version,
            catalogs = manifest.catalogs?.map { it.id },
            resources = manifest.resources,
            types = manifest.types,
            dateAdded = Date()
        )
        
        stremioAddonDao.insertAddon(addonEntity)
        addonEntity
    }
    
    suspend fun removeAddon(id: String) = withContext(Dispatchers.IO) {
        stremioAddonDao.removeAddon(id)
    }
    
    fun getAllAddons(): Flow<List<StremioAddonEntity>> {
        return stremioAddonDao.getAllAddons()
    }
    
    fun getStreamingAddons(): Flow<List<StremioAddonEntity>> {
        return stremioAddonDao.getStreamingAddons()
    }
    
    suspend fun getStreams(contentId: String, contentType: String, imdbId: String?) = withContext(Dispatchers.IO) {
        val addons = stremioAddonDao.getStreamingAddons().first()
        val streams = mutableListOf<com.strem.app.data.model.stremio.StremioStream>()
        
        // If we have an IMDB ID, use it
        val id = imdbId ?: contentId
        val type = if (contentType == "movie") "movie" else "series"
        
        addons.forEach { addon ->
            val addonStreams = stremioApi.getStreams(addon.url, type, id)
            streams.addAll(addonStreams)
        }
        
        streams
    }
}