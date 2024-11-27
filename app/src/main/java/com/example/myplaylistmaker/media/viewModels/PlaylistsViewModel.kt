package com.example.myplaylistmaker.media.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.db.PlaylistState
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistState: LiveData<PlaylistState> = _playlistState

    private val _visibilityState = MutableLiveData(false)
    val visibilityState: LiveData<Boolean> = _visibilityState

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylist()
                .collect { playlists -> processResult(playlists) }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Error("Playlists list is empty"))
            _visibilityState.value = false
        } else {
            renderState(PlaylistState.Content(playlists))
            _visibilityState.value = true
        }
    }

    private fun renderState(state: PlaylistState) {
        _playlistState.value = state
    }
}