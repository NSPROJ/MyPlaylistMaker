package com.example.myplaylistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.databinding.FragmentFavoritesBinding
import com.example.myplaylistmaker.db.FavoritesState
import com.example.myplaylistmaker.media.viewModels.FavoritesViewModel
import com.example.myplaylistmaker.player.ui.PlayerActivity
import com.example.myplaylistmaker.player.ui.PlayerActivity.Companion.TRACK_KEY
import com.example.myplaylistmaker.search.domain.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoriteTracks = mutableListOf<Track>()
    private var isClicked = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var iError: ImageView
    private lateinit var tError: TextView
    private lateinit var favoritesAdapter: FavoritesAdapter

    private val favoritesViewModel by viewModel<FavoritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }

        private const val DEBOUNCE_DELAY = 1000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iError = binding.placeholder
        tError = binding.placeholderT
        recyclerView = binding.FavoritesRecyclerView

        favoritesAdapter = FavoritesAdapter(favoriteTracks) {}
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoritesAdapter

        favoritesViewModel.observeState().observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        favoritesViewModel.loadFavorites()

        favoritesAdapter.onTrackClickListener = { track ->
            openTrack(track)
        }
    }

    private fun updateUI(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> {
                if (state.trackList.isNotEmpty()) {
                    favoriteTracks.clear()
                    favoriteTracks.addAll(state.trackList)
                    favoritesAdapter.notifyDataSetChanged()
                    iError.visibility = View.GONE
                    tError.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                } else {
                    iError.visibility = View.VISIBLE
                    tError.visibility = View.VISIBLE
                }
            }

            is FavoritesState.Error -> {
                iError.visibility = View.VISIBLE
                tError.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.loadFavorites()
    }

    private suspend fun debounce() {
        if (isClicked) {
            isClicked = false
            delay(DEBOUNCE_DELAY)
            isClicked = true
        }
    }

    private fun openTrack(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            debounce()
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(TRACK_KEY, track)
            }
            startActivity(intent)
        }
    }
}