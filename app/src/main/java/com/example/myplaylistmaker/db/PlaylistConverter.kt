package com.example.myplaylistmaker.db

import com.example.myplaylistmaker.media.domain.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistConverter {

    private fun playlistToString(list: MutableList<Int>): String {
        return Gson().toJson(list)
    }

    private fun stringToList(string: String): MutableList<Int> {
        val type = object : TypeToken<MutableList<Int>>() {}.type
        return Gson().fromJson(string, type)
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


