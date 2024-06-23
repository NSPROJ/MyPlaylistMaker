package com.example.myplaylistmaker

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        findViewById<ImageView>(R.id.imageView).setOnClickListener { finish() }

        track = intent.getParcelableExtra("track") ?: getSavedTrack()

        updateUI()
    }

    private fun getSavedTrack(): Track {
        val sharedPreferences = getSharedPreferences("MediaActivityPrefs", Context.MODE_PRIVATE)
        return Track(
            trackName = sharedPreferences.getString("trackName", "")!!,
            artistName = sharedPreferences.getString("artistName", "")!!,
            trackTimeMillis = sharedPreferences.getLong("trackTimeMillis", 0),
            artworkUrl100 = sharedPreferences.getString("artworkUrl100", "")!!,
            trackId = sharedPreferences.getLong("trackId", 0),
            collectionName = sharedPreferences.getString("collectionName", null),
            releaseDate = sharedPreferences.getString("releaseDate", "")!!,
            primaryGenreName = sharedPreferences.getString("primaryGenreName", "")!!,
            country = sharedPreferences.getString("country", "")
        )
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.songTitle).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        val trackDuration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        findViewById<TextView>(R.id.songDuration).apply {
            text = trackDuration
            tag = track.trackTimeMillis
        }

        val albumNameTextView = findViewById<TextView>(R.id.albumName)
        if (track.collectionName.isNullOrEmpty()) {
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
        val placeholderImageView = findViewById<ImageView>(R.id.imageViewPlaceholder)

        placeholderImageView.visibility = View.VISIBLE
        albumImageView.visibility = View.GONE

        Glide.with(this)
            .load(getCoverArtwork())
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(dpToPx(8)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    placeholderImageView.visibility = View.GONE
                    albumImageView.setImageDrawable(resource)
                    albumImageView.visibility = View.VISIBLE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholderImageView.visibility = View.VISIBLE
                    albumImageView.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    placeholderImageView.visibility = View.VISIBLE
                    albumImageView.visibility = View.GONE
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