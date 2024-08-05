package com.example.myplaylistmaker.domain.repositories

import com.example.myplaylistmaker.domain.Track

interface TrackRepository {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}