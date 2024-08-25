package com.example.myplaylistmaker.sharing.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myplaylistmaker.creator.Creator
import com.example.myplaylistmaker.sharing.domain.OfferInteractor
import com.example.myplaylistmaker.sharing.domain.ShareInteractor
import com.example.myplaylistmaker.sharing.domain.SupportIntentData
import com.example.myplaylistmaker.sharing.domain.SupportInteractor

class ShareViewModel(
    private val shareInteractor: ShareInteractor,
    private val supportInteractor: SupportInteractor,
    private val offerInteractor: OfferInteractor
) : ViewModel() {

    private val _shareIntentData = MutableLiveData<String>()
    val shareIntentData: LiveData<String>
        get() = _shareIntentData

    private val _supportIntentData = MutableLiveData<SupportIntentData>()
    val supportIntentData: LiveData<SupportIntentData>
        get() = _supportIntentData

    private val _offerIntentData = MutableLiveData<String>()
    val offerIntentData: LiveData<String>
        get() = _offerIntentData

    fun prepareShareIntent() {
        _shareIntentData.value = shareInteractor.getShareMessage()
    }

    fun prepareSupportIntent() {
        _supportIntentData.value = SupportIntentData(
            email = supportInteractor.getSupportEmail(),
            subject = supportInteractor.getSupportSubject(),
            text = supportInteractor.getSupportText()
        )
    }

    fun prepareOfferIntent() {
        _offerIntentData.value = offerInteractor.getOfferUrl()
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = Creator.provideRepository()
            return ShareViewModel(
                repository.provideShareInteractor(),
                repository.provideSupportInteractor(),
                repository.provideOfferInteractor()
            ) as T
        }
    }
}

