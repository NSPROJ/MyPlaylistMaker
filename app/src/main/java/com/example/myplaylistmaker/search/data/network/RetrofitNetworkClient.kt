package com.example.myplaylistmaker.search.data.network

import com.example.myplaylistmaker.search.data.dto.Response
import com.example.myplaylistmaker.search.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesAPI = retrofit.create(ApiService::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            val resp = itunesAPI.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}