package com.example.myplaylistmaker.domain.domain.interactors

import com.example.myplaylistmaker.domain.domain.Track
import com.example.myplaylistmaker.domain.domain.api.TrackInteractor
import com.example.myplaylistmaker.domain.domain.repositories.TrackRepository

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getSavedTrack(): Track {
        return repository.getSavedTrack()
    }
}
