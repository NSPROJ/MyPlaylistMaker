package com.example.myplaylistmaker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonClear: Button
    private lateinit var historyTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var apiService: ApiService
    private lateinit var placeholderText: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private val trackList = arrayListOf<Track>()
    private var savedText: String = ""
    private var isRefreshing = false
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrow2Button: ImageView = findViewById(R.id.arrow2)
        editText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearButton)
        buttonClear = findViewById(R.id.button_clear)
        historyTitle = findViewById(R.id.historyTitle)
        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        placeholderText = findViewById(R.id.placeholderText)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.button_refresh)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        trackAdapter = TrackAdapter(trackList) { track -> onTrackSelected(track) }
        historyAdapter = SearchHistoryAdapter(searchHistory.getHistory()) { track -> onTrackSelected(track) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        recyclerView.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearButton.visibility = View.GONE

        arrow2Button.setOnClickListener { finish() }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateHistoryVisibility()
            }
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = editText.text.toString()
                if (searchText.isNotEmpty()) {
                    searchTracks(searchText)
                    updateUIForResults()
                }
                true
            } else {
                false
            }
        }

        clearButton.setOnClickListener {
            editText.text.clear()
            hideKeyboard()
            hideHistory()
            clearButton.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderText.visibility = View.GONE
            clearSearchFieldFocus()
            updateHistoryVisibility()
        }

        buttonClear.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.updateHistoryList(searchHistory.getHistory())
            updateHistoryVisibility()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    hideHistory()
                } else {
                    updateHistoryVisibility()
                }
            }
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                clearButton.visibility = if (searchText.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })

        refreshButton.setOnClickListener {
            val searchText = editText.text.toString()
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }

        if (savedInstanceState != null) {
            savedText = savedInstanceState.getString("savedText", "")
            editText.setText(savedText)
            if (savedText.isNotEmpty()) {
                searchTracks(savedText)
                updateUIForResults()
            }
        }

        updateHistoryVisibility()
        updateClearButtonVisibility()
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
        sendRequest(keyword)
    }

    private fun sendRequest(keyword: String) {
        apiService.search(keyword)?.enqueue(object : Callback<Tracks?> {
            override fun onResponse(call: Call<Tracks?>, response: Response<Tracks?>) {
                if (response.isSuccessful) {
                    recyclerView.visibility = View.VISIBLE
                    placeholderText.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    isRefreshing = false

                    val results = response.body()?.results

                    if (!results.isNullOrEmpty()) {
                        updateTrackList(results)
                    } else {
                        showMessage(getString(R.string.nothing_to_show))
                        setPlaceholderImage(R.drawable.ic_placeholder_light, R.drawable.ic_placeholder_night)
                    }
                } else {
                    showRefreshButton()
                    showMessage(getString(R.string.internet_issue))
                    setPlaceholderImage(R.drawable.ic_placeholder_no_light, R.drawable.ic_placeholder_no_night)
                }
            }

            override fun onFailure(call: Call<Tracks?>, t: Throwable) {
                showRefreshButton()
                showMessage(getString(R.string.internet_issue))
                setPlaceholderImage(R.drawable.ic_placeholder_no_light, R.drawable.ic_placeholder_no_night)
            }
        })
    }

    private fun updateTrackList(newTrackList: List<Track>?) {
        trackList.clear()
        newTrackList?.let {
            trackList.addAll(it)
        }
        trackAdapter.notifyDataSetChanged()
        recyclerView.visibility = if (trackList.isEmpty()) View.GONE else View.VISIBLE
        hideHistory()
    }

    private fun showMessage(text: String) {
        placeholderText.text = text
        placeholderText.visibility = View.VISIBLE
        placeholderImage.visibility = View.VISIBLE
        buttonClear.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun setPlaceholderImage(resourceLight: Int, resourceNight: Int) {
        if (isDarkThemeEnabled()) {
            placeholderImage.setImageResource(resourceNight)
        } else {
            placeholderImage.setImageResource(resourceLight)
        }
    }

    private fun showRefreshButton() {
        refreshButton.visibility = View.VISIBLE
        buttonClear.visibility = View.GONE
        isRefreshing = true
    }

    private fun onTrackSelected(track: Track) {
        searchHistory.addTrackToHistory(track)
        historyAdapter.updateHistoryList(searchHistory.getHistory())
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
    }

    private fun updateHistoryVisibility() {
        val isHistoryNotEmpty = searchHistory.getHistory().isNotEmpty()
        val isSearchFieldEmpty = editText.text.toString().isEmpty()
        val hasFocus = editText.hasFocus()

        historyTitle.visibility = if (isHistoryNotEmpty && isSearchFieldEmpty && hasFocus) View.VISIBLE else View.GONE
        buttonClear.visibility = if (isHistoryNotEmpty && isSearchFieldEmpty && hasFocus) View.VISIBLE else View.GONE

        if (isHistoryNotEmpty && isSearchFieldEmpty && hasFocus) {
            recyclerView.adapter = historyAdapter
            historyAdapter.updateHistoryList(searchHistory.getHistory())
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateClearButtonVisibility() {
        clearButton.visibility = if (editText.text.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateUIForResults() {
        buttonClear.visibility = View.GONE
        historyTitle.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = trackAdapter
    }

    private fun clearSearchFieldFocus() {
        editText.clearFocus()
        updateHistoryVisibility()
    }

    private fun hideHistory() {
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        if (recyclerView.adapter is SearchHistoryAdapter) {
            recyclerView.visibility = View.GONE
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}