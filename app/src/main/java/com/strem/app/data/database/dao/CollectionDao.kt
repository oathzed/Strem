package com.strem.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.strem.app.data.database.entity.CollectionEntity
import com.strem.app.data.database.entity.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionItem(collectionEntity: CollectionEntity)
    
    @Delete
    suspend fun removeCollectionItem(collectionEntity: CollectionEntity)
    
    @Query("DELETE FROM collection WHERE contentId = :contentId")
    suspend fun removeCollectionItemById(contentId: String)
    
    @Query("SELECT * FROM collection ORDER BY dateAdded DESC")
    fun getCollection(): Flow<List<CollectionEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM collection WHERE contentId = :contentId LIMIT 1)")
    suspend fun isInCollection(contentId: String): Boolean
    
    @Query("UPDATE collection SET syncedWithTrakt = :synced WHERE contentId = :contentId")
    suspend fun updateSyncStatus(contentId: String, synced: Boolean)
    
    @Query("SELECT * FROM collection WHERE syncedWithTrakt = 0")
    suspend fun getUnsyncedItems(): List<CollectionEntity>
    
    @Transaction
    @Query("SELECT c.* FROM content c INNER JOIN collection col ON c.id = col.contentId ORDER BY col.dateAdded DESC")
    fun getCollectionWithContent(): Flow<List<ContentEntity>>
}