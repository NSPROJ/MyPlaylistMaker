package com.example.myplaylistmaker.settings.domain.interactors

import com.example.myplaylistmaker.settings.domain.api.ThemeInteractor
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {
    override fun switchTheme(darkThemeEnabled: Boolean) = repository.switchTheme(darkThemeEnabled)
    override fun sharedPreferencesEdit(checked: Boolean) = repository.sharedPreferencesEdit(checked)
    override fun getSharedPreferencesThemeValue(): Boolean =
        repository.getSharedPreferencesThemeValue()
}