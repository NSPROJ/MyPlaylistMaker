package com.example.myplaylistmaker.domain.interactors

import com.example.myplaylistmaker.domain.Track
import com.example.myplaylistmaker.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.domain.repositories.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun getHistory(): ArrayList<Track> {
        return repository.getHistory()
    }
}