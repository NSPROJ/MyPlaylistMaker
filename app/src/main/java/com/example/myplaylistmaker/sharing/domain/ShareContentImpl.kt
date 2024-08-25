package com.example.myplaylistmaker.sharing.domain

import com.example.myplaylistmaker.R

class ShareInteractorImpl(private val resourceRepository: ResourceProvider) : ShareInteractor {
    override fun getShareMessage(): String {
        return resourceRepository.getString(R.string.Y_P)
    }
}

class SupportInteractorImpl(private val resourceRepository: ResourceProvider) : SupportInteractor {
    override fun getSupportEmail(): String {
        return resourceRepository.getString(R.string.my_mail)
    }

    override fun getSupportSubject(): String {
        return resourceRepository.getString(R.string.congrats_str)
    }

    override fun getSupportText(): String {
        return resourceRepository.getString(R.string.thanks_str)
    }
}

class OfferInteractorImpl(private val resourceRepository: ResourceProvider) : OfferInteractor {
    override fun getOfferUrl(): String {
        return resourceRepository.getString(R.string.YP_urlOffer)
    }
}