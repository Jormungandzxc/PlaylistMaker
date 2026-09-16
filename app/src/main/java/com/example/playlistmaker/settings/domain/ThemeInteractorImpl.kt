package com.example.playlistmaker.settings.domain

class ThemeInteractorImpl (private val repository: ThemeRepository): ThemeInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun saveTheme(editDarkTheme: Boolean) {
        return repository.saveTheme(editDarkTheme)
    }
}