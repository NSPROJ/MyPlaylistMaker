package com.example.myplaylistmaker.media.data.dto

import android.net.Uri

interface ImageStorageManager {

    suspend fun saveImage(uri: Uri, filename: String): Boolean
    fun getImagePath(filename: String): String
}