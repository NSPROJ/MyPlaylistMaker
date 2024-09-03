package com.example.myplaylistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository
import org.koin.java.KoinJavaComponent.getKoin

class ThemeRepositoryImpl : ThemeRepository {
    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun sharedPreferencesEdit(checked: Boolean) {
        val sharedPref = getKoin().get<SharedPreferences>()
        sharedPref.edit().putBoolean("key_theme", checked).apply()
    }

    override fun getSharedPreferencesThemeValue(): Boolean {
        val sharedPref = getKoin().get<SharedPreferences>()
        return sharedPref.getBoolean("key_theme", false)
    }
}