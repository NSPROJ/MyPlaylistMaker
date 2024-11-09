package com.example.myplaylistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.player.ui.PlayerActivity
import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.ui.adapters.SearchHistoryAdapter
import com.example.myplaylistmaker.search.ui.adapters.TrackAdapter
import com.example.myplaylistmaker.search.viewmodels.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonClear: Button
    private lateinit var historyTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var placeholderText: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar

    private var savedText: String = ""
    private val trackList = arrayListOf<Track>()
    private val viewModel by viewModel<SearchViewModel>()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupAdapters()
        setupListeners()
        handleSavedInstanceState(savedInstanceState)
        updateHistoryVisibility()
        setupObservers()

        clearButton.setOnClickListener {
            clearSearchField()
        }
    }

    override fun onResume() {
        super.onResume()
        placeholderImage.visibility = View.GONE
        placeholderText.visibility = View.GONE
    }

    private fun initializeViews(view: View) {
        editText = view.findViewById(R.id.editText)
        clearButton = view.findViewById(R.id.clearButton)
        buttonClear = view.findViewById(R.id.button_clear)
        historyTitle = view.findViewById(R.id.historyTitle)
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        placeholderText = view.findViewById(R.id.placeholderText)
        placeholderImage = view.findViewById(R.id.placeholderImage)
        refreshButton = view.findViewById(R.id.button_refresh)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(trackList) { track -> onTrackSelected(track) }
        historyAdapter = SearchHistoryAdapter(emptyList()) { track -> onTrackSelected(track) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = trackAdapter
    }
    private fun hideHistory() {
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        if (recyclerView.adapter is SearchHistoryAdapter) {
            recyclerView.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateHistoryVisibility()
            } else {
                hideHistory()
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                if (!s.isNullOrEmpty()) {
                    clearPlaceholder()
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(2000)
                        searchTracks(s.toString())
                    }
                    updateClearButtonVisibility()

                } else {
                    clearSearchField()
                    showHistory()

                }
            }

            override fun afterTextChanged(s: Editable?) {
                clearPlaceholder()

            }
        })

        buttonClear.setOnClickListener { clearSearchHistory() }
        refreshButton.setOnClickListener { retrySearch() }
    }

    private fun clearPlaceholder() {
        placeholderText.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun retrySearch() {
        val searchText = editText.text.toString()
        if (searchText.isNotEmpty()) {
            searchTracks(searchText)
        }
    }

    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            updateTrackList(tracks)
        }

        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.updateHistoryList(history)
            updateHistoryVisibility()
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchViewModel.UIState.ShowResults -> showResults()
                is SearchViewModel.UIState.ShowEmptyResult -> showEmptyResultsMessage()
                is SearchViewModel.UIState.ShowError -> showErrorMessage()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar() else hideProgressBar()
        }
    }

    private fun showResults() {
        recyclerView.visibility = View.VISIBLE
        placeholderText.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showEmptyResultsMessage() {
        placeholderText.text = getString(R.string.nothing_to_show)
        placeholderImage.setImageResource(R.drawable.ic_placeholder_light)
        placeholderText.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showErrorMessage() {
        placeholderText.text = getString(R.string.internet_issue)
        placeholderImage.setImageResource(R.drawable.ic_placeholder_no_light)
        placeholderText.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun updateTrackList(newTrackList: List<Track>?) {
        trackList.clear()
        newTrackList?.let { trackList.addAll(it)
            trackAdapter.notifyItemRangeChanged(0, trackList.size)}
    }


    private fun showHistory() {
        val hasHistory = viewModel.searchHistory.value?.isNotEmpty() ?: false

        val searchFieldIsEmpty = editText.text.isNullOrBlank()

        val hasEditTextFocus = editText.hasFocus()

        if (hasHistory && searchFieldIsEmpty && hasEditTextFocus) {
            recyclerView.adapter = historyAdapter

            historyTitle.visibility = View.VISIBLE
            buttonClear.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            placeholderText.visibility = View.GONE
            placeholderImage.visibility = View.GONE
        } else {
            historyTitle.visibility = View.GONE
            buttonClear.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun clearSearchField() {
        editText.text.clear()
        viewModel.clearTracks()
        trackAdapter.notifyItemRangeRemoved(0, trackList.size)
        showHistory()
        updateTrackList(emptyList())
        clearButton.visibility = View.GONE
        placeholderImage.visibility = View.GONE
    }

    private fun clearSearchHistory() {
        viewModel.clearSearchHistory()
        recyclerView.adapter = trackAdapter
    }

    private fun updateClearButtonVisibility() {
        clearButton.visibility = if (editText.text.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun searchTracks(keyword: String) {
        viewModel.searchTracks(keyword)
        recyclerView.adapter = trackAdapter
    }

    private fun handleSavedInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            savedText = it.getString(SAVED_TEXT_KEY, "")
            editText.setText(savedText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_TEXT_KEY, editText.text.toString())
    }

    private fun onTrackSelected(track: Track) {
        val intent = Intent(context, PlayerActivity::class.java).apply {
            putExtra(TRACK_KEY, track)
            viewModel.viewModelScope.launch {
                viewModel.addTrackToHistory(track)

            }
        }
        context?.startActivity(intent)
    }

    companion object {
        private const val SAVED_TEXT_KEY = "savedText"
        private const val TRACK_KEY = "track"
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        refreshButton.visibility = View.GONE
        placeholderText.visibility = View.GONE
        placeholderImage.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun updateHistoryVisibility() {
        val hasHistory = viewModel.searchHistory.value?.isNotEmpty() ?: false

        val searchFieldIsEmpty = editText.text.isNullOrBlank()

        val hasEditTextFocus = editText.hasFocus()

        if (hasHistory && searchFieldIsEmpty && hasEditTextFocus) {
            recyclerView.adapter = historyAdapter

            historyTitle.visibility = View.VISIBLE
            buttonClear.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
        } else {
            historyTitle.visibility = View.GONE
            buttonClear.visibility = View.GONE
            recyclerView.visibility = if (trackList.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}