package com.example.myplaylistmaker.search.domain.repositories

import com.example.myplaylistmaker.search.domain.Track

interface SearchRepository {

    fun searchTrack(expression: String): List<Track>
}