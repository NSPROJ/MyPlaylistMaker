package com.example.myplaylistmaker.player.domain.api

import com.example.myplaylistmaker.search.domain.Track

interface TrackInteractor {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}