package com.example.myplaylistmaker.search.data.repositories

import com.example.myplaylistmaker.search.data.dto.SearchRequest
import com.example.myplaylistmaker.search.data.dto.SearchResponse
import com.example.myplaylistmaker.search.data.network.NetworkClient
import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTrack(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))

        if (response.resultCode == 200) {
            emit((response as SearchResponse).results.map {
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
            })
        } else {
            emit(emptyList())
        }
    }
}