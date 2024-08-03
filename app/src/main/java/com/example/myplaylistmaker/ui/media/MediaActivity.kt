package com.example.myplaylistmaker.ui.media

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myplaylistmaker.Creator
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.domain.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "track"
    }

    private lateinit var track: Track
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var progressTextView: TextView
    private var isPlaying = false
    private var handler = Handler(Looper.getMainLooper())
    private var savedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        playButton = findViewById(R.id.imageView3)
        pauseButton = findViewById(R.id.imageView3pause)
        progressTextView = findViewById(R.id.time_dur)

        val trackInteractor = Creator.provideTrackInteractor(this)

        playButton.setOnClickListener(debounceClick { onPlayButtonClick() })
        pauseButton.setOnClickListener(debounceClick { onPauseButtonClick() })

        findViewById<ImageView>(R.id.imageView).setOnClickListener { onBackPressed() }

        track = intent.getParcelableExtra(TRACK_KEY) ?: trackInteractor.getSavedTrack()

        if (intent.hasExtra(TRACK_KEY)) {
            trackInteractor.saveTrack(track)
        }

        updateUI()
    }


    private fun debounceClick(onClick: () -> Unit): View.OnClickListener {
        val debounceInterval = 1000L
        var lastClickTime = 0L

        return View.OnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceInterval) {
                lastClickTime = currentTime
                onClick()
            }
        }
    }

    private fun onPlayButtonClick() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun onPauseButtonClick() {
        pausePlayback()
    }

    private fun startPlayback() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepare()
            seekTo(savedPosition)
            start()
            setOnCompletionListener {
                onPlaybackComplete()
            }
        }
        isPlaying = true
        playButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.VISIBLE
        progressTextView.visibility = View.VISIBLE

        handler.post(updateProgressRunnable)
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val currentPosition = it.currentPosition / 1000
                val minutes = currentPosition / 60
                val seconds = currentPosition % 60
                progressTextView.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        savedPosition = mediaPlayer?.currentPosition ?: 0
        isPlaying = false
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
    }

    private fun onPlaybackComplete() {
        isPlaying = false
        savedPosition = 0
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        handler.removeCallbacks(updateProgressRunnable)
        progressTextView.text = getString(R.string.zero_time)
        progressTextView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onBackPressed() {
        if (isPlaying) {
            pausePlayback()
        }
        super.onBackPressed()
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.songTitle).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        val trackDuration =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        findViewById<TextView>(R.id.songDuration).apply {
            text = trackDuration
            tag = track.trackTimeMillis
        }

        val albumNameTextView = findViewById<TextView>(R.id.albumName)
        if (track.collectionName.isEmpty()) {
            albumNameTextView.visibility = View.GONE
        } else {
            albumNameTextView.visibility = View.VISIBLE
            albumNameTextView.text = track.collectionName
        }

        val formattedReleaseDate = formatReleaseDate(track.releaseDate)
        findViewById<TextView>(R.id.year).text = formattedReleaseDate
        findViewById<TextView>(R.id.genre).text = track.primaryGenreName
        findViewById<TextView>(R.id.country1).text = track.country

        loadArtwork()
    }

    private fun formatReleaseDate(date: String): String {
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

    private fun loadArtwork() {
        val albumImageView = findViewById<ImageView>(R.id.albumCover)
        val placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.placeholdertrue)

        Glide.with(this)
            .load(getCoverArtwork())
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(dpToPx(8)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    albumImageView.setImageDrawable(resource)
                    albumImageView.visibility = View.VISIBLE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    albumImageView.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    albumImageView.setImageDrawable(placeholderDrawable)
                    albumImageView.visibility = View.VISIBLE
                }
            })
    }

    private fun getCoverArtwork(): String {
        return track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
