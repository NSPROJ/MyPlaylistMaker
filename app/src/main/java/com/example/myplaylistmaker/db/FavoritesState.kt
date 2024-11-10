package com.example.myplaylistmaker.db

import com.example.myplaylistmaker.search.domain.Track

sealed interface FavoritesState {

    data class Content(val trackList: List<Track>) : FavoritesState

    data class Error(val message: String) : FavoritesState
}