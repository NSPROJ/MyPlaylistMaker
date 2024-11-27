package com.example.myplaylistmaker.media.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import com.example.myplaylistmaker.media.domain.interactors.SaveImageInteractor
import kotlinx.coroutines.launch

class NewPlayViewModel(private val playlistInteractor: PlaylistInteractor,private val saveImageInteractor: SaveImageInteractor ) : ViewModel() {

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }

    fun saveImage(uri: Uri, fileName: String): Boolean {
        return saveImageInteractor.saveImageToStorage(uri, fileName)
    }
}