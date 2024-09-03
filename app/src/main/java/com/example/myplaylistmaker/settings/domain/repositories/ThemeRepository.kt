package com.example.myplaylistmaker.settings.domain.repositories

interface ThemeRepository {
    fun switchTheme(darkThemeEnabled: Boolean) {
    }

    fun sharedPreferencesEdit(checked: Boolean) {
    }

    fun getSharedPreferencesThemeValue(): Boolean
}