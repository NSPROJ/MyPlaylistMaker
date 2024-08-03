package com.example.myplaylistmaker.domain.domain.repositories

interface ThemeRepository {
    fun switchTheme(darkThemeEnabled: Boolean) {
    }

    fun sharedPreferencesEdit(checked: Boolean) {
    }

    fun getSharedPreferencesThemeValue(): Boolean
}