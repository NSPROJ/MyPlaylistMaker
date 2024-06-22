package com.example.myplaylistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val trackId: Long,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)


