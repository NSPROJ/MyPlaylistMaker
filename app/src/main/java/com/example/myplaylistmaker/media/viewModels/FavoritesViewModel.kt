package com.example.myplaylistmaker.media.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.db.FavoritesState
import com.example.myplaylistmaker.media.domain.interactors.FavoritesInteractor
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favorites = MutableLiveData<FavoritesState>()

    fun observeState(): LiveData<FavoritesState> = _favorites

    fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites()
                .collect { tracks -> processResult(tracks) }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Error("Favorites list is empty"))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }


    private fun renderState(state: FavoritesState) {
        _favorites.postValue(state)
    }
}
