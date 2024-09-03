package com.example.myplaylistmaker.sharing.domain.api

import com.example.myplaylistmaker.sharing.domain.SupportIntentData

interface ExternalNavigator {
    fun shareLink(): String
    fun openLink(): String
    fun supEmail(): SupportIntentData
}