package com.example.myplaylistmaker.data.dto

import android.content.SharedPreferences
import com.example.myplaylistmaker.domain.models.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val maxHistorySize = 10

    fun addTrackToHistory(trackDto: TrackDto) {
        val trackList = getHistory()
        val existingTrackIndex = trackList.indexOfFirst { it.trackId == trackDto.trackId }

        if (existingTrackIndex != -1) {
            trackList.removeAt(existingTrackIndex)
        }

        trackList.add(0, trackDto)

        if (trackList.size > maxHistorySize) {
            trackList.removeAt(maxHistorySize)
        }

        saveHistory(trackList)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    fun getHistory(): ArrayList<TrackDto> {
        val historyJson = sharedPreferences.getString(historyKey, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<TrackDto>>() {}.type
        return gson.fromJson(historyJson, type)
    }

    private fun saveHistory(trackDtoList: ArrayList<TrackDto>) {
        val historyJson = gson.toJson(trackDtoList)
        sharedPreferences.edit().putString(historyKey, historyJson).apply()
    }
}