package com.example.myplaylistmaker.media.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.media.data.dto.ImageStorageManager
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlayViewModel(private val playlistInteractor: PlaylistInteractor,private val application: Application,private val storageManager: ImageStorageManager)
    : ViewModel() {

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }

    suspend fun saveImage(uri: Uri, filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            storageManager.saveImage(uri, filename)
        }
    }

    fun getImagePath(filename: String): String {
        return storageManager.getImagePath(filename)
    }
}
