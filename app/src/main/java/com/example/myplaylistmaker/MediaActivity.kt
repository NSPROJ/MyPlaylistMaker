package com.example.myplaylistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private lateinit var trackName: String
    private lateinit var artistName: String
    private var trackTimeMillis: Long = 0
    private lateinit var artworkUrl100: String
    private var collectionName: String? = null
    private lateinit var releaseDate: String
    private lateinit var primaryGenreName: String
    private var country: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        findViewById<ImageView>(R.id.imageView).setOnClickListener { finish() }

        val sharedPreferences = getSharedPreferences("MediaActivityPrefs", Context.MODE_PRIVATE)

        trackName = intent.getStringExtra("trackName") ?: sharedPreferences.getString("trackName", "")!!
        artistName = intent.getStringExtra("artistName") ?: sharedPreferences.getString("artistName", "")!!
        trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0).takeIf { it != 0L } ?: sharedPreferences.getLong("trackTimeMillis", 0)
        artworkUrl100 = intent.getStringExtra("artworkUrl100") ?: sharedPreferences.getString("artworkUrl100", "")!!
        collectionName = intent.getStringExtra("collectionName") ?: sharedPreferences.getString("collectionName", null)
        releaseDate = intent.getStringExtra("releaseDate") ?: sharedPreferences.getString("releaseDate", "")!!
        primaryGenreName = intent.getStringExtra("primaryGenreName") ?: sharedPreferences.getString("primaryGenreName", "")!!
        country = intent.getStringExtra("country") ?: sharedPreferences.getString("country", "")!!

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.songTitle).text = trackName
        findViewById<TextView>(R.id.artistName).text = artistName
        val trackDuration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        findViewById<TextView>(R.id.songDuration).apply {
            text = trackDuration
            tag = trackTimeMillis
        }

        val albumNameTextView = findViewById<TextView>(R.id.albumName)
        if (collectionName.isNullOrEmpty()) {
            albumNameTextView.visibility = View.GONE
        } else {
            albumNameTextView.text = formatCollectionName(collectionName!!)
            albumNameTextView.visibility = View.VISIBLE
        }

        val formattedReleaseDate = formatReleaseDate(releaseDate)
        findViewById<TextView>(R.id.year).text = formattedReleaseDate

        findViewById<TextView>(R.id.genre).text = primaryGenreName
        findViewById<TextView>(R.id.country1).text = country

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

    private fun formatCollectionName(name: String): String {
        return if (name.length > 30) {
            "${name.substring(0, 30)}..."
        } else {
            name
        }
    }

    private fun loadArtwork() {
        val albumImageView = findViewById<ImageView>(R.id.albumCover)
        if (isNetworkAvailable()) {
            Glide.with(this)
                .load(getCoverArtwork())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .transform(RoundedCorners(dpToPx(8)))
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(albumImageView)
        } else {
            albumImageView.setImageResource(R.drawable.placeholder)
        }
    }

    private fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("MediaActivityPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("trackName", trackName)
        editor.putString("artistName", artistName)
        editor.putLong("trackTimeMillis", trackTimeMillis)
        editor.putString("artworkUrl100", artworkUrl100)
        editor.putString("collectionName", collectionName)
        editor.putString("releaseDate", releaseDate)
        editor.putString("primaryGenreName", primaryGenreName)
        editor.putString("country", country)

        editor.apply()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}