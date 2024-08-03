package com.example.myplaylistmaker.domain.domain.interactors

import com.example.myplaylistmaker.domain.domain.api.SearchInteractor
import com.example.myplaylistmaker.domain.domain.repositories.SearchRepository
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