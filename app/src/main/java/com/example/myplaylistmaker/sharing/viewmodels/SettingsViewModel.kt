package com.example.myplaylistmaker.sharing.viewmodels

import androidx.lifecycle.ViewModel
import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor) : ViewModel() {
    fun getShareAppLink(): String {
        return sharingInteractor.shareApp()
    }

    fun getTermsLink(): String {
        return sharingInteractor.openTerms()
    }

    fun getSupportEmailData(): SupportIntentData {
        return sharingInteractor.openSupport()
    }
}