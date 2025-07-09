package com.strem.app.data.repository

import com.strem.app.data.database.dao.PlaybackProgressDao
import com.strem.app.data.database.entity.PlaybackProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class PlaybackRepository(
    private val playbackProgressDao: PlaybackProgressDao
) {
    suspend fun savePlaybackProgress(
        contentId: String,
        seasonNumber: Int?,
        episodeNumber: Int?,
        position: Long,
        duration: Long
    ) = withContext(Dispatchers.IO) {
        val id = if (seasonNumber != null && episodeNumber != null) {
            "$contentId-$seasonNumber-$episodeNumber"
        } else {
            contentId
        }
        
        val playbackProgressEntity = PlaybackProgressEntity(
            id = id,
            contentId = contentId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            position = position,
            duration = duration,
            lastPlayed = Date(),
            syncedWithTrakt = false
        )
        
        playbackProgressDao.insertPlaybackProgress(playbackProgressEntity)
    }
    
    suspend fun getPlaybackProgress(
        contentId: String,
        seasonNumber: Int?,
        episodeNumber: Int?
    ) = withContext(Dispatchers.IO) {
        playbackProgressDao.getPlaybackProgress(contentId, seasonNumber, episodeNumber)
    }
    
    suspend fun getContinueWatchingContent(limit: Int) = withContext(Dispatchers.IO) {
        playbackProgressDao.getContinueWatchingContent(limit)
    }
    
    fun getContinueWatchingFlow(): Flow<List<com.strem.app.data.database.entity.ContentEntity>> {
        return playbackProgressDao.getContinueWatchingFlow()
    }
}