package com.example.myplaylistmaker.search.domain.interactors

import com.example.myplaylistmaker.search.domain.api.SearchInteractor
import com.example.myplaylistmaker.search.domain.repositories.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            try {
                val foundTracks = repository.searchTrack(expression)
                consumer.consume(foundTracks)
            } catch (e: Exception) {
                consumer.onError(e)
            }
        }
    }
}