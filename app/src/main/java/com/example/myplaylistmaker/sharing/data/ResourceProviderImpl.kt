package com.example.myplaylistmaker.sharing.data

import android.content.res.Resources
import com.example.myplaylistmaker.sharing.domain.ResourceProvider

class ResourceProviderImpl(private val resources: Resources) : ResourceProvider {
    override fun getString(resId: Int): String {
        return resources.getString(resId)
    }
}