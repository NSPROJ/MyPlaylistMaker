package com.example.myplaylistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private val PREFS_NAME = "ThemePrefs"
    private val PREF_DARK_THEME = "darkTheme"

    companion object {
        var darkTheme: Boolean = false
        lateinit var instance: App
            private set

        fun switchTheme(darkThemeEnabled: Boolean) {
            instance.saveThemeToPreferences(darkThemeEnabled)
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        darkTheme = getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun getThemeFromPreferences(): Boolean {
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(PREF_DARK_THEME, false)
    }

    fun saveThemeToPreferences(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(PREF_DARK_THEME, darkThemeEnabled)
        editor.apply()
    }
}