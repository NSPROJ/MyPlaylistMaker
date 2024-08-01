package com.example.myplaylistmaker.domain.models.api

import com.example.myplaylistmaker.domain.models.TrackDto

class SearchInteractor(private val apiRepository: ApiRepository) {
    fun search(keyword: String, callback: (List<TrackDto>) -> Unit) {
        apiRepository.searchTracks(keyword, callback)
    }
}