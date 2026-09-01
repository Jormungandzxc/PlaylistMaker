package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val SEARCH_HISTORY_KEY = "key_for_search_history"

class SearchHistoryRepositoryImpl (private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {

    private val gson = Gson()

    override fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаление старого индекса повторяющегося трека
        val existingTrackIndex = history.indexOfFirst{it.trackId == track.trackId}
        if (existingTrackIndex != -1){
            history.removeAt(existingTrackIndex)
        }

        history.add(0, track)

        if(history.size > 10){
            history.removeAt(history.lastIndex)
        }

        saveHistory(history)
    }

    override fun getHistory(): ArrayList<Track>{
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?:
        return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>(){}.type
        return  gson.fromJson(json, type)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}