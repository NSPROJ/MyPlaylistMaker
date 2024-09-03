package com.example.myplaylistmaker

import android.app.Application
import com.example.myplaylistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        Creator.initApplication(this)
        val sharedPreferencesInteractor = Creator.provideSwitchThemeInteractor()
        val darkTheme = Creator.provideSwitchThemeRepository().getSharedPreferencesThemeValue()
        sharedPreferencesInteractor.switchTheme(darkTheme)
        val sharedPreferences = Creator.provideSharedPreferences(APP_PREFS)
        Creator.initialize(sharedPreferences)
    }
    companion object {
        const val APP_PREFS = "app_prefs"
    }
}

