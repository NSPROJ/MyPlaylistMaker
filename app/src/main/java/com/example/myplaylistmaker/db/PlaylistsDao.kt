package com.example.myplaylistmaker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistsDao {

    @Insert(entity = PlaylistsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistsEntity)

    @Query("SELECT * FROM playlists_table ORDER BY playlistId DESC")
    suspend fun getPlaylists(): List<PlaylistsEntity>

    @Delete(entity = PlaylistsEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistsEntity)

    @Update(entity = PlaylistsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistsEntity)
}