package com.example.myplaylistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myplaylistmaker.Creator
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.domain.domain.Track
import com.example.myplaylistmaker.ui.adapters.TrackAdapter
import com.example.myplaylistmaker.ui.media.MediaActivity

class SearchActivity : AppCompatActivity() {

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
    private val trackList = arrayListOf<Track>()
    private var savedText: String = ""
    private var isRefreshing = false
    private lateinit var searchHistory: SearchHistory
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()
        setupAdapters()
        setupListeners()

        if (savedInstanceState != null) {
            handleSavedInstanceState(savedInstanceState)
        }

        updateHistoryVisibility()
        updateClearButtonVisibility()
    }

    private fun initializeViews() {
        val arrow2Button: ImageView = findViewById(R.id.arrow2)
        editText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearButton)
        buttonClear = findViewById(R.id.button_clear)
        historyTitle = findViewById(R.id.historyTitle)
        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        placeholderText = findViewById(R.id.placeholderText)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.button_refresh)
        progressBar = findViewById(R.id.progressBar)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        arrow2Button.setOnClickListener { finish() }
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(trackList) { track -> onTrackSelectedHistory(track) }
        historyAdapter =
            SearchHistoryAdapter(searchHistory.getHistory()) { track -> onTrackSelected(track) }

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearButton.visibility = View.GONE
        progressBar.visibility = View.GONE
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
                if (s.toString().isNotEmpty()) {
                    hideHistory()
                    placeholderText.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    searchRunnable = Runnable {
                        if (s!!.isNotEmpty()) {
                            searchTracks(s.toString())
                        }
                    }
                    searchHandler.postDelayed(searchRunnable!!, 200)
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
            clearSearchField()
            updateHistoryVisibility()
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
        savedText = savedInstanceState.getString("savedText", "")
        editText.setText(savedText)
        if (savedText.isNotEmpty()) {
            searchTracks(savedText)
            updateUIForResults()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savedText", editText.text.toString())
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun searchTracks(keyword: String) {
        showProgressBar()
        Creator.provideSearchTracks(
            keyword,
            onSuccess = { foundTrack ->
                runOnUiThread {
                    hideProgressBar()
                    if (foundTrack.isNotEmpty()) {
                        updateTrackList(foundTrack)
                    } else {
                        displayMessageWithPlaceholder(
                            getString(R.string.nothing_to_show),
                            R.drawable.ic_placeholder_light,
                            R.drawable.ic_placeholder_night
                        )
                    }
                }
            },
            onError = {
                runOnUiThread {
                    hideProgressBar()
                    displayErrorMessage()
                }
            }
        )
    }

    private fun updateTrackList(newTrackList: List<Track>?) {
        trackList.clear()
        newTrackList?.let { trackList.addAll(it) }
        trackAdapter.notifyDataSetChanged()
        recyclerView.adapter = trackAdapter
        recyclerView.isVisible = trackList.isNotEmpty()
        hideHistory()
    }

    private var lastClickTime = 0L
    private fun onTrackSelected(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime
            val intent = Intent(this, MediaActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }
    }

    private fun onTrackSelectedHistory(track: Track) {
        searchHistory.addTrackToHistory(track)
        historyAdapter.updateHistoryList(searchHistory.getHistory())
        historyTitle.visibility = View.GONE
        onTrackSelected(track)
    }

    private fun displayMessageWithPlaceholder(message: String, lightImage: Int, darkImage: Int) {
        showMessage(message)
        setPlaceholderImage(lightImage, darkImage)
    }

    private fun displayErrorMessage() {
        showMessage(getString(R.string.internet_issue))
        setPlaceholderImage(R.drawable.ic_placeholder_no_light, R.drawable.ic_placeholder_no_night)
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

    private fun setPlaceholderImage(resourceLight: Int, resourceNight: Int) {
        placeholderImage.setImageResource(if (isDarkThemeEnabled()) resourceNight else resourceLight)
    }

    private fun showRefreshButton() {
        refreshButton.visibility = View.VISIBLE
        isRefreshing = true
    }

    private fun updateHistoryVisibility() {
        val isHistoryNotEmpty = searchHistory.getHistory().isNotEmpty()
        val isSearchFieldEmpty = editText.text.toString().isEmpty()
        val hasFocus = editText.hasFocus()

        if (isHistoryNotEmpty && isSearchFieldEmpty && hasFocus) {
            recyclerView.adapter = historyAdapter
            historyAdapter.updateHistoryList(searchHistory.getHistory())
            recyclerView.visibility = View.VISIBLE
            historyTitle.visibility = View.VISIBLE
            buttonClear.visibility = View.VISIBLE
        } else {
            historyTitle.visibility = View.GONE
            buttonClear.visibility = View.GONE
            if (recyclerView.adapter is SearchHistoryAdapter) {
                recyclerView.visibility = View.GONE
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
        searchHistory.clearHistory()
        historyAdapter.updateHistoryList(searchHistory.getHistory())
        updateHistoryVisibility()
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
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = trackAdapter
    }

    private fun isDarkThemeEnabled(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}