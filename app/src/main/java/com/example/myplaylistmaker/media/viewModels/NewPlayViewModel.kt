package com.example.myplaylistmaker.media.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.media.data.dto.ImageStorageManager
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NewPlayViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val storageManager: ImageStorageManager
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>(null)
    val playlist: LiveData<Playlist?> = _playlist

    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    private val _playlistName = MutableLiveData("")
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData("")
    val playlistDescription: LiveData<String> = _playlistDescription

    private val _playlistCoverPath = MutableLiveData<String?>(null)
    val playlistCoverPath: LiveData<String?> = _playlistCoverPath

    fun setPlaylistName(name: String) {
        _playlistName.value = name
    }

    fun setPlaylistDescription(description: String) {
        _playlistDescription.value = description
    }

    fun setPlaylistCoverPath(path: String?) {
        _playlistCoverPath.value = path
    }

    fun loadPlaylist(playlistId: Int?) {
        if (playlistId == null) {
            _isEditing.value = false
            return
        }
        _isEditing.value = true
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.value = playlist
            if (playlist != null) {
                _playlistName.value = playlist.playlistName
            }
            if (playlist != null) {
                _playlistDescription.value = playlist.description
            }
            if (playlist != null) {
                _playlistCoverPath.value = playlist.path
            }
        }
    }

    fun savePlaylist() {
        val playlist = _playlist.value
        val name = _playlistName.value ?: ""
        val description = _playlistDescription.value ?: ""
        val path = _playlistCoverPath.value

        viewModelScope.launch {
            if (playlist == null) {
                val newPlaylist = Playlist(
                    0,
                    name,
                    description,
                    path.toString(),
                    mutableListOf(),
                    0
                )
                playlistInteractor.insertPlaylist(newPlaylist)
            } else {
                val updatedPlaylist = playlist.copy(
                    playlistName = name,
                    description = description,
                    path = path.toString()
                )
                playlistInteractor.insertPlaylist(updatedPlaylist)
            }
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
