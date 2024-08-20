package com.example.myplaylistmaker.settings.data

import androidx.appcompat.app.AppCompatDelegate
import com.example.myplaylistmaker.creator.Creator
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository

class ThemeRepositoryImp : ThemeRepository {
    companion object {
        private const val NAME_THEME = "name_theme"
        private const val KEY_THEME = "key_theme"
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun sharedPreferencesEdit(checked: Boolean) {
        val sharedPref = Creator.provideSharedPreferences(NAME_THEME)
        sharedPref.edit()
            .putBoolean(KEY_THEME, checked)
            .apply()
    }

    override fun getSharedPreferencesThemeValue(): Boolean {
        val sharedPref = Creator.provideSharedPreferences(NAME_THEME)
        val darkTheme = sharedPref.getBoolean(KEY_THEME, false)
        return darkTheme
    }
}