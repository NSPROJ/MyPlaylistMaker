package com.example.myplaylistmaker.search.data.network

import com.example.myplaylistmaker.search.data.dto.Response
import com.example.myplaylistmaker.search.data.dto.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val itunesAPI: ApiService) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesAPI.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}