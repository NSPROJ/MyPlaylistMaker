package com.example.myplaylistmaker.media.data.dto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidImageStorageManager(private val context: Context) : ImageStorageManager {

    override suspend fun saveImage(uri: Uri, filename: String): Boolean {
        val contentResolver = context.contentResolver
        val path = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_images")

        if (!path.exists()) {
            path.mkdirs()
        }

        val file = File(path, filename)

        try {
            withContext(Dispatchers.IO) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                        outputStream.flush()
                    }
                }
            }

            return true
        } catch (e: Exception) {
            Log.e("ImageStorageManager", "Error saving image: ${e.message}")
            return false
        }
    }

    override fun getImagePath(filename: String): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_images")
        val file = File(filePath, filename)
        return file.toString()
    }
}
