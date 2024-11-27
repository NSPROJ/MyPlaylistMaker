package com.example.myplaylistmaker.player.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.db.PlaylistState
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.FavoritesInteractor
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(
    private val trackInteractor: TrackInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _track = MutableLiveData<Track?>()
    val track: LiveData<Track?> = _track
    private val playlistState = MutableLiveData<PlaylistState>()
    fun getPlaylistState(): LiveData<PlaylistState> = playlistState

    fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylist()
                .collect { playlists -> processResult(playlists) }

        }
    }


    fun isTrackInPlaylist(trackIdToCheck: Int, playlist: Playlist): Boolean {
        return playlist.trackId.contains(trackIdToCheck)
    }

    fun updatePlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            if (isTrackInPlaylist(track.trackId.toInt(), playlist)) {
                playlist.trackId.remove(track.trackId.toInt())
                playlistInteractor
                playlist.count--
            } else {
                playlist.trackId.add(track.trackId.toInt())
            }
            playlistInteractor.updatePlaylist(track, playlist)
        }
    }


    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Error("empty list"))
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistState) {
        playlistState.postValue(state)
    }

    fun toggleFavorite() {
        val currentTrack = _track.value ?: return

        viewModelScope.launch {
            currentTrack.isFavorite = !currentTrack.isFavorite

            if (currentTrack.isFavorite) {
                favoritesInteractor.insertFavorite(currentTrack)
            } else {
                favoritesInteractor.deleteFavorite(currentTrack)
            }

            _track.value = currentTrack
        }
    }

    fun initTrack(intentTrack: Track?, isTrackSelected: Boolean) {
        viewModelScope.launch {
            val initializedTrack = intentTrack ?: getSavedTrack()

            if (initializedTrack != null) {
                val favorites = favoritesInteractor.getFavorites().first()
                val isFavorite = favorites.any { it.trackId == initializedTrack.trackId }

                initializedTrack.isFavorite = isFavorite
                _track.value = initializedTrack

                if (isTrackSelected) {
                    saveTrack(initializedTrack)
                }
            } else {
                Log.e(TAG, "track is null!")
            }
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
            val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
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
