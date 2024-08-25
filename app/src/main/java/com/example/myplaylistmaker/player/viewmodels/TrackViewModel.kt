package com.example.myplaylistmaker.player.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myplaylistmaker.player.data.repositories.TrackRepositoryImpl
import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.player.domain.interactors.TrackInteractorImpl
import com.example.myplaylistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private val trackInteractor: TrackInteractor = TrackInteractorImpl(TrackRepositoryImpl(application))
    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    fun initTrack(intentTrack: Track?) {
        if (intentTrack != null) {
            _track.value = intentTrack
            saveTrack(intentTrack)
        } else {
            _track.value = getSavedTrack()
        }
    }

    private fun saveTrack(track: Track) {
        trackInteractor.saveTrack(track)
    }

    private fun getSavedTrack(): Track {
        return trackInteractor.getSavedTrack()
    }

    fun formatReleaseDate(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val parsedDate = parser.parse(date)
            if (parsedDate != null) {
                formatter.format(parsedDate)
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    fun getCoverArtwork(): String {
        return _track.value?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""
    }
}