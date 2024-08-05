package com.example.myplaylistmaker.domain.repositories

import com.example.myplaylistmaker.domain.Track

interface SearchRepository {

    fun searchTrack(expression: String): List<Track>
}