package com.example.myplaylistmaker.domain.api

import com.example.myplaylistmaker.domain.Track

interface SearchInteractor {
    fun searchTrack(expression: String, consumer: SearchConsumer)

    interface SearchConsumer {
        fun consume(foundTrack: List<Track>)
        fun onError(error: Throwable)
    }
}