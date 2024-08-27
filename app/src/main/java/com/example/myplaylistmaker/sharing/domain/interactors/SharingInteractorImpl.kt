package com.example.myplaylistmaker.sharing.domain.interactors

import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator
import com.example.myplaylistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp(): String {
        return externalNavigator.shareLink()
    }

    override fun openTerms(): String {
        return externalNavigator.openLink()
    }

    override fun openSupport(): SupportIntentData {
        return externalNavigator.supEmail()
    }

}