package com.example.myplaylistmaker.sharing.domain.api

import com.example.myplaylistmaker.sharing.domain.SupportIntentData

interface SharingInteractor {
    fun shareApp(): String
    fun openTerms(): String
    fun openSupport(): SupportIntentData
}