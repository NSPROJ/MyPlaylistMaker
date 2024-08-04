package com.example.myplaylistmaker.domain.interactors

import com.example.myplaylistmaker.domain.Track
import com.example.myplaylistmaker.domain.api.TrackInteractor
import com.example.myplaylistmaker.domain.repositories.TrackRepository

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getSavedTrack(): Track {
        return repository.getSavedTrack()
    }
}
