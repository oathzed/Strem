package com.strem.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "content")
data class ContentEntity(
    @PrimaryKey
    val id: String, // TMDB ID
    val imdbId: String?,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: Date?,
    val voteAverage: Float?,
    val contentType: String, // "movie" or "tv"
    val genres: List<String>,
    val lastUpdated: Date = Date()
)