package com.example.myplaylistmaker.media.domain.interactors

import android.net.Uri

class SaveImageInteractor(private val imageStorage: ImageStorage) {
    fun saveImageToStorage(uri: Uri, fileName: String): Boolean {
        return imageStorage.saveImage(uri, fileName)
    }
}