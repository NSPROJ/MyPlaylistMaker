package com.example.myplaylistmaker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PlaylistTracksDao {

    @Insert(entity = PlaylistTracksEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun  insertPlaylistTracks(playlistTracksEntity: PlaylistTracksEntity)

}