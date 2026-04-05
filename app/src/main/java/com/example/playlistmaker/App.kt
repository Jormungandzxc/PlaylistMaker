package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

     private var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFS, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }

    companion object {
        const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"
        const val DARK_THEME_KEY = "key_for_dark_theme"
    }
}