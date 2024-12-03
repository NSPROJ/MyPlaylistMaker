package com.example.myplaylistmaker.search.domain.repositories

import com.example.myplaylistmaker.search.domain.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}