package com.example.myplaylistmaker.sharing.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myplaylistmaker.creator.Creator
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

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(Creator.provideSharingInteractor()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}