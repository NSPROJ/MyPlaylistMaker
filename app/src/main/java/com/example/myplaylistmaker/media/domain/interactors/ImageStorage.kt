package com.example.myplaylistmaker.media.domain.interactors

import android.net.Uri

interface ImageStorage {
    fun saveImage(uri: Uri, fileName: String): Boolean
}