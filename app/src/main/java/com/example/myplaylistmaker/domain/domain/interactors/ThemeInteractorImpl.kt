package com.example.myplaylistmaker.domain.domain.interactors

import com.example.myplaylistmaker.domain.domain.api.ThemeInteractor
import com.example.myplaylistmaker.domain.domain.repositories.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) :
    ThemeInteractor {


    override fun switchTheme(darkThemeEnabled: Boolean) {
        return repository.switchTheme(darkThemeEnabled)
    }

    override fun sharedPreferencesEdit(checked: Boolean) {
        return repository.sharedPreferencesEdit(checked)
    }

    override fun getSharedPreferencesThemeValue(): Boolean {
        return repository.getSharedPreferencesThemeValue()
    }

}