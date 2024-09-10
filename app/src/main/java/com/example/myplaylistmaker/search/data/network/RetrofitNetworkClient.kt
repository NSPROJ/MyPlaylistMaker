package com.example.myplaylistmaker.search.data.network

import com.example.myplaylistmaker.search.data.dto.Response
import com.example.myplaylistmaker.search.data.dto.SearchRequest

class RetrofitNetworkClient(private val itunesAPI: ApiService) : NetworkClient {

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