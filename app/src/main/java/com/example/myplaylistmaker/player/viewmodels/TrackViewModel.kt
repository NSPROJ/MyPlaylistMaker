package com.example.myplaylistmaker.player.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.db.FavoritesState
import com.example.myplaylistmaker.media.domain.interactors.FavoritesInteractor
import com.example.myplaylistmaker.player.domain.api.TrackInteractor
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewModel(
    private val trackInteractor: TrackInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _track = MutableLiveData<Track?>()
    val track: LiveData<Track?> = _track

    private val _favorite = MutableLiveData<Boolean>()
    val favorite: LiveData<Boolean> = _favorite

    private val _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favoritesState

    init {
        fetchFavorites()
    }

    fun isFavorite(track: Track): LiveData<Boolean> {
        val isFavoriteLiveData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.getFavorites()
            isFavoriteLiveData.postValue(isFavorite.first().contains(track))
        }
        return isFavoriteLiveData
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { favorites ->
                if (favorites.isNotEmpty()) {
                    _favoritesState.postValue(FavoritesState.Content(favorites))
                } else {
                    _favoritesState.postValue(FavoritesState.Error("Список избранных пуст"))
                }
            }
        }
    }

    fun onFavoriteClicked(currentTrack: Track) {
        viewModelScope.launch {
            val currentIsFavorite = currentTrack.isFavorite
            if (!currentIsFavorite) {
                favoritesInteractor.insertFavorite(currentTrack)
                _favorite.postValue(!currentIsFavorite)
            } else {
                favoritesInteractor.deleteFavorite(currentTrack)
            }
            _favorite.postValue(!currentIsFavorite)
            fetchFavorites()
        }
    }

    fun initTrack(intentTrack: Track?) {
        if (intentTrack != null) {
            _track.value = intentTrack
            viewModelScope.launch {
                val isFavorite = favoritesInteractor.getFavorites()
                _favorite.postValue(isFavorite.first().contains(intentTrack))
            }
            saveTrack(intentTrack)
        } else {
            val savedTrack = getSavedTrack()
            _track.value = savedTrack
            savedTrack.let {
                viewModelScope.launch {
                    val isFavorite = favoritesInteractor.getFavorites()
                    _favorite.postValue(isFavorite.first().contains(savedTrack))
                }
            }
        }
    }

    private fun saveTrack(track: Track) {
        trackInteractor.saveTrack(track)
    }

    private fun getSavedTrack(): Track {
        return trackInteractor.getSavedTrack()
    }

    fun formatReleaseDate(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val parsedDate = parser.parse(date)
            if (parsedDate != null) {
                formatter.format(parsedDate)
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    fun getCoverArtwork(): String {
        return _track.value?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""
    }
}