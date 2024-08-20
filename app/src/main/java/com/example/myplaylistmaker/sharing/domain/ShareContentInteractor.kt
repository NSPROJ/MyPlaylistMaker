package com.example.myplaylistmaker.sharing.domain

interface ShareInteractor {
    fun getShareMessage(): String
}

interface SupportInteractor {
    fun getSupportEmail(): String
    fun getSupportSubject(): String
    fun getSupportText(): String
}

interface OfferInteractor {
    fun getOfferUrl(): String
}