package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.ThemeSettings

const val THEME_SWITCHER_KEY = "key_for_dark_theme"

class SettingsRepositoryImpl (
    private val sharedPreferences: SharedPreferences
) : SettingsRepository{
    override fun getThemeSettings(): ThemeSettings {
        val isDarkTheme = sharedPreferences.getBoolean(THEME_SWITCHER_KEY, false)
        return ThemeSettings(isDarkThemeEnabled = isDarkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_SWITCHER_KEY, settings.isDarkThemeEnabled)
            .apply()
    }
}