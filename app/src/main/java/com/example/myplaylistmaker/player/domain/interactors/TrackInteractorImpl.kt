package com.example.myplaylistmaker.player.domain.interactors

import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.player.domain.repositories.TrackRepository
import com.example.myplaylistmaker.search.domain.Track

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getSavedTrack(): Track {
        return repository.getSavedTrack()
    }
}
