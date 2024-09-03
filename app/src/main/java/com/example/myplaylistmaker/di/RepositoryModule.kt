package com.example.myplaylistmaker.di

import com.example.myplaylistmaker.player.data.repositories.TrackRepositoryImpl
import com.example.myplaylistmaker.player.domain.repositories.TrackRepository
import com.example.myplaylistmaker.search.data.repositories.SearchHistoryRepositoryImpl
import com.example.myplaylistmaker.search.data.repositories.SearchRepositoryImpl
import com.example.myplaylistmaker.search.domain.repositories.SearchHistoryRepository
import com.example.myplaylistmaker.search.domain.repositories.SearchRepository
import com.example.myplaylistmaker.settings.data.ThemeRepositoryImpl
import com.example.myplaylistmaker.settings.domain.repositories.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl() }
    single<TrackRepository> { TrackRepositoryImpl(androidContext()) }

}