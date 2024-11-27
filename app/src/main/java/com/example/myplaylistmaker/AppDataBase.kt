package com.example.myplaylistmaker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myplaylistmaker.db.PlaylistTracksDao
import com.example.myplaylistmaker.db.PlaylistTracksEntity
import com.example.myplaylistmaker.db.PlaylistsEntity
import com.example.myplaylistmaker.db.PlaylistsDao
import com.example.myplaylistmaker.db.TracksDao
import com.example.myplaylistmaker.db.TracksEntity

@Database(
    entities = [TracksEntity::class, PlaylistsEntity::class, PlaylistTracksEntity::class],
    version = 5
)

abstract class AppDataBase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}