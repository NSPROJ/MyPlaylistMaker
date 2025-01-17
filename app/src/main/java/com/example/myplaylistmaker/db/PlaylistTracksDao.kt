package com.example.myplaylistmaker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTracksDao {

    @Insert(entity = PlaylistTracksEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTracks(playlistTracksEntity: PlaylistTracksEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<PlaylistTracksEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(trackId: Long)
}