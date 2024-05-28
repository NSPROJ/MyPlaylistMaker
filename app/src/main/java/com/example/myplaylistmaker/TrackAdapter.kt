package com.example.myplaylistmaker

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackAdapter(private val trackList: List<Track>, private val connectivityManager: ConnectivityManager) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>()  {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackImageView: ImageView = itemView.findViewById(R.id.trackImageView)
        val trackTitleTextView: TextView = itemView.findViewById(R.id.trackTitleTextView)
        val artistTimeTextView: TextView = itemView.findViewById(R.id.artistTimeTextView)
    }
    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_items, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val currentItem = trackList[position]

        holder.trackTitleTextView.text = currentItem.trackName
        holder.artistTimeTextView.text = "${currentItem.artistName} • ${currentItem.trackTime}"

        val radius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)
        val roundedCorners = RoundedCorners(radius)
        val requestOptions = RequestOptions.bitmapTransform(roundedCorners)

        if (isInternetAvailable()) {
            Glide.with(holder.itemView)
                .load(currentItem.artworkUrl100)
                .apply(requestOptions)
                .into(holder.trackImageView)
        } else {
            holder.trackImageView.setImageResource(R.drawable.placeholder)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}