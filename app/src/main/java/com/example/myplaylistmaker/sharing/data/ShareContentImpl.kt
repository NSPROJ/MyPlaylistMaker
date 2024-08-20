package com.example.myplaylistmaker.sharing.data

import android.content.res.Resources
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.sharing.domain.OfferInteractor
import com.example.myplaylistmaker.sharing.domain.ShareInteractor
import com.example.myplaylistmaker.sharing.domain.SupportInteractor

class ShareInteractorImpl(private val resources: Resources) : ShareInteractor {
    override fun getShareMessage(): String {
        return resources.getString(R.string.Y_P)
    }
}

class SupportInteractorImpl(private val resources: Resources) : SupportInteractor {
    override fun getSupportEmail(): String {
        return resources.getString(R.string.my_mail)
    }

    override fun getSupportSubject(): String {
        return resources.getString(R.string.congrats_str)
    }

    override fun getSupportText(): String {
        return resources.getString(R.string.thanks_str)
    }
}

class OfferInteractorImpl(private val resources: Resources) : OfferInteractor {
    override fun getOfferUrl(): String {
        return resources.getString(R.string.YP_urlOffer)
    }
}