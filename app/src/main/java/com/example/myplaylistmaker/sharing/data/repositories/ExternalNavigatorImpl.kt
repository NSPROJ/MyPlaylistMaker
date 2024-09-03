package com.example.myplaylistmaker.sharing.data.repositories

import android.content.Context
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(): String = context.getString(R.string.share_string)
    override fun openLink(): String = context.getString(R.string.YP_urlOffer)
    override fun supEmail(): SupportIntentData {
        return SupportIntentData(
            email = context.getString(R.string.my_mail),
            subject = context.getString(R.string.congrats_str),
            text = context.getString(R.string.thanks_str)
        )
    }
}