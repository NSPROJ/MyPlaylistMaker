package com.example.myplaylistmaker.data.repositories

import android.content.Context
import com.example.myplaylistmaker.domain.domain.Track
import com.example.myplaylistmaker.domain.domain.repositories.TrackRepository

class TrackRepositoryImpl(private val context: Context) : TrackRepository {
    companion object {
        const val PREFS_NAME = "MediaActivityPrefs"
        const val TRACK_NAME_KEY = "trackName"
        const val ARTIST_NAME_KEY = "artistName"
        const val TRACK_TIME_MILLIS_KEY = "trackTimeMillis"
        const val ARTWORK_URL_KEY = "artworkUrl100"
        const val TRACK_ID_KEY = "trackId"
        const val COLLECTION_NAME_KEY = "collectionName"
        const val RELEASE_DATE_KEY = "releaseDate"
        const val PRIMARY_GENRE_NAME_KEY = "primaryGenreName"
        const val COUNTRY_KEY = "country"
        const val PREVIEW_URL_KEY = "previewUrl"
    }

    override fun saveTrack(track: Track) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(TRACK_NAME_KEY, track.trackName)
            putString(ARTIST_NAME_KEY, track.artistName)
            putLong(TRACK_TIME_MILLIS_KEY, track.trackTimeMillis)
            putString(ARTWORK_URL_KEY, track.artworkUrl100)
            putLong(TRACK_ID_KEY, track.trackId)
            putString(COLLECTION_NAME_KEY, track.collectionName)
            putString(RELEASE_DATE_KEY, track.releaseDate)
            putString(PRIMARY_GENRE_NAME_KEY, track.primaryGenreName)
            putString(COUNTRY_KEY, track.country)
            putString(PREVIEW_URL_KEY, track.previewUrl)
            apply()
        }
    }

    override fun getSavedTrack(): Track {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return Track(
            trackName = sharedPreferences.getString(TRACK_NAME_KEY, "")!!,
            artistName = sharedPreferences.getString(ARTIST_NAME_KEY, "")!!,
            trackTimeMillis = sharedPreferences.getLong(TRACK_TIME_MILLIS_KEY, 0),
            artworkUrl100 = sharedPreferences.getString(ARTWORK_URL_KEY, "")!!,
            trackId = sharedPreferences.getLong(TRACK_ID_KEY, 0),
            collectionName = sharedPreferences.getString(COLLECTION_NAME_KEY, null)!!,
            releaseDate = sharedPreferences.getString(RELEASE_DATE_KEY, "")!!,
            primaryGenreName = sharedPreferences.getString(PRIMARY_GENRE_NAME_KEY, "")!!,
            country = sharedPreferences.getString(COUNTRY_KEY, "")!!,
            previewUrl = sharedPreferences.getString(PREVIEW_URL_KEY, "")!!
        )
    }
}