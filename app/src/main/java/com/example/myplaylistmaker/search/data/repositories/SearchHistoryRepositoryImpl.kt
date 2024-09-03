package com.example.myplaylistmaker.search.data.repositories

import android.content.SharedPreferences
import com.example.myplaylistmaker.search.domain.Track
import com.example.myplaylistmaker.search.domain.repositories.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val maxHistorySize = 10

    override fun addTrackToHistory(track: Track) {
        val trackList = getHistory()
        val existingTrackIndex = trackList.indexOfFirst { it.trackId == track.trackId }

        if (existingTrackIndex != -1) {
            trackList.removeAt(existingTrackIndex)
        }

        trackList.add(0, track)

        if (trackList.size > maxHistorySize) {
            trackList.removeAt(maxHistorySize)
        }
        saveHistory(trackList)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    override fun getHistory(): ArrayList<Track> {
        val historyJson = sharedPreferences.getString(historyKey, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(historyJson, type)
    }

    private fun saveHistory(trackList: ArrayList<Track>) {
        val historyJson = gson.toJson(trackList)
        sharedPreferences.edit().putString(historyKey, historyJson).apply()
    }
}