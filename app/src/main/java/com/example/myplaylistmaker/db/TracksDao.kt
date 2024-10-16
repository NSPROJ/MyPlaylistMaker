package com.example.myplaylistmaker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TracksEntity)

    @Delete
    suspend fun deleteTrack(track: TracksEntity)

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracks(): List<TracksEntity>

    @Query("SELECT trackId FROM tracks_table")
    suspend fun getTrackIds(): List<Long>
}