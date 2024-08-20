package com.example.myplaylistmaker.search.data.dto

data class SearchResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()