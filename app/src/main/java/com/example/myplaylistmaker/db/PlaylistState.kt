package com.example.myplaylistmaker.db

import com.example.myplaylistmaker.media.domain.Playlist

interface PlaylistState {
    data class Content(val playlist: List<Playlist>) : PlaylistState

    data class Error(val message: String) : PlaylistState
}