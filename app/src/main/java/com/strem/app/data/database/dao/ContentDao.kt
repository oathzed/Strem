package com.strem.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.strem.app.data.database.entity.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContent(content: List<ContentEntity>)
    
    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContentById(id: String): ContentEntity?
    
    @Query("SELECT * FROM content WHERE imdbId = :imdbId")
    suspend fun getContentByImdbId(imdbId: String): ContentEntity?
    
    @Query("SELECT * FROM content WHERE id IN (:ids)")
    suspend fun getContentByIds(ids: List<String>): List<ContentEntity>
    
    @Query("SELECT * FROM content WHERE contentType = :type ORDER BY lastUpdated DESC LIMIT :limit")
    suspend fun getRecentContent(type: String, limit: Int): List<ContentEntity>
    
    @Query("SELECT * FROM content WHERE title LIKE '%' || :query || '%'")
    suspend fun searchContent(query: String): List<ContentEntity>
}