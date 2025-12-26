package com.example.animejikan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_table")
data class AnimeEntity(
    @PrimaryKey val malId: Int,
    val title: String,
    val imageUrl: String,
    val score: Double?,
    val episodes: Int?,
    val synopsis: String?,
    val trailerUrl: String?,
    val trailerImageUrl: String?,
    val youTubeVideoId: String?,
    val genres: String? // Storing as comma-separated string for simplicity
)
