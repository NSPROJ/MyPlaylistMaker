package com.example.myplaylistmaker.media.domain

data class Playlist(
    val playlistId:Int,
    val playlistName:String,
    val description:String,
    val path:String,
    var trackId:MutableList<Int>,
    var count:Int,
)
