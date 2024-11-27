package com.example.myplaylistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.FragmentPlaylistsBinding
import com.example.myplaylistmaker.db.PlaylistState
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.ui.adapters.PlaylistAdapter
import com.example.myplaylistmaker.media.viewModels.PlaylistsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentPlaylistsBinding == null")

    private lateinit var createButton: Button
    private lateinit var textPlaceholder: View

    private val viewModel by viewModel<PlaylistsViewModel>()
    private val playlists = mutableListOf<Playlist>()
    private val playlistAdapter = PlaylistAdapter(playlists)

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PlaylistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.PlaylistsRecyclerView.adapter = playlistAdapter
        viewModel.loadPlaylists()

        createButton = binding.createButton
        textPlaceholder = binding.mediaPlaceholderTv

        createButton.setOnClickListener {
            findNavController().navigate(R.id.newPlayListFragment)

            val activity = requireActivity() as AppCompatActivity
            activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                View.GONE
        }

        viewModel.visibilityState.observe(viewLifecycleOwner) { visible ->
            if (visible) {
                binding.PlaylistsRecyclerView.visibility = View.VISIBLE
                binding.placeholder.visibility = View.GONE
                textPlaceholder.visibility = View.GONE
            } else {
                binding.PlaylistsRecyclerView.visibility = View.GONE
                binding.placeholder.visibility = View.VISIBLE
                textPlaceholder.visibility = View.VISIBLE
            }
        }

        viewModel.playlistState.observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistState.Content -> {
                    playlists.clear()
                    playlists.addAll(it.playlist)
                    playlistAdapter.notifyDataSetChanged()
                }
                is PlaylistState.Error -> {
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
        val activity = requireActivity() as AppCompatActivity
        activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
