package com.example.myplaylistmaker.domain.repositories

import com.example.myplaylistmaker.domain.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}