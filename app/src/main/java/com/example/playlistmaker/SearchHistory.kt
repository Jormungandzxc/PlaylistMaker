package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val SEARCH_HISTORY_KEY = "key_for_search_history"

class SearchHistory (private val sharedPreferences: SharedPreferences){
    private val gson = Gson()

    //Чтение списка из SP
    fun getHistory(): ArrayList<Track>{
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?:
        return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>(){}.type
        return  gson.fromJson(json, type)
    }

    //Добавление трека в историю
    fun addTrack(track: Track){
        val history = getHistory()

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

    //Очистка истории
    fun clearHistory(){
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun saveHistory(history: ArrayList<Track>){
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}