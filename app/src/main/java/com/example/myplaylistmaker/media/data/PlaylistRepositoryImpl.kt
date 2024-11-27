package com.example.myplaylistmaker.media.data

import com.example.myplaylistmaker.AppDataBase
import com.example.myplaylistmaker.db.PlaylistConverter
import com.example.myplaylistmaker.db.PlaylistTracksEntity
import com.example.myplaylistmaker.db.PlaylistsEntity
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.repositories.PlaylistRepository
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDataBase: AppDataBase, private val playlistConverter: PlaylistConverter):
    PlaylistRepository {

    private fun convertToPlaylist(playlist: List<PlaylistsEntity>): List<Playlist> {
        return playlist.map { playlist -> playlistConverter.mapFromEntity(playlist) }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = playlistConverter.mapToEntity(playlist)
        appDataBase.playlistsDao().insertPlaylist(playlistEntity)

    }

    override suspend fun getPlaylist(): Flow<List<Playlist>> = flow {
        val playlistEntity = appDataBase.playlistsDao().getPlaylists()
        val playlists = convertToPlaylist(playlistEntity)
        emit(playlists)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConverter.mapToEntity(playlist)
        appDataBase.playlistsDao().deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist) {
        playlist.trackId.add(track.trackId.toInt())
        playlist.count += 1
        val playlistEntity = playlistConverter.mapToEntity(playlist)
        appDataBase.playlistsDao().updatePlaylist(playlistEntity)

    }

    override suspend fun insertTrack(track: Track) {
        val trackToPlaylist = PlaylistTracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.country,
            track.primaryGenreName,
            track.previewUrl,
            System.currentTimeMillis(),
            track.isFavorite
        )
        appDataBase.playlistTracksDao().insertPlaylistTracks(trackToPlaylist)
    }
}