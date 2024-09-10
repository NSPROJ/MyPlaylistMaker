package com.example.myplaylistmaker.di

import com.example.myplaylistmaker.media.viewModels.FavoritesViewModel
import com.example.myplaylistmaker.media.viewModels.PlaylistsViewModel
import com.example.myplaylistmaker.player.viewmodels.PlayerViewModel
import com.example.myplaylistmaker.player.viewmodels.TrackViewModel
import com.example.myplaylistmaker.search.viewmodels.SearchViewModel
import com.example.myplaylistmaker.settings.viewmodels.ThemeViewModel
import com.example.myplaylistmaker.sharing.viewmodels.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SearchViewModel(get(), get()) }
    viewModel { TrackViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PlayerViewModel() }
    viewModel { ThemeViewModel(get()) }
    viewModel { FavoritesViewModel() }
    viewModel { PlaylistsViewModel() }
}