package com.example.myplaylistmaker.settings.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myplaylistmaker.creator.Creator

class ThemeViewModel : ViewModel() {

    private val switchThemeInteractor = Creator.provideSwitchThemeInteractor()

    private val isThemeCheck = MutableLiveData<Boolean>()

    val isThemeChecked: LiveData<Boolean> get() = isThemeCheck

    init {
        isThemeCheck.value = switchThemeInteractor.getSharedPreferencesThemeValue()
    }

    fun onThemeSwitchChanged(isChecked: Boolean) {
        switchThemeInteractor.sharedPreferencesEdit(isChecked)
        switchThemeInteractor.switchTheme(isChecked)
        isThemeCheck.value = isChecked
    }
}