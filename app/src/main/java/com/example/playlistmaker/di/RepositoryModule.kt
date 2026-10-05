package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.ThemeRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory <SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    factory <TracksRepository>{
        TracksRepositoryImpl(get())
    }

    factory<SettingsRepository>{
        SettingsRepositoryImpl(get())
    }

    factory<ThemeRepository>{
        ThemeRepositoryImpl(get())
    }
}