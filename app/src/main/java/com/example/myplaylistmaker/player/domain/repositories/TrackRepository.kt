package com.example.myplaylistmaker.player.domain.repositories

import com.example.myplaylistmaker.search.domain.Track

interface TrackRepository {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}