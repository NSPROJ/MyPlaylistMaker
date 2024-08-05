package com.example.myplaylistmaker.domain

import com.example.myplaylistmaker.domain.Track

data class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)