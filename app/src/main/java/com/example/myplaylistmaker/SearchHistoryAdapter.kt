package com.example.myplaylistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(
    private var historyList: ArrayList<Track>,
    private val onTrackSelected: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_items, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(historyList[position])
        holder.itemView.setOnClickListener {
            onTrackSelected(historyList[position])
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateHistoryList(newHistoryList: ArrayList<Track>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }
}