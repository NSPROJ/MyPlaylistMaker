package com.example.myplaylistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackImageView: ImageView = itemView.findViewById(R.id.trackImageView)
        private val trackTitleTextView: TextView = itemView.findViewById(R.id.trackTitleTextView)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val artistTimeTextView: TextView = itemView.findViewById(R.id.artistTimeTextView)

        fun bind(track: Track) {
            trackTitleTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            artistTimeTextView.text = track.trackTime

            val radius = itemView.context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)
            val roundedCorners = RoundedCorners(radius)
            val requestOptions = RequestOptions.bitmapTransform(roundedCorners).placeholder(R.drawable.placeholder)

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .apply(requestOptions)
                .into(trackImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}