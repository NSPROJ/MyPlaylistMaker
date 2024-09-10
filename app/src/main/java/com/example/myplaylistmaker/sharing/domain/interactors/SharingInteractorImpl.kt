package com.example.myplaylistmaker.sharing.domain.interactors

import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator
import com.example.myplaylistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    override fun shareApp(): String = externalNavigator.shareLink()
    override fun openTerms(): String = externalNavigator.openLink()
    override fun openSupport(): SupportIntentData = externalNavigator.supEmail()
}
