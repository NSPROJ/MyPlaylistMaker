package com.example.myplaylistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.databinding.BottomSheetItemBinding
import com.example.myplaylistmaker.media.domain.Playlist

class PlaylistTracksAdapter(

    private val data: List<Playlist>,
    private val onPlaylistItemListener: (Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistTracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return PlaylistTracksViewHolder(
            BottomSheetItemBinding.inflate(
                layoutInspector,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PlaylistTracksViewHolder, position: Int) {
        val playlist = data[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistItemListener.invoke(playlist)
        }
    }
}