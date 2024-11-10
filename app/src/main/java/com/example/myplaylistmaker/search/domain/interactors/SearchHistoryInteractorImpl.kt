package com.example.myplaylistmaker.search.domain.interactors

import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.search.domain.repositories.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

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