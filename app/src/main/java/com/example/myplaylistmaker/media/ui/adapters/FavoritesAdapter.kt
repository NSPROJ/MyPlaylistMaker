package com.example.myplaylistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.player.ui.TrackViewHolder
import com.example.myplaylistmaker.search.domain.Track


class FavoritesAdapter(
    private var data: List<Track>,
    var onTrackClickListener: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        val view = layoutInspector.inflate(R.layout.favorite_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClickListener.invoke(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}