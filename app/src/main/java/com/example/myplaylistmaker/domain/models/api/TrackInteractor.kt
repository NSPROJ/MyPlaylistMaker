package com.example.myplaylistmaker.domain.models.api

import com.example.myplaylistmaker.domain.models.TrackDto

interface TrackInteractor {
    fun saveTrack(track: TrackDto)
    fun getSavedTrack(): TrackDto
}