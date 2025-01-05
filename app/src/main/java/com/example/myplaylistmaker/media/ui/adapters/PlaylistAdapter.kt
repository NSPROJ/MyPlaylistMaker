package com.example.myplaylistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.databinding.PlaylistItemBinding
import com.example.myplaylistmaker.media.domain.Playlist

class PlaylistAdapter(private val data: List<Playlist>, private val onPlaylistItemClick: ((Playlist) -> Unit)?) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(PlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = data[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistItemClick?.invoke(playlist)
        }
    }
}
