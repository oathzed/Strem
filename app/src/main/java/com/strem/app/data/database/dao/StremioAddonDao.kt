package com.strem.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.strem.app.data.database.entity.StremioAddonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StremioAddonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddon(stremioAddonEntity: StremioAddonEntity)
    
    @Query("DELETE FROM stremio_addons WHERE id = :id")
    suspend fun removeAddon(id: String)
    
    @Query("SELECT * FROM stremio_addons ORDER BY dateAdded DESC")
    fun getAllAddons(): Flow<List<StremioAddonEntity>>
    
    @Query("SELECT * FROM stremio_addons WHERE resources LIKE '%stream%' ORDER BY dateAdded DESC")
    fun getStreamingAddons(): Flow<List<StremioAddonEntity>>
    
    @Query("SELECT * FROM stremio_addons WHERE resources LIKE '%meta%' ORDER BY dateAdded DESC")
    fun getMetadataAddons(): Flow<List<StremioAddonEntity>>
    
    @Query("SELECT * FROM stremio_addons WHERE resources LIKE '%subtitle%' ORDER BY dateAdded DESC")
    fun getSubtitleAddons(): Flow<List<StremioAddonEntity>>
}