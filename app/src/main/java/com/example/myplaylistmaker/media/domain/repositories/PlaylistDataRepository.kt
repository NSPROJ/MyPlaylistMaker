package com.example.myplaylistmaker.media.domain.repositories

import android.net.Uri

interface PlaylistDataRepository {
    suspend fun saveImageToStorage(uri: Uri): Boolean
    suspend fun getPath(): String
}