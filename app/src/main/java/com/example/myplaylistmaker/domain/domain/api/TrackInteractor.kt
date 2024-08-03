package com.example.myplaylistmaker.domain.domain.api

import com.example.myplaylistmaker.domain.domain.Track

interface TrackInteractor {
    fun saveTrack(track: Track)
    fun getSavedTrack(): Track
}