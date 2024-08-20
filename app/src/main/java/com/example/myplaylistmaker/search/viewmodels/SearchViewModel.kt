package com.example.myplaylistmaker.search.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.creator.Creator
import com.example.myplaylistmaker.search.domain.Track

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> = _searchHistory

    private val _uiState = MutableLiveData<UIState>()
    val uiState: LiveData<UIState> = _uiState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        updateSearchHistory()
    }

    fun searchTracks(keyword: String) {
        _isLoading.postValue(true)
        Creator.provideSearchTracks(
            keyword,
            onSuccess = { foundTracks ->
                _isLoading.postValue(false)
                if (foundTracks.isNotEmpty()) {
                    _tracks.postValue(foundTracks)
                    _uiState.postValue(UIState.ShowResults)
                } else {
                    _uiState.postValue(
                        UIState.ShowPlaceholder(
                            message = getApplication<Application>().getString(R.string.nothing_to_show),
                            imageResLight = R.drawable.ic_placeholder_light,
                            imageResDark = R.drawable.ic_placeholder_night
                        )
                    )
                }
            },
            onError = {
                _isLoading.postValue(false)
                _uiState.postValue(
                    UIState.ShowError(
                        message = getApplication<Application>().getString(R.string.internet_issue),
                        imageResLight = R.drawable.ic_placeholder_no_light,
                        imageResDark = R.drawable.ic_placeholder_no_night
                    )
                )
            }
        )
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        updateSearchHistory()
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        updateSearchHistory()
    }

    private fun updateSearchHistory() {
        _searchHistory.value = searchHistoryInteractor.getHistory()
    }

    sealed class UIState {
        data object ShowResults : UIState()
        data class ShowPlaceholder(
            val message: String,
            val imageResLight: Int,
            val imageResDark: Int
        ) : UIState()

        data class ShowError(val message: String, val imageResLight: Int, val imageResDark: Int) :
            UIState()
    }
}