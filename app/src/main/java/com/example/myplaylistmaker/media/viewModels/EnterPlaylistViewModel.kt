package com.example.myplaylistmaker.media.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.launch

class EnterPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.postValue(playlist!!)
        }
    }

    fun loadTracksForPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val tracks = playlistInteractor.getTracksForPlaylist(playlistId)
            _tracks.value = tracks
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlistId)
            val updatedTracks = playlistInteractor.getTracksForPlaylist(playlistId)
            _tracks.value = updatedTracks
            val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.value = updatedPlaylist!!
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }
}