package com.example.myplaylistmaker.media.domain.repositories

import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    suspend fun getFavorites(): Flow<List<Track>>

    suspend fun insertFavorite(track: Track)

    suspend fun deleteFavorite(track: Track)

}