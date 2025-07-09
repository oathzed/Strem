package com.strem.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.database.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(watchlistEntity: WatchlistEntity)
    
    @Delete
    suspend fun removeWatchlistItem(watchlistEntity: WatchlistEntity)
    
    @Query("DELETE FROM watchlist WHERE contentId = :contentId")
    suspend fun removeWatchlistItemById(contentId: String)
    
    @Query("SELECT * FROM watchlist ORDER BY dateAdded DESC")
    fun getWatchlist(): Flow<List<WatchlistEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE contentId = :contentId LIMIT 1)")
    suspend fun isInWatchlist(contentId: String): Boolean
    
    @Query("UPDATE watchlist SET syncedWithTrakt = :synced WHERE contentId = :contentId")
    suspend fun updateSyncStatus(contentId: String, synced: Boolean)
    
    @Query("SELECT * FROM watchlist WHERE syncedWithTrakt = 0")
    suspend fun getUnsyncedItems(): List<WatchlistEntity>
    
    @Transaction
    @Query("SELECT c.* FROM content c INNER JOIN watchlist w ON c.id = w.contentId ORDER BY w.dateAdded DESC")
    fun getWatchlistWithContent(): Flow<List<ContentEntity>>
}