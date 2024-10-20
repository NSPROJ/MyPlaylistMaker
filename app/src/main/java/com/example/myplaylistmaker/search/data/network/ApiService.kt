package com.example.myplaylistmaker.search.data.network

import com.example.myplaylistmaker.search.data.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): SearchResponse
}