package com.example.myplaylistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.myplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myplaylistmaker.data.repositories.SearchRepositoryImpl
import com.example.myplaylistmaker.data.repositories.ThemeRepositoryImp
import com.example.myplaylistmaker.data.repositories.TrackRepositoryImpl
import com.example.myplaylistmaker.domain.domain.Track
import com.example.myplaylistmaker.domain.domain.api.SearchInteractor
import com.example.myplaylistmaker.domain.domain.api.ThemeInteractor
import com.example.myplaylistmaker.domain.domain.api.TrackInteractor
import com.example.myplaylistmaker.domain.domain.interactors.SearchInteractorImpl
import com.example.myplaylistmaker.domain.domain.interactors.ThemeInteractorImpl
import com.example.myplaylistmaker.domain.domain.interactors.TrackInteractorImpl
import com.example.myplaylistmaker.domain.domain.repositories.SearchRepository
import com.example.myplaylistmaker.domain.domain.repositories.ThemeRepository

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
}