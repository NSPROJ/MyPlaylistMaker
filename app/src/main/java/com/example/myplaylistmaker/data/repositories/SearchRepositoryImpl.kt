package com.example.myplaylistmaker.data.repositories

import com.example.myplaylistmaker.data.dto.SearchRequest
import com.example.myplaylistmaker.data.dto.SearchResponse
import com.example.myplaylistmaker.data.network.NetworkClient
import com.example.myplaylistmaker.domain.domain.Track
import com.example.myplaylistmaker.domain.domain.repositories.SearchRepository

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {

    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as SearchResponse).results.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}