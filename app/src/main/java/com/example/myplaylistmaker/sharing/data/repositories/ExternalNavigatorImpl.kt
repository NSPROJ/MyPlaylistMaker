package com.example.myplaylistmaker.sharing.data.repositories

import android.content.Context
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(): String {
        val link = context.getString(R.string.share_string)
        return link
    }

    override fun openLink(): String {
        val license = context.getString(R.string.YP_urlOffer)
        return license
    }

    override fun supEmail(): SupportIntentData {
        val testEmail = SupportIntentData(
            context.getString(R.string.my_mail),
            context.getString(R.string.congrats_str),
            context.getString(R.string.thanks_str)
        )
        return testEmail
    }
}