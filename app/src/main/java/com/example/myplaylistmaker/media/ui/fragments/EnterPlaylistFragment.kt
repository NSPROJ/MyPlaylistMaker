package com.example.myplaylistmaker.media.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.FragmentEnterPlaylistBinding
import com.example.myplaylistmaker.main.ui.MainActivity
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.ui.adapters.TracksAdapter
import com.example.myplaylistmaker.media.viewModels.EnterPlaylistViewModel
import com.example.myplaylistmaker.player.ui.PlayerActivity
import com.example.myplaylistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EnterPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentEnterPlaylistBinding
    private lateinit var toolbar: Toolbar
    private lateinit var trackAdapter: TracksAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlist: Playlist

    private val viewModel by viewModel<EnterPlaylistViewModel>()
    private val tracks = mutableListOf<Track>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()

        val bundle = arguments
        if (bundle != null) {
            val playlistId = bundle.getInt("playlistId")
            viewModel.loadPlaylist(playlistId)
            viewModel.playlist.observe(viewLifecycleOwner) { playlistFromViewModel ->
                playlist = playlistFromViewModel
                updatePlaylistUI(playlist)
            }

            viewModel.loadTracksForPlaylist(playlistId)
            viewModel.tracks.observe(viewLifecycleOwner) { tracksFromViewModel ->
                this.tracks.clear()
                this.tracks.addAll(tracksFromViewModel)
                trackAdapter.notifyDataSetChanged()
                updateEmptyListVisibility()

                val totalTimeMillis = tracksFromViewModel.sumOf { it.trackTimeMillis }
                val totalMinutes = totalTimeMillis / (1000 * 60)
                binding.duration.text = "$totalMinutes ${getEndingMin(totalMinutes.toInt())}"
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetSettings)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.optionPlaylist.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        toolbar = binding.toolbar.apply {
            val callback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    findNavController().navigateUp()
                }
            setNavigationOnClickListener {
                callback.handleOnBackPressed()
            }
        }

        trackAdapter = TracksAdapter(tracks, { track -> onTrackSelected(track) }, { track ->
            showDeleteTrackDialog(track)
            true
        })
        binding.recyclerTracks.layoutManager = LinearLayoutManager(context)
        binding.recyclerTracks.adapter = trackAdapter

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.sheetDelete.setOnClickListener {
            (parentFragment as? BottomSheetDialogFragment)?.dismiss()
            showDeleteConfirmationDialog()
        }

        binding.sheetShare.setOnClickListener {
            sharePlaylist()
        }

        binding.sheetRedactor.setOnClickListener {
            navigateToEditPlaylist()
        }
    }

    private fun updateEmptyListVisibility() {
        if (tracks.isEmpty()) {
            binding.emptyListTextView.visibility = View.VISIBLE
            binding.recyclerTracks.visibility = View.GONE
        } else {
            binding.emptyListTextView.visibility = View.GONE
            binding.recyclerTracks.visibility = View.VISIBLE
        }
    }

    private fun navigateToEditPlaylist() {
        val bundle = Bundle()
        bundle.putInt("playlistId", playlist.playlistId)
        findNavController().navigate(R.id.action_enterPlaylistFragment_to_newPlayListFragment, bundle)
    }

    private fun updatePlaylistUI(playlist: Playlist) {
        val playlistName = playlist.playlistName
        val description = playlist.description
        val count = playlist.count
        val path = playlist.path

        binding.namePlayList.text = playlistName
        binding.bsNamePlayList.text = playlistName
        binding.descriptionPlayList.text = description
        binding.count.text = "$count".plus(" ").plus(getEnding(count))
        binding.bsCountPlayList.text = "$count".plus(" ").plus(getEnding(count))

        if (path != null) {
            Glide.with(requireContext())
                .load(path)
                .placeholder(R.drawable.placeholder)
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
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imagePlayList)

            Glide.with(requireContext())
                .load(path)
                .placeholder(R.drawable.placeholder)
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
                .into(binding.bsPlayList)



            viewModel.loadTracksForPlaylist(playlist.playlistId)
            viewModel.tracks.observe(viewLifecycleOwner) { tracksFromViewModel ->
                this.tracks.clear()
                this.tracks.addAll(tracksFromViewModel)
                trackAdapter.notifyDataSetChanged()

                val totalTimeMillis = tracksFromViewModel.sumOf { it.trackTimeMillis }
                val totalMinutes = totalTimeMillis / (1000 * 60)
                binding.duration.text = "$totalMinutes ${getEndingMin(totalMinutes.toInt())}"
            }
        }


        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetSettings)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.optionPlaylist.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }


        toolbar = binding.toolbar.apply {
            val callback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    findNavController().navigateUp()
                }
            setNavigationOnClickListener {
                callback.handleOnBackPressed()
            }
        }

        trackAdapter = TracksAdapter(tracks, { track -> onTrackSelected(track) }, { track ->
            showDeleteTrackDialog(track)
            true
        })
        binding.recyclerTracks.layoutManager = LinearLayoutManager(context)
        binding.recyclerTracks.adapter = trackAdapter

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.sheetDelete.setOnClickListener {
            (parentFragment as? BottomSheetDialogFragment)?.dismiss()
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val playlistName = playlist.playlistName
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.be_sure_to_delete) + " \"$playlistName\"?")
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { _, _ ->
                deletePlaylist()
            }
            .show()
    }

    private fun deletePlaylist() {
        viewModel.deletePlaylist(playlist)
        parentFragmentManager.popBackStack()
    }

    private fun sharePlaylist() {
        if (tracks.isEmpty()) {
            showToast(getString(R.string.no_tracks))
        } else {
            val playlistName = arguments?.getString("playlistName") ?: ""
            val description = arguments?.getString("description") ?: ""
            val trackCount = tracks.size
            val message = buildString {
                appendLine(playlistName)
                appendLine(description)
                appendLine("$trackCount ${getEnding(trackCount)}")
                tracks.forEachIndexed { index, track ->
                    val formattedTime = SimpleDateFormat("mm:ss",
                        Locale.getDefault()).format(track.trackTimeMillis)
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($formattedTime)")
                }
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            ContextCompat.startActivity(requireContext(), Intent.createChooser(shareIntent, getString(R.string.share)), null)
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteTrackDialog(track: Track): Boolean {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.delete_question))
            .setPositiveButton("Да") { _, _ ->
                deleteTrack(track)
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        return true
    }

    private fun deleteTrack(track: Track) {
        val playlistId = arguments?.getInt("playlistId") ?: return
        viewModel.deleteTrackFromPlaylist(track, playlistId)
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

    private fun getEndingMin(number: Int): String {
        val lastTwoDigits = number % 100
        val lastDigit = number % 10

        return when {
            lastTwoDigits in 11..14 -> "минут"
            lastDigit == 1 -> "минута"
            lastDigit in 2..4 -> "минуты"
            else -> "минут"
        }
    }

    private fun onTrackSelected(track: Track) {
        val intent = Intent(context, PlayerActivity::class.java).apply {
            putExtra(TRACK_KEY, track)
            viewModel.viewModelScope.launch {

            }
        }
        context?.startActivity(intent)
    }

    companion object {
        private const val TRACK_KEY = "track"
    }
}
    

    