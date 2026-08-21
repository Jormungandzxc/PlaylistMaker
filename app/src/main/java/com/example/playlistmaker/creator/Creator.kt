package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPrefs = context.getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    //Настройки темы
    private fun getThemeRepository(context: Context): ThemeRepository {
        val sharedPrefs = context.getSharedPreferences(App.PLAYLIST_MAKER_PREFS, Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    //Плеер
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl()
    }
}