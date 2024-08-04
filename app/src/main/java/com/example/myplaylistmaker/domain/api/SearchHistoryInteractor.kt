package com.example.myplaylistmaker.domain.api

import com.example.myplaylistmaker.domain.Track

interface SearchHistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}