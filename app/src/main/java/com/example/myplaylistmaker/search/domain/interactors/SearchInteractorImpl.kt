package com.example.myplaylistmaker.search.domain.interactors

import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.api.SearchInteractor
import com.example.myplaylistmaker.search.domain.repositories.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    override fun searchTrack(expression: String): Flow<List<Track>> = flow {
        try {
            repository.searchTrack(expression)
                .collect { tracks ->
                    emit(tracks)
                }
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)
}