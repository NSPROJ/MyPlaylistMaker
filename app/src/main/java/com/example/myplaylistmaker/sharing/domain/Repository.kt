package com.example.myplaylistmaker.sharing.domain

class Repository(private val resourceProvider: ResourceProvider) {
    fun provideShareInteractor(): ShareInteractor {
        return ShareInteractorImpl(resourceProvider)
    }

    fun provideSupportInteractor(): SupportInteractor {
        return SupportInteractorImpl(resourceProvider)
    }

    fun provideOfferInteractor(): OfferInteractor {
        return OfferInteractorImpl(resourceProvider)
    }
}