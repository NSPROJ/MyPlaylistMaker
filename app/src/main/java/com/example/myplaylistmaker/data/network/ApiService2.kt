package com.example.myplaylistmaker.data.network

import com.example.myplaylistmaker.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService2 {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}