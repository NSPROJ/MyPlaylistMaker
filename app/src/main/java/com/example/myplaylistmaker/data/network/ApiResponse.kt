package com.example.myplaylistmaker.data.network

import com.example.myplaylistmaker.domain.models.TrackDto

data class ApiResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)