package com.example.myplaylistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.myplaylistmaker.App
import com.example.myplaylistmaker.player.data.repositories.TrackRepositoryImpl
import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.player.domain.interactors.TrackInteractorImpl
import com.example.myplaylistmaker.search.data.network.RetrofitNetworkClient
import com.example.myplaylistmaker.search.data.repositories.SearchHistoryRepositoryImpl
import com.example.myplaylistmaker.search.data.repositories.SearchRepositoryImpl
import com.example.myplaylistmaker.search.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.search.domain.api.SearchInteractor
import com.example.myplaylistmaker.search.domain.interactors.SearchHistoryInteractorImpl
import com.example.myplaylistmaker.search.domain.interactors.SearchInteractorImpl
import com.example.myplaylistmaker.search.domain.repositories.SearchHistoryRepository
import com.example.myplaylistmaker.search.domain.repositories.SearchRepository
import com.example.myplaylistmaker.settings.data.ThemeRepositoryImp
import com.example.myplaylistmaker.settings.domain.api.ThemeInteractor
import com.example.myplaylistmaker.settings.domain.interactors.ThemeInteractorImpl
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository
import com.example.myplaylistmaker.sharing.data.repositories.ExternalNavigatorImpl
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator
import com.example.myplaylistmaker.sharing.domain.api.SharingInteractor
import com.example.myplaylistmaker.sharing.domain.interactors.SharingInteractorImpl

object Creator {
    private lateinit var application: App

    fun initApplication(application: App) {
        Creator.application = application
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(provideSearchRepository())
    }

    fun provideSharedPreferences(key: String): SharedPreferences {
        return application.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun provideSwitchThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(provideSwitchThemeRepository())
    }

    fun provideSwitchThemeRepository(): ThemeRepository {
        return ThemeRepositoryImp()
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        val repository = TrackRepositoryImpl(context)
        return TrackInteractorImpl(repository)
    }

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(sharedPreferences: SharedPreferences) {
        Creator.sharedPreferences = sharedPreferences
    }

    private val repository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(sharedPreferences)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(repository)
    }
}



