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
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel : ViewModel(), LifecycleObserver {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _isPlaying = MutableLiveData<Boolean>()
    private val _currentPosition = MutableLiveData<Int>()
    private val _trackDuration = MutableLiveData<String>()
    private val _formattedCurrentPosition = MediatorLiveData<String>()
    val formattedCurrentPosition: LiveData<String> = _formattedCurrentPosition
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val trackDuration: LiveData<String> get() = _trackDuration

    private var savedPosition = 0

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
        handler.post(updateProgressRunnable)
    }

    fun pausePlayback() {
        mediaPlayer.pause()
        savedPosition = mediaPlayer.currentPosition
        _isPlaying.value = false
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val currentPositionInSeconds = mediaPlayer.currentPosition / 1000
            _currentPosition.value = currentPositionInSeconds
            handler.postDelayed(this, 1000)
        }
    }

    private fun onPlaybackComplete() {
        _isPlaying.value = false
        savedPosition = 0
        _currentPosition.value = 0
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updateProgressRunnable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePause() {
        pausePlayback()
    }
}