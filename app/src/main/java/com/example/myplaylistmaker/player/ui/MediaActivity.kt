package com.example.myplaylistmaker.player.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.player.viewmodels.PlayerViewModel
import com.example.myplaylistmaker.player.viewmodels.TrackViewModel
import com.example.myplaylistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "track"
    }

    private lateinit var track: Track
    private lateinit var viewModel: PlayerViewModel
    private lateinit var trackViewModel: TrackViewModel
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var progressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        playButton = findViewById(R.id.imageView3)
        pauseButton = findViewById(R.id.imageView3pause)
        progressTextView = findViewById(R.id.time_dur)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[PlayerViewModel::class.java]

        trackViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TrackViewModel::class.java]

        playButton.setOnClickListener(debounceClick { onPlayButtonClick() })
        pauseButton.setOnClickListener(debounceClick { onPauseButtonClick() })

        findViewById<ImageView>(R.id.imageView).setOnClickListener { onBackPressed() }

        trackViewModel.trackInteractor.observe(this) { trackInteractor ->
            track = intent.getParcelableExtra(TRACK_KEY) ?: trackInteractor.getSavedTrack()
            if (intent.hasExtra(TRACK_KEY)) {
                trackInteractor.saveTrack(track)
            }

            viewModel.initMediaPlayer(track.previewUrl, track.trackTimeMillis)

            observeViewModel()
            updateUI()
        }
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
        if (viewModel.isPlaying.value == true) {
            viewModel.pausePlayback()
        } else {
            viewModel.startPlayback()
        }
    }

    private fun onPauseButtonClick() {
        viewModel.pausePlayback()
    }

    private fun observeViewModel() {
        viewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                playButton.visibility = View.INVISIBLE
                pauseButton.visibility = View.VISIBLE
                progressTextView.visibility = View.VISIBLE
            } else {
                pauseButton.visibility = View.GONE
                playButton.visibility = View.VISIBLE
            }
        }

        viewModel.currentPosition.observe(this) { currentPosition ->
            val minutes = currentPosition / 60
            val seconds = currentPosition % 60
            progressTextView.text = String.format("%02d:%02d", minutes, seconds)
        }

        viewModel.trackDuration.observe(this) { duration ->
            val trackDurationTextView = findViewById<TextView>(R.id.songDuration)
            trackDurationTextView.text = duration
        }
    }

    override fun onBackPressed() {
        if (viewModel.isPlaying.value == true) {
            viewModel.pausePlayback()
        }
        super.onBackPressed()
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.songTitle).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.albumName).apply {
            if (track.collectionName.isEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = track.collectionName
            }
        }

        findViewById<TextView>(R.id.year).text = formatReleaseDate(track.releaseDate)
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
