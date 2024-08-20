package com.example.myplaylistmaker.player.viewmodels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _isPlaying = MutableLiveData<Boolean>()
    private val _currentPosition = MutableLiveData<Int>()
    private val _trackDuration = MutableLiveData<String>()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val currentPosition: LiveData<Int> get() = _currentPosition
    val trackDuration: LiveData<String> get() = _trackDuration

    private var savedPosition = 0

    fun initMediaPlayer(trackUrl: String, duration: Long) {
        mediaPlayer.apply {
            setDataSource(trackUrl)
            prepare()
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
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updateProgressRunnable)
    }
}