package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl (private val sharedPreferences: SharedPreferences): ThemeRepository{
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(App.DARK_THEME_KEY, false)
    }

    override fun saveTheme(editDarkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(App.DARK_THEME_KEY, editDarkTheme)
            .apply()
    }
}