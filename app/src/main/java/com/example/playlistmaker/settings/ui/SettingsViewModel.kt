package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.ThemeSettings
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel (
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val application: App
): ViewModel(){
    private val _themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    val themeSettingsLiveData: LiveData<ThemeSettings> = _themeSettingsLiveData

    init {
        _themeSettingsLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun switchTheme(isDark: Boolean){
        val newSettings = ThemeSettings(isDarkThemeEnabled = isDark)
        application.switchTheme(isDark)
        _themeSettingsLiveData.value = newSettings
    }

    fun shareApp(){
        sharingInteractor.shareApp()
    }

    fun openSupport(){
        sharingInteractor.openSupport()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }
}
