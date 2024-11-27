package com.example.myplaylistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.myplaylistmaker.media.domain.interactors.ImageStorage
import java.io.File
import java.io.FileOutputStream

class ImageStorageImpl(private val context: Context) : ImageStorage {

    override fun saveImage(uri: Uri, fileName: String): Boolean {
        val contentResolver = context.contentResolver
        val path = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_images")

        if (!path.exists()) {
            path.mkdirs()
        }

        val file = File(path, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                    outputStream.flush()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}