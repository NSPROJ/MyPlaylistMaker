package com.example.myplaylistmaker.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.api.SearchHistoryInteractor
import com.example.myplaylistmaker.search.domain.api.SearchInteractor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            searchInteractor.searchTrack(keyword)
                .catch {
                    _isLoading.postValue(false)
                    _uiState.postValue(UIState.ShowError)
                }
                .collect { foundTracks ->
                    _isLoading.postValue(false)
                    if (foundTracks.isNotEmpty()) {
                        _tracks.postValue(foundTracks)
                        _uiState.postValue(UIState.ShowResults)
                    } else {
                        _uiState.postValue(UIState.ShowEmptyResult)
                    }
                }
        }
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