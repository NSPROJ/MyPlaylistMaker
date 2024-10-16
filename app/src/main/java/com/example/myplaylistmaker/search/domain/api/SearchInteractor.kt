package com.example.myplaylistmaker.search.domain.api

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTrack(expression: String): Flow<List<Track>>
}