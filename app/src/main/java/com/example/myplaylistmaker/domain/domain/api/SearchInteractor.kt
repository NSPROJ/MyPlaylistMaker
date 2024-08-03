package com.example.myplaylistmaker.domain.domain.api

import com.example.myplaylistmaker.domain.domain.Track

interface SearchInteractor {
    fun searchTrack(expression: String, consumer: SearchConsumer)

    interface SearchConsumer {
        fun consume(foundTrack: List<Track>)
        fun onError(error: Throwable)
    }
}