package com.example.myplaylistmaker.player.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myplaylistmaker.creator.Creator
import com.example.myplaylistmaker.player.domain.api.TrackInteractor

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private val _trackInteractor = MutableLiveData<TrackInteractor>()

    val trackInteractor: LiveData<TrackInteractor>
        get() = _trackInteractor

    init {
        _trackInteractor.value = Creator.provideTrackInteractor(application)
    }
}