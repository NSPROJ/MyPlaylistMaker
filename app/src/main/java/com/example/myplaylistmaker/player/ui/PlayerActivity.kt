package com.example.myplaylistmaker.player.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.ActivityPlayerBinding
import com.example.myplaylistmaker.player.viewmodels.PlayerViewModel
import com.example.myplaylistmaker.player.viewmodels.TrackViewModel
import com.example.myplaylistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_KEY = "track"
    }

    private var _binding: ActivityPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlayerViewModel>()
    private val trackViewModel by viewModel<TrackViewModel>()
    private lateinit var liked: ImageView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var progressTextView: TextView
    private lateinit var favoriteButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater).apply { setContentView(root) }

        playButton = binding.imageView3
        pauseButton = binding.imageView3pause
        progressTextView = binding.timeDur
        favoriteButton = binding.favImage1
        liked = binding.favLiked

        val intentTrack = intent.getParcelableExtra<Track>(TRACK_KEY)
        if (intentTrack != null) {
            trackViewModel.initTrack(intentTrack, true)

            intentTrack.previewUrl.let { url ->
                viewModel.initMediaPlayer(url)
            }
        } else {
            Log.w("PlayerActivity", "null track")
        }

        lifecycle.addObserver(viewModel)

        trackViewModel.track.observe(this) { track ->
            if (track != null) {
                viewModel.initMediaPlayer(track.previewUrl, track.trackTimeMillis)
            }}

        favoriteButton.setOnClickListener {
            trackViewModel.toggleFavorite()
        }

        playButton.setOnClickListener(debounceClick { onPlayButtonClick() })
        pauseButton.setOnClickListener(debounceClick { onPauseButtonClick() })

        binding.imageView.setOnClickListener { onBackPressed() }

        observeViewModel()
        updateUI()
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

        viewModel.formattedCurrentPosition.observe(this) { formattedTime ->
            progressTextView.text = formattedTime
        }
        trackViewModel.track.observe(this) { track ->
            track?.let {
                liked.visibility = if (it.isFavorite) View.VISIBLE else View.INVISIBLE
                updateUI()
            }
        }

        viewModel.trackDuration.observe(this) { duration ->
            findViewById<TextView>(R.id.songDuration).text = duration
            updateUI()
        }
    }

    override fun onBackPressed() {
        if (viewModel.isPlaying.value == true) {
            viewModel.pausePlayback()
        }
        super.onBackPressed()
    }

    private fun updateUI() {
        val track = trackViewModel.track.value ?: return

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

        findViewById<TextView>(R.id.year).text = trackViewModel.formatReleaseDate(track.releaseDate)
        findViewById<TextView>(R.id.genre).text = track.primaryGenreName
        findViewById<TextView>(R.id.country1).text = track.country

        loadArtwork()
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

    private fun loadArtwork() {
        val albumImageView = findViewById<ImageView>(R.id.albumCover)
        val placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.placeholdertrue)

        Glide.with(this)
            .load(trackViewModel.getCoverArtwork())
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

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}