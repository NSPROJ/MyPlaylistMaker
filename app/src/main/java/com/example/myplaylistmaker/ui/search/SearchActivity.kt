package com.example.myplaylistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
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
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.data.ApiRepositoryImpl
import com.example.myplaylistmaker.data.dto.SearchHistory
import com.example.myplaylistmaker.domain.models.TrackDto
import com.example.myplaylistmaker.domain.models.api.SearchInteractor
import com.example.myplaylistmaker.presentation.TrackAdapter
import com.example.myplaylistmaker.ui.media.MediaActivity

class SearchActivity() : AppCompatActivity(), Parcelable {

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
    private val trackList = arrayListOf<TrackDto>()
    private var lastClickTime: Long = 0
    private lateinit var searchHistory: SearchHistory
    private val searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private lateinit var searchInteractor: SearchInteractor

    constructor(parcel: Parcel) : this() {
        lastClickTime = parcel.readLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()
        setupAdapters()
        setupListeners()

        val apiRepository = ApiRepositoryImpl()
        searchInteractor = SearchInteractor(apiRepository)

        savedInstanceState?.let { handleSavedInstanceState(it) }

        updateHistoryVisibility()
        updateClearButtonVisibility()
    }

    private fun initializeViews() {
        findViewById<ImageView>(R.id.arrow2).setOnClickListener { finish() }

        editText = findViewById<EditText>(R.id.editText).apply {
            setOnFocusChangeListener { _, hasFocus -> if (hasFocus) updateHistoryVisibility() else hideHistory() }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    updateClearButtonVisibility()
                    if (s.toString().isNotEmpty()) performSearchWithDelay(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        clearButton =
            findViewById<ImageView>(R.id.clearButton).apply { setOnClickListener { clearSearchField() } }
        buttonClear =
            findViewById<Button>(R.id.button_clear).apply { setOnClickListener { clearSearchHistory() } }
        historyTitle = findViewById(R.id.historyTitle)
        recyclerView = findViewById<RecyclerView>(R.id.searchResultsRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
        placeholderText = findViewById(R.id.placeholderText)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton =
            findViewById<Button>(R.id.button_refresh).apply { setOnClickListener { refreshSearch() } }
        progressBar = findViewById(R.id.progressBar)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(trackList) { track -> onTrackSelected(track) }
        historyAdapter =
            SearchHistoryAdapter(searchHistory.getHistory()) { track -> onTrackSelectedHistory(track) }
        recyclerView.visibility = View.GONE
        historyTitle.visibility = View.GONE
        buttonClear.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun setupListeners() {
        clearButton.setOnClickListener { clearSearchField() }
        buttonClear.setOnClickListener { clearSearchHistory() }
        refreshButton.setOnClickListener { refreshSearch() }
    }

    private fun handleSavedInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.getString("savedText")?.let {
            editText.setText(it)
            if (it.isNotEmpty()) searchTracks(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savedText", editText.text.toString())
    }

    private fun searchTracks(keyword: String) {
        showProgressBar()
        searchInteractor.search(keyword) { trackList ->
            hideProgressBar()
            if (trackList.isNotEmpty()) {
                updateTrackList(trackList)
            } else {
                displayMessageWithPlaceholder(
                    getString(R.string.nothing_to_show),
                    R.drawable.ic_placeholder_light, R.drawable.ic_placeholder_night
                )
            }
        }
    }
    private fun updateTrackList(newTrackList: List<TrackDto>) {
        trackList.apply {
            clear()
            addAll(newTrackList)
        }
        trackAdapter.notifyDataSetChanged()
        recyclerView.adapter = trackAdapter
        recyclerView.isVisible = trackList.isNotEmpty()
        hideHistory()
    }

    private fun onTrackSelected(track: TrackDto) {
        if (System.currentTimeMillis() - lastClickTime > 1000) {
            lastClickTime = System.currentTimeMillis()
            startActivity(Intent(this, MediaActivity::class.java).apply {
                putExtra(
                    "track",
                    track
                )
            })
        }
    }

    private fun onTrackSelectedHistory(track: TrackDto) {
        searchHistory.addTrackToHistory(track)
        historyAdapter.updateHistoryList(searchHistory.getHistory())
        historyTitle.visibility = View.GONE
        onTrackSelected(track)
    }

    private fun displayMessageWithPlaceholder(message: String, lightImage: Int, darkImage: Int) {
        showMessage(message)
        setPlaceholderImage(lightImage, darkImage)
    }

    private fun showMessage(text: String) {
        placeholderText.apply {
            this.text = text
            visibility = View.VISIBLE
        }
        placeholderImage.visibility = View.VISIBLE
        toggleViewsVisibility(View.GONE)
    }

    private fun setPlaceholderImage(resourceLight: Int, resourceNight: Int) {
        placeholderImage.setImageResource(if (isDarkThemeEnabled()) resourceNight else resourceLight)
    }

    private fun updateHistoryVisibility() {
        if (searchHistory.getHistory()
                .isNotEmpty() && editText.text.isEmpty() && editText.hasFocus()
        ) {
            historyAdapter.updateHistoryList(searchHistory.getHistory())
            recyclerView.adapter = historyAdapter
            historyTitle.visibility = View.VISIBLE
            buttonClear.visibility = View.VISIBLE
        } else {
            hideHistory()
        }
    }

    private fun clearSearchField() {
        editText.apply {
            text.clear()
            clearFocus()
        }.also {
            hideKeyboard()
            hideHistory()
            clearButton.visibility = View.GONE
        }
    }

    private fun clearSearchHistory() {
        searchHistory.clearHistory()
        historyAdapter.updateHistoryList(searchHistory.getHistory())
        updateHistoryVisibility()
    }

    private fun refreshSearch() {
        val searchText = editText.text.toString()
        if (searchText.isNotEmpty()) searchTracks(searchText)
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
            recyclerView.visibility = View.VISIBLE
            buttonClear.visibility = visibility
            recyclerView.visibility = visibility
            historyTitle.visibility = visibility
            placeholderText.visibility = visibility
            placeholderImage.visibility = visibility
        }
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            editText.windowToken,
            0
        )
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

    private fun performSearchWithDelay(query: String) {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable { searchTracks(query) }
        searchHandler.postDelayed(searchRunnable!!, 1000)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(lastClickTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchActivity> {
        override fun createFromParcel(parcel: Parcel): SearchActivity {
            return SearchActivity(parcel)
        }

        override fun newArray(size: Int): Array<SearchActivity?> {
            return arrayOfNulls(size)
        }
    }
}