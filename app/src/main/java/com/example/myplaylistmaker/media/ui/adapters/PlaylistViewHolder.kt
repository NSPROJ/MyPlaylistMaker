package com.example.myplaylistmaker.media.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.PlaylistItemBinding
import com.example.myplaylistmaker.media.domain.Playlist

class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(playlist: Playlist) {
        Log.d("PLAYLIST_PATH", "Loading image from: ${playlist.path}")

        Glide.with(itemView)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(
                            16
                        )
                    )
                )
            )
            .into(binding.playlistImage)

        binding.playlistName.text = playlist.playlistName
        binding.countTracks.text = "${playlist.count} ${getEnding(playlist.count)}"
    }

    private fun getEnding(number: Int): String {
        val lastTwoDigits = number % 100
        val lastDigit = number % 10

        return when {
            lastTwoDigits in 11..14 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }
    }
}