package com.example.myplaylistmaker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistsEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val description: String,
    val path: String,
    val trackId: String,
    val count: Int,
)