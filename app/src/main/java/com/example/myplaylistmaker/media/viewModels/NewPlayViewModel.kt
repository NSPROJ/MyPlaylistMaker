package com.example.myplaylistmaker.media.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlayViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }
}