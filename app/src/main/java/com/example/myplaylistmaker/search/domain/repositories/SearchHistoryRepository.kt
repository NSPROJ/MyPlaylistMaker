package com.example.myplaylistmaker.search.domain.repositories

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
    fun getTracksFlow(): Flow<List<Track>>
}