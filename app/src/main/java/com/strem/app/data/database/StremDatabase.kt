package com.strem.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.strem.app.data.database.converter.DateConverter
import com.strem.app.data.database.converter.ListConverter
import com.strem.app.data.database.dao.CollectionDao
import com.strem.app.data.database.dao.ContentDao
import com.strem.app.data.database.dao.PlaybackProgressDao
import com.strem.app.data.database.dao.StremioAddonDao
import com.strem.app.data.database.dao.WatchlistDao
import com.strem.app.data.database.entity.CollectionEntity
import com.strem.app.data.database.entity.ContentEntity
import com.strem.app.data.database.entity.PlaybackProgressEntity
import com.strem.app.data.database.entity.StremioAddonEntity
import com.strem.app.data.database.entity.WatchlistEntity

@Database(
    entities = [
        ContentEntity::class,
        WatchlistEntity::class,
        CollectionEntity::class,
        PlaybackProgressEntity::class,
        StremioAddonEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class StremDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun collectionDao(): CollectionDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun stremioAddonDao(): StremioAddonDao
}