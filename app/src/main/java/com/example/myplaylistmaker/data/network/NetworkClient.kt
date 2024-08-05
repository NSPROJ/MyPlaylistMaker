package com.example.myplaylistmaker.data.network

import com.example.myplaylistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}