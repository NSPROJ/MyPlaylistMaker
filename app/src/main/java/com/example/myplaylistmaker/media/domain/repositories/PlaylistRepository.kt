package com.example.myplaylistmaker.media.domain.repositories

import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun getPlaylist(): Flow<List<Playlist>>

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(track: Track, playlist: Playlist)

    suspend fun insertTrack(track: Track)
}
