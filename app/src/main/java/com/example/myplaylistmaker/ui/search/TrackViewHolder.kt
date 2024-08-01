package com.example.myplaylistmaker.ui.search

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.domain.models.TrackDto
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackImage: ImageView = itemView.findViewById(R.id.trackImageView)
    private val trackName: TextView = itemView.findViewById(R.id.trackTitleTextView)
    private val artist: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val trackTime: TextView = itemView.findViewById(R.id.artistTimeTextView)

    fun bind(item: TrackDto) {
        trackName.text = item.trackName
        artist.text = item.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)

        val roundedCornersRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f,
            itemView.resources.displayMetrics
        ).toInt()

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(roundedCornersRadius)
            )
            .into(trackImage)
    }
}