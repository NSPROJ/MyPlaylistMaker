package com.example.myplaylistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val data: List<Track>,
    private val onTrackSelected: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackSelected(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}