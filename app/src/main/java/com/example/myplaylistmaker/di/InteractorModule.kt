package com.example.myplaylistmaker.di

import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.player.domain.interactors.TrackInteractorImpl
import com.example.myplaylistmaker.search.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.search.domain.api.SearchInteractor
import com.example.myplaylistmaker.search.domain.interactors.SearchHistoryInteractorImpl
import com.example.myplaylistmaker.search.domain.interactors.SearchInteractorImpl
import com.example.myplaylistmaker.settings.domain.api.ThemeInteractor
import com.example.myplaylistmaker.settings.domain.interactors.ThemeInteractorImpl
import com.example.myplaylistmaker.sharing.domain.api.SharingInteractor
import com.example.myplaylistmaker.sharing.domain.interactors.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SharingInteractor> { SharingInteractorImpl(get()) }
    single<SearchInteractor> { SearchInteractorImpl(get()) }
    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<TrackInteractor> { TrackInteractorImpl(get()) }
}