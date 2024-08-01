package com.example.myplaylistmaker.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search")
    fun search(@Query("term") keyword: String): Call<ApiResponse>
}