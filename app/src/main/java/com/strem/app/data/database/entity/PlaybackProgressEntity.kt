package com.strem.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "playback_progress")
data class PlaybackProgressEntity(
    @PrimaryKey
    val id: String, // contentId + seasonNumber + episodeNumber
    val contentId: String,
    val seasonNumber: Int?, // null for movies
    val episodeNumber: Int?, // null for movies
    val position: Long, // in milliseconds
    val duration: Long, // in milliseconds
    val lastPlayed: Date = Date(),
    val syncedWithTrakt: Boolean = false
)