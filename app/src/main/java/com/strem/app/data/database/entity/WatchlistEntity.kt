package com.strem.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val contentId: String,
    val dateAdded: Date = Date(),
    val syncedWithTrakt: Boolean = false
)