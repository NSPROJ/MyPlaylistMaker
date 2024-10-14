package com.example.myplaylistmaker.search.domain.repositories

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTrack(expression: String): Flow<List<Track>>
}