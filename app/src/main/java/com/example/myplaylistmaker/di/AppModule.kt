package com.example.myplaylistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.myplaylistmaker.search.data.network.ApiService
import com.example.myplaylistmaker.search.data.network.NetworkClient
import com.example.myplaylistmaker.search.data.network.RetrofitNetworkClient
import com.example.myplaylistmaker.sharing.data.repositories.ExternalNavigatorImpl
import com.example.myplaylistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single { provideSharedPreferences(androidContext(), "app_prefs") }

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }

    single<NetworkClient> { RetrofitNetworkClient(get()) }
}

fun provideSharedPreferences(context: Context, key: String): SharedPreferences {
    return context.getSharedPreferences(key, Context.MODE_PRIVATE)
}