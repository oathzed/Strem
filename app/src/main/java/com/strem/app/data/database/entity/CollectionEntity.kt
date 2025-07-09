package com.strem.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "collection")
data class CollectionEntity(
    @PrimaryKey
    val contentId: String,
    val dateAdded: Date = Date(),
    val syncedWithTrakt: Boolean = false
)