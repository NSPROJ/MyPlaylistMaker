package com.example.myplaylistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.player.ui.TrackViewHolder
import com.example.myplaylistmaker.search.domain.Track

class TracksAdapter(
    private val data: MutableList<Track>,
    private val onTrackSelectedHistory: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Boolean
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
        holder.itemView.setOnLongClickListener {
            onTrackLongClick(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}