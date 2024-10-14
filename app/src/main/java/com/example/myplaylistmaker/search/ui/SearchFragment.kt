package com.example.myplaylistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private var isRefreshing = false
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
        setupObservers()

        if (savedInstanceState != null) {
            handleSavedInstanceState(savedInstanceState)
        }

        updateHistoryVisibility()
        updateClearButtonVisibility()
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
                is SearchViewModel.UIState.ShowResults -> updateUIForResults()
                is SearchViewModel.UIState.ShowEmptyResult -> displayMessageWithPlaceholder(
                    getString(R.string.nothing_to_show)
                )

                is SearchViewModel.UIState.ShowError -> displayErrorMessage()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar() else hideProgressBar()
        }
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
        trackAdapter = TrackAdapter(trackList) { track -> onTrackSelectedHistory(track) }
        historyAdapter =
            SearchHistoryAdapter(viewModel.searchHistory.value ?: emptyList()) { track ->
                onTrackSelected(track)
            }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = trackAdapter

        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.updateHistoryList(history)
            updateHistoryVisibility()
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
                if (s.toString().isNotEmpty()) {
                    hideHistory()
                    placeholderText.visibility = View.GONE
                    placeholderImage.visibility = View.GONE

                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(2000)
                        if (s != null) {
                            if (s.isNotEmpty()) {
                                searchTracks(s.toString())
                            }
                        }
                    }
                } else {
                    updateHistoryVisibility()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                updateHistoryVisibility()
                clearButton.isVisible = s.toString().isNotEmpty()
            }
        })

        clearButton.setOnClickListener {
            updateTrackList(null)
            clearSearchField()
            clearSearchFieldFocus()

        }

        buttonClear.setOnClickListener {
            clearSearchHistory()
        }

        refreshButton.setOnClickListener {
            val searchText = editText.text.toString()
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }
    }

    private fun handleSavedInstanceState(savedInstanceState: Bundle) {
        savedText = savedInstanceState.getString(SAVED_TEXT_KEY, "")
        editText.setText(savedText)
        if (savedText.isNotEmpty()) {
            searchTracks(savedText)
            updateUIForResults()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_TEXT_KEY, editText.text.toString())
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun searchTracks(keyword: String) {
        viewModel.searchTracks(keyword)
    }

    private fun updateTrackList(newTrackList: List<Track>?) {
        trackList.clear()
        newTrackList?.let { trackList.addAll(it) }
        trackAdapter.notifyDataSetChanged()
        recyclerView.adapter = trackAdapter
        recyclerView.isVisible = trackList.isNotEmpty()
        hideHistory()
    }

    private var lastClickTime: Long = 0
    private var clickJob: Job? = null

    private fun onTrackSelected(track: Track) {
        val currentTime = System.currentTimeMillis()

        clickJob?.cancel()
        clickJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_INTERVAL)

            if (currentTime - lastClickTime >= CLICK_INTERVAL) {
                lastClickTime = currentTime
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, track)
                }
                context?.startActivity(intent)
            }
        }
    }

    companion object {
        private const val SAVED_TEXT_KEY = "savedText"
        private const val CLICK_INTERVAL = 1000L
        private const val TRACK_KEY = "track"
    }

    private fun onTrackSelectedHistory(track: Track) {
        viewModel.addTrackToHistory(track)
        onTrackSelected(track)
    }

    private fun displayMessageWithPlaceholder(message: String) {
        showMessage(message)
        placeholderImage.setImageResource(R.drawable.ic_placeholder_light)
    }

    private fun displayErrorMessage() {
        showMessage(getString(R.string.internet_issue))
        placeholderImage.setImageResource(R.drawable.ic_placeholder_no_light)
        showRefreshButton()
    }

    private fun showMessage(text: String) {
        placeholderText.text = text
        placeholderText.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        refreshButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        buttonClear.visibility = View.GONE
        historyTitle.visibility = View.GONE
    }

    private fun showRefreshButton() {
        refreshButton.visibility = View.VISIBLE
        isRefreshing = true
    }

    private fun updateHistoryVisibility() {
        val isHistoryNotEmpty = viewModel.searchHistory.value?.isNotEmpty() == true
        val isSearchFieldEmpty = editText.text.toString().isEmpty()
        val hasFocus = editText.hasFocus()

        if (isHistoryNotEmpty && isSearchFieldEmpty && hasFocus) {
            recyclerView.adapter = historyAdapter
            recyclerView.visibility = View.VISIBLE
            historyTitle.visibility = View.VISIBLE
            buttonClear.visibility = View.VISIBLE
        } else {
            historyTitle.visibility = View.GONE
            buttonClear.visibility = View.GONE
            recyclerView.adapter = trackAdapter
            if (trackList.isEmpty()) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun clearSearchField() {
        editText.text.clear()
        hideKeyboard()
        hideHistory()
        clearButton.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderText.isVisible = false
        refreshButton.visibility = View.GONE
        clearSearchFieldFocus()
    }

    private fun clearSearchHistory() {
        viewModel.clearSearchHistory()
    }

    private fun updateClearButtonVisibility() {
        clearButton.visibility = if (editText.text.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        toggleViewsVisibility(View.GONE)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        toggleViewsVisibility(View.VISIBLE)
    }

    private fun toggleViewsVisibility(visibility: Int) {
        if (recyclerView.adapter is TrackAdapter || recyclerView.adapter == null) {
            buttonClear.visibility = visibility
            historyTitle.visibility = visibility
            recyclerView.visibility = visibility
            placeholderText.visibility = visibility
            placeholderImage.visibility = visibility
            refreshButton.visibility = visibility
        }
    }

    private fun clearSearchFieldFocus() {
        editText.clearFocus()
    }

    private fun hideHistory() {
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        if (recyclerView.adapter is SearchHistoryAdapter) {
            recyclerView.visibility = View.GONE
        }
    }

    private fun updateUIForResults() {
        buttonClear.visibility = View.GONE
        historyTitle.visibility = View.GONE
        placeholderText.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        refreshButton.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = trackAdapter
    }
}