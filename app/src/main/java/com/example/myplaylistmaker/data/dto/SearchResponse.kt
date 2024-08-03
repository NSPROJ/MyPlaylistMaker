package com.example.myplaylistmaker.data.dto

data class SearchResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()