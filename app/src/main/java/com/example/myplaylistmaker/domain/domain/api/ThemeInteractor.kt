package com.example.myplaylistmaker.domain.domain.api

interface ThemeInteractor {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun sharedPreferencesEdit(checked: Boolean)
    fun getSharedPreferencesThemeValue(): Boolean
}