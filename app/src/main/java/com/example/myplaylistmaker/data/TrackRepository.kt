package com.example.myplaylistmaker.data

import com.example.myplaylistmaker.domain.models.TrackDto

interface TrackRepository {
    fun saveTrack(track: TrackDto)
    fun getSavedTrack(): TrackDto
}