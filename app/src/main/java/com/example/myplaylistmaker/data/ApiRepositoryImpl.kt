package com.example.myplaylistmaker.data

import com.example.myplaylistmaker.data.network.ApiResponse
import com.example.myplaylistmaker.data.network.ApiService
import com.example.myplaylistmaker.domain.models.TrackDto
import com.example.myplaylistmaker.domain.models.api.ApiRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRepositoryImpl : ApiRepository {
    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    override fun searchTracks(keyword: String, callback: (List<TrackDto>) -> Unit) {
        apiService.search(keyword).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    callback(results)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}