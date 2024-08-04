package com.example.myplaylistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.myplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myplaylistmaker.data.repositories.SearchHistoryRepositoryImpl
import com.example.myplaylistmaker.data.repositories.SearchRepositoryImpl
import com.example.myplaylistmaker.data.repositories.ThemeRepositoryImp
import com.example.myplaylistmaker.data.repositories.TrackRepositoryImpl
import com.example.myplaylistmaker.domain.Track
import com.example.myplaylistmaker.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.domain.api.SearchInteractor
import com.example.myplaylistmaker.domain.api.ThemeInteractor
import com.example.myplaylistmaker.domain.api.TrackInteractor
import com.example.myplaylistmaker.domain.interactors.SearchHistoryInteractorImpl
import com.example.myplaylistmaker.domain.interactors.SearchInteractorImpl
import com.example.myplaylistmaker.domain.interactors.ThemeInteractorImpl
import com.example.myplaylistmaker.domain.interactors.TrackInteractorImpl
import com.example.myplaylistmaker.domain.repositories.SearchHistoryRepository
import com.example.myplaylistmaker.domain.repositories.SearchRepository
import com.example.myplaylistmaker.domain.repositories.ThemeRepository

object Creator {
    private lateinit var application: App

    fun initApplication(application: App) {
        this.application = application
    }

    private fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(provideSearchRepository())
    }

    fun provideSearchTracks(
        keyword: String,
        onSuccess: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val searchInteractor = provideSearchInteractor()
        searchInteractor.searchTrack(keyword, object : SearchInteractor.SearchConsumer {
            override fun consume(foundTrack: List<Track>) {
                onSuccess(foundTrack)
            }

            override fun onError(error: Throwable) {
                onError(error)
            }
        })
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
        this.sharedPreferences = sharedPreferences
    }

    val repository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(sharedPreferences)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(repository)
    }
}
