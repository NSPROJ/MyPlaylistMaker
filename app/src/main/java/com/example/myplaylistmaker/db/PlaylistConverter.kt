package com.example.myplaylistmaker.db

import com.example.myplaylistmaker.media.domain.Playlist
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class PlaylistConverter {

    fun playlistToString(list: MutableList<Int>): String {
        return Gson().toJson(list.takeIf { it.isNotEmpty() } ?: emptyList<Int>())
    }

    fun stringToList(string: String): MutableList<Int> {
        if (string.isBlank()) {
            return mutableListOf()
        }
        return try {
            val type = object : TypeToken<MutableList<Int>>() {}.type
            Gson().fromJson(string, type) ?: mutableListOf()
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        }
    }


    fun mapToEntity(playlist: Playlist): PlaylistsEntity {
        return PlaylistsEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.description,
            playlist.path,
            playlistToString(playlist.trackId),
            playlist.count,
        )
    }

    fun mapFromEntity(entity: PlaylistsEntity): Playlist {
        return Playlist(
            entity.playlistId,
            entity.playlistName,
            entity.description,
            entity.path,
            stringToList(entity.trackId),
            entity.count
        )
    }
}


