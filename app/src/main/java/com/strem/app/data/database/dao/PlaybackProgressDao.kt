package com.strem.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.database.entity.PlaybackProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaybackProgress(playbackProgressEntity: PlaybackProgressEntity)
    
    @Query("SELECT * FROM playback_progress WHERE id = :id")
    suspend fun getPlaybackProgressById(id: String): PlaybackProgressEntity?
    
    @Query("SELECT * FROM playback_progress WHERE contentId = :contentId AND (seasonNumber IS NULL OR seasonNumber = :seasonNumber) AND (episodeNumber IS NULL OR episodeNumber = :episodeNumber)")
    suspend fun getPlaybackProgress(contentId: String, seasonNumber: Int?, episodeNumber: Int?): PlaybackProgressEntity?
    
    @Query("SELECT * FROM playback_progress ORDER BY lastPlayed DESC LIMIT :limit")
    suspend fun getRecentPlaybackProgress(limit: Int): List<PlaybackProgressEntity>
    
    @Query("UPDATE playback_progress SET syncedWithTrakt = :synced WHERE id = :id")
    suspend fun updateSyncStatus(id: String, synced: Boolean)
    
    @Query("SELECT * FROM playback_progress WHERE syncedWithTrakt = 0")
    suspend fun getUnsyncedProgress(): List<PlaybackProgressEntity>
    
    @Transaction
    @Query("SELECT c.* FROM content c INNER JOIN playback_progress p ON c.id = p.contentId ORDER BY p.lastPlayed DESC LIMIT :limit")
    suspend fun getContinueWatchingContent(limit: Int): List<ContentEntity>
    
    @Transaction
    @Query("SELECT c.* FROM content c INNER JOIN playback_progress p ON c.id = p.contentId ORDER BY p.lastPlayed DESC")
    fun getContinueWatchingFlow(): Flow<List<ContentEntity>>
}