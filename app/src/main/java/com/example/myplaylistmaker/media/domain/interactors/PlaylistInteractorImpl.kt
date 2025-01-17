package com.example.myplaylistmaker.media.domain.interactors

import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.repositories.PlaylistRepository
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun getPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylist()
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        return playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist) {
        return playlistRepository.updatePlaylist(track, playlist)
    }

    override suspend fun insertTrack(track: Track) {
        return playlistRepository.insertTrack(track)
    }

    override suspend fun getTracksForPlaylist(playlistId: Int): List<Track> {
        return playlistRepository.getTracksForPlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        playlistRepository.deleteTrackFromPlaylist(track, playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

}