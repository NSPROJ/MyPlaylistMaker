package com.example.myplaylistmaker.domain.domain.repositories

import com.example.myplaylistmaker.domain.domain.Track

interface SearchRepository {

    fun searchTrack(expression: String): List<Track>
}