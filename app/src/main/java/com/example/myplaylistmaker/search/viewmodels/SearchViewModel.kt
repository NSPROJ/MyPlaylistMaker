package com.example.myplaylistmaker.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.search.domain.api.SearchInteractor

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModel() {

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
        searchInteractor.searchTrack(keyword, object : SearchInteractor.SearchConsumer {
            override fun consume(foundTrack: List<Track>) {
                _isLoading.postValue(false)
                if (foundTrack.isNotEmpty()) {
                    _tracks.postValue(foundTrack)
                    _uiState.postValue(UIState.ShowResults)
                } else {
                    _uiState.postValue(UIState.ShowEmptyResult)
                }
            }

            override fun onError(error: Throwable) {
                _isLoading.postValue(false)
                _uiState.postValue(UIState.ShowError)
            }
        })
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
        data object ShowEmptyResult : UIState()
        data object ShowError : UIState()
    }
}