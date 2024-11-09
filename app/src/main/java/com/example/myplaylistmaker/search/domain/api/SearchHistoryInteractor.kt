package com.example.myplaylistmaker.search.domain.api

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}