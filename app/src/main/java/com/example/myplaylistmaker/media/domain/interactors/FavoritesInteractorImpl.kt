package com.example.myplaylistmaker.media.domain.interactors

import com.example.myplaylistmaker.media.domain.repositories.FavoritesRepository
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }

    override suspend fun insertFavorite(track: Track) {
        return favoritesRepository.insertFavorite(track)
    }

    override suspend fun deleteFavorite(track: Track) {
        return favoritesRepository.deleteFavorite(track)
    }

}