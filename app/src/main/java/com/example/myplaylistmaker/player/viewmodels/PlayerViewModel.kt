@file:Suppress("DEPRECATION")

package com.example.myplaylistmaker.player.viewmodels

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel : ViewModel(), LifecycleObserver {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _isPlaying = MutableLiveData<Boolean>()
    private val _currentPosition = MutableLiveData<Int>()
    private val _trackDuration = MutableLiveData<String>()
    private val _formattedCurrentPosition = MediatorLiveData<String>()
    val formattedCurrentPosition: LiveData<String> = _formattedCurrentPosition
    private var savedPosition = 0
    private var updateJob: Job? = null

    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val trackDuration: LiveData<String> get() = _trackDuration

    init {
        _formattedCurrentPosition.addSource(_currentPosition) { position ->
            _formattedCurrentPosition.value = formatTime(position)
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    fun initMediaPlayer(trackUrl: String, duration: Long) {
        mediaPlayer.apply {
            setDataSource(trackUrl)
            prepareAsync()
            seekTo(savedPosition)
            setOnCompletionListener { onPlaybackComplete() }
        }
        _trackDuration.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
    }

    fun startPlayback() {
        mediaPlayer.start()
        _isPlaying.value = true
        startUpdatingProgress()
    }

    fun pausePlayback() {
        mediaPlayer.pause()
        savedPosition = mediaPlayer.currentPosition
        _isPlaying.value = false
        stopUpdatingProgress()
    }

    private fun startUpdatingProgress() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                val currentPositionInSeconds = mediaPlayer.currentPosition / 1000
                _currentPosition.postValue(currentPositionInSeconds)
                delay(300)
            }
        }
    }

    private fun stopUpdatingProgress() {
        updateJob?.cancel()
    }

    private fun onPlaybackComplete() {
        _isPlaying.value = false
        savedPosition = 0
        _currentPosition.value = 0
        stopUpdatingProgress()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        stopUpdatingProgress()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePause() {
        pausePlayback()
    }
}