package com.example.myplaylistmaker.media.data

import com.example.myplaylistmaker.AppDataBase
import com.example.myplaylistmaker.db.PlaylistConverter
import com.example.myplaylistmaker.db.PlaylistTracksEntity
import com.example.myplaylistmaker.db.PlaylistsEntity
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.repositories.PlaylistRepository
import com.example.myplaylistmaker.search.domain.Track
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val playlistConverter: PlaylistConverter
) :
    PlaylistRepository {

    private fun convertToPlaylist(playlist: List<PlaylistsEntity>): List<Playlist> {
        return playlist.map { entity -> playlistConverter.mapFromEntity(entity) }
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


    override suspend fun getTracksForPlaylist(playlistId: Int): List<Track> {
        val playlistEntity = appDataBase.playlistsDao().getPlaylistById(playlistId)
        val trackIdsString = playlistEntity?.trackId ?: ""
        val trackIds = try {
            val type = object : TypeToken<MutableList<Int>>() {}.type
            Gson().fromJson<MutableList<Int>>(trackIdsString, type)?.map { it.toLong() } ?: emptyList()
        } catch (e: JsonSyntaxException) {
            emptyList()
        }

        if (trackIds.isEmpty()) {
            return emptyList()
        }

        return appDataBase.playlistTracksDao().getTracksByIds(trackIds)
            .map { entity ->
                Track(
                    entity.trackId,
                    entity.trackName,
                    entity.artistName,
                    entity.trackTimeMillis,
                    entity.artworkUrl100,
                    entity.collectionName,
                    entity.releaseDate,
                    entity.country,
                    entity.primaryGenreName,
                    entity.previewUrl,
                    entity.isFavorite,
                    entity.addedTime
                )
            }
            .sortedByDescending { it.addedTime }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        val playlistEntity = appDataBase.playlistsDao().getPlaylistById(playlistId)
        if (playlistEntity != null) {
            val trackIdsString = playlistEntity.trackId
            val trackIds = playlistConverter.stringToList(trackIdsString)
            trackIds.remove(track.trackId.toInt())
            val updatedTrackIdsString = playlistConverter.playlistToString(trackIds)
            val updatedPlaylistEntity = playlistEntity.copy(
                trackId = updatedTrackIdsString,
                count = trackIds.size
            )
            appDataBase.playlistsDao().updatePlaylist(updatedPlaylistEntity)
        }
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return appDataBase.playlistsDao().getPlaylistById(playlistId)?.let { entity ->
            playlistConverter.mapFromEntity(entity).copy(
            )
        }!!
    }
}