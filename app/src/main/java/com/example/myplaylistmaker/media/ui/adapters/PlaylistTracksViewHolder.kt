package com.example.myplaylistmaker.media.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.BottomSheetItemBinding
import com.example.myplaylistmaker.media.domain.Playlist

class PlaylistTracksViewHolder(private val binding: BottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(playlist: Playlist) {
        Glide.with(itemView)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius))
                    )
                )
            )
            .into(binding.playlistImageV)
        binding.albumTitle.text = playlist.playlistName
        binding.trackCount.text = "${playlist.count} ${getEnding(playlist.count)}"
    }

    private fun getEnding(number: Int): String {
        val lastDigit = number % 10
        return when (lastDigit) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
    }
}