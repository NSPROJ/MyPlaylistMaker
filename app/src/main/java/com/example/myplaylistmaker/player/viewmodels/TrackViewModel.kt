package com.example.myplaylistmaker.player.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _track = MutableLiveData<Track?>()
    val track: MutableLiveData<Track?> = _track

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