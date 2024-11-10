package com.example.myplaylistmaker.media.domain.interactors

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun getFavorites(): Flow<List<Track>>

    suspend fun insertFavorite(track: Track)

    suspend fun deleteFavorite(track: Track)
}