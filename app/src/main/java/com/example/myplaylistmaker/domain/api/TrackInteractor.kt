package com.example.myplaylistmaker.domain.api

import com.example.myplaylistmaker.domain.Track

interface TrackInteractor {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}