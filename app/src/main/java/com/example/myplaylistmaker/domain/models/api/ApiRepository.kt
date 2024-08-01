package com.example.myplaylistmaker.domain.models.api

import com.example.myplaylistmaker.domain.models.TrackDto

interface ApiRepository {
    fun searchTracks(keyword: String, callback: (List<TrackDto>) -> Unit)
}