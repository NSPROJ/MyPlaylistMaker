package com.example.myplaylistmaker

import android.app.Application
import com.example.myplaylistmaker.di.appModule
import com.example.myplaylistmaker.di.interactorModule
import com.example.myplaylistmaker.di.repositoryModule
import com.example.myplaylistmaker.di.viewModelModule
import com.example.myplaylistmaker.settings.domain.api.ThemeInteractor
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(viewModelModule, appModule, repositoryModule, interactorModule))
        }

        val themeInteractor: ThemeInteractor by inject()
        val darkTheme = get<ThemeRepository>().getSharedPreferencesThemeValue()
        themeInteractor.switchTheme(darkTheme)
    }
}

