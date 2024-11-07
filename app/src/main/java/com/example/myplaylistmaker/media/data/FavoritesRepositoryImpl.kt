package com.example.myplaylistmaker.media.data

import com.example.myplaylistmaker.AppDataBase
import com.example.myplaylistmaker.db.TrackConverter
import com.example.myplaylistmaker.db.TracksEntity
import com.example.myplaylistmaker.media.domain.repositories.FavoritesRepository
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val trackConverter: TrackConverter
) : FavoritesRepository {

    override suspend fun getFavorites(): Flow<List<Track>> = flow {
        val favourites = appDataBase.tracksDao().getAllTracks()
        val tracks = convertFromEntity(favourites)
        for (i in tracks) {
            i.isFavorite = true
        }
        emit(tracks)
    }.map { trackList ->
        trackList.sortedByDescending { it.addedTimestamp }
    }

    override suspend fun insertFavorite(track: Track) {
        appDataBase.tracksDao().insertTrack(trackConverter.convertToEntity(track))
    }

    override suspend fun deleteFavorite(track: Track) {
        appDataBase.tracksDao().deleteTrack(trackConverter.convertToEntity(track))
    }

    private fun convertFromEntity(trackList: List<TracksEntity>): List<Track> {
        return trackList.map {
            trackConverter.convertToTrack(it)
        }
    }
}