package com.example.playlistmaker.di

import com.example.playlistmaker.App
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel{ (previewUrl: String) ->
        PlayerViewModel(
            previewUrl = previewUrl,
            get()
        )
    }

    viewModel{
        SettingsViewModel(
            get(),
            get(),
            androidApplication() as App
        )
    }
}