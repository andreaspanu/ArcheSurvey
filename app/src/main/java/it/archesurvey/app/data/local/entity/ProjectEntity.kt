package it.archesurvey.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val client: String,
    val location: String,
    val notes: String,
    val createdAtMillis: Long?
)
