package com.example.myplaylistmaker.domain.domain

data class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)