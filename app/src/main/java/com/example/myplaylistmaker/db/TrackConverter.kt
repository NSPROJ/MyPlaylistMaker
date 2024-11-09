package com.example.myplaylistmaker.db

import com.example.myplaylistmaker.search.domain.Track

class TrackConverter {

    fun convertToEntity(track: Track): TracksEntity {
        return TracksEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedTime = System.currentTimeMillis()

        )
    }


    fun convertToTrack(trackEntity: TracksEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            collectionName = trackEntity.collectionName,
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            addedTime = trackEntity.addedTime
        )
    }
}


