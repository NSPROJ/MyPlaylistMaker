package com.example.myplaylistmaker.presentation

import com.example.myplaylistmaker.domain.models.TrackDto
import com.example.myplaylistmaker.domain.models.api.TrackInteractor
import com.example.myplaylistmaker.data.TrackRepository

    class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

        override fun saveTrack(track: TrackDto) {
            repository.saveTrack(track)
        }

        override fun getSavedTrack(): TrackDto {
            return repository.getSavedTrack()
        }
    }
