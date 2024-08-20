package com.example.myplaylistmaker.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.player.ui.TrackViewHolder
import com.example.myplaylistmaker.search.domain.Track

class TrackAdapter(
    private var data: MutableList<Track>,
    private val onTrackSelectedHistory: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackSelectedHistory(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}