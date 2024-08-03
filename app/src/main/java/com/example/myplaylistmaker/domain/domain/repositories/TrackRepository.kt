package com.example.myplaylistmaker.domain.domain.repositories

import com.example.myplaylistmaker.domain.domain.Track

interface TrackRepository {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}