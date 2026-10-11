package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val interactorModule = module {

    single<ExecutorService>{
        Executors.newCachedThreadPool()
    }

    factory <TracksInteractor>{
        TracksInteractorImpl(
            repository = get(),
            executor = get()
        )
    }

    factory <SearchHistoryInteractor>{
        SearchHistoryInteractorImpl(get())
    }

    factory <PlayerInteractor> {
        PlayerInteractorImpl(mediaPlayer = get())
    }

    factory <SettingsInteractor>{
        SettingsInteractorImpl(get())
    }

    factory <ThemeInteractor>{
        ThemeInteractorImpl(get())
    }

    factory<SharingInteractor>{
        SharingInteractorImpl(get())
    }
}