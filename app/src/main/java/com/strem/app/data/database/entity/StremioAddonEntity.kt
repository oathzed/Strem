package com.strem.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "stremio_addons")
data class StremioAddonEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val url: String,
    val description: String?,
    val version: String?,
    val catalogs: List<String>?,
    val resources: List<String>?,
    val types: List<String>?,
    val dateAdded: Date = Date()
)